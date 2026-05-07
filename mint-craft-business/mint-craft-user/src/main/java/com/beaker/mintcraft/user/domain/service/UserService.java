package com.beaker.mintcraft.user.domain.service;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alicp.jetcache.anno.CacheRefresh;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beaker.mintcraft.api.user.constant.UserOperateTypeEnum;
import com.beaker.mintcraft.api.user.response.UserOperatorResponse;
import com.beaker.mintcraft.base.exception.biz.BizException;
import com.beaker.mintcraft.base.exception.biz.RepoErrorCode;
import com.beaker.mintcraft.lock.DistributeLock;
import com.beaker.mintcraft.user.domain.entity.User;
import com.beaker.mintcraft.user.infrastructure.exception.UserErrorCode;
import com.beaker.mintcraft.user.infrastructure.exception.UserException;
import com.beaker.mintcraft.user.infrastructure.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

import static com.beaker.mintcraft.user.infrastructure.exception.UserErrorCode.DUPLICATE_TELEPHONE_NUMBER;

/**
 * @Author beaker
 * @Date 2026/4/27 21:37
 * @Description 用户服务
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> implements InitializingBean {

    private static final String DEFAULT_NICK_NAME_PREFIX = "藏家_";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserOperateStreamService userOperateStreamService;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 用户名布隆过滤器
     */
    private RBloomFilter<String> nickNameBloomFilter;

    /**
     * 邀请码布隆过滤器
     */
    private RBloomFilter<String> inviteCodeBloomFilter;

    /**
     * 通过用户 id 查询详细信息
     *
     * @param userId
     * @return
     */
    @Cached(name = ":user:cache:id:", cacheType = CacheType.BOTH, key = "#userId", cacheNullValue = true)
    @CacheRefresh(refresh = 15, timeUnit = TimeUnit.MINUTES)
    public User findById(Long userId) {
        return userMapper.findById(userId);
    }

    /**
     * 通过手机号查询用户信息
     *
     * @param telephone
     * @return
     */
    public User findByTelephone(String telephone) {
        return userMapper.findByTelephone(telephone);
    }

    /**
     * 通过手机号和密码查询用户信息
     *
     * @param telephone
     * @param password
     * @return
     */
    public User findByTelephoneAndPassword(String telephone, String password) {
        return userMapper.findByTelephoneAndPasswordHash(telephone, DigestUtil.md5Hex(password));
    }

    @DistributeLock(scene = "USER_REGISTER", keyExpression = "#telephone")
    @Transactional(rollbackFor = Exception.class)
    public UserOperatorResponse register(String telephone, String inviteCode) {
        String defaultNickName;
        String randomString;

        // 使用布隆过滤器防止缓存穿透
        do {
            randomString = RandomUtil.randomString(6).toUpperCase();

            // 默认用户名: 前缀 + 随机字符串 + 手机号后四位
            defaultNickName = DEFAULT_NICK_NAME_PREFIX + randomString + telephone.substring(7, 11);
        } while (nickNameExist(defaultNickName) || inviteCodeExist(inviteCode));

        // 根据邀请码获取到邀请者 id
        String inviterId = null;
        if (StringUtils.isNoneBlank(inviteCode)) {
            User inviter = userMapper.findByInviteCode(inviteCode);

            if (inviter != null) {
                inviterId = inviter.getId().toString();
            }
        }

        // fixme: 在极端情况下, 这里可能存在邀请码重复的情况, 代码不做特殊处理, 靠唯一索引保证
        User user = register(telephone, defaultNickName, telephone, randomString, inviterId);
        Assert.notNull(user, UserErrorCode.USER_OPERATE_FAILED.getCode());

        // 更新布隆过滤器
        addNickName(defaultNickName);
        addInviteCode(randomString);
        // TODO 在这里需要更新邀请者的排名
        // TODO 在这里需要更新用户的 redis 缓存

        // 加入流水
        long streamResult = userOperateStreamService.insertStream(user, UserOperateTypeEnum.REGISTER);
        Assert.notNull(streamResult, () -> new BizException(RepoErrorCode.INSERT_FAILED));

        UserOperatorResponse userOperatorResponse = new UserOperatorResponse();
        userOperatorResponse.setSuccess(true);

        return userOperatorResponse;
    }

    /**
     * 注册
     *
     * @param telephone
     * @param nickName
     * @param password
     * @param inviteCode
     * @param inviterId
     * @return
     */
    public User register(String telephone, String nickName, String password, String inviteCode, String inviterId) {
        // 幂等校验
        if (userMapper.findByTelephone(telephone) != null) {
            throw new UserException(DUPLICATE_TELEPHONE_NUMBER);
        }

        User user = new User();
        user.register(telephone, nickName, password, inviteCode, inviterId);

        return save(user) ? user : null;
    }

    public boolean nickNameExist(String nickName) {
        // 如果布隆过滤器认为已经存在, 进行二次校验
        if (nickNameBloomFilter != null && nickNameBloomFilter.contains(nickName)) {
            return userMapper.findByNickName(nickName) != null;
        }

        return false;
    }

    public boolean inviteCodeExist(String inviteCode) {
        if (inviteCode != null && inviteCodeBloomFilter.contains(inviteCode)) {
            return userMapper.findByInviteCode(inviteCode) != null;
        }

        return false;
    }

    private boolean addNickName(String nickName) {
        if (nickName != null) {
            return nickNameBloomFilter != null && nickNameBloomFilter.add(nickName);
        }

        return false;
    }

    private boolean addInviteCode(String inviteCode) {
        if (inviteCode != null) {
            return inviteCodeBloomFilter != null && inviteCodeBloomFilter.add(inviteCode);
        }

        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        nickNameBloomFilter = redissonClient.getBloomFilter("nickName");
        if (nickNameBloomFilter != null && !nickNameBloomFilter.isExists()) {
            nickNameBloomFilter.tryInit(100000L, 0.01);
        }

        inviteCodeBloomFilter = redissonClient.getBloomFilter("inviteCode");
        if (inviteCodeBloomFilter != null && !inviteCodeBloomFilter.isExists()) {
            inviteCodeBloomFilter.tryInit(100000L, 0.01);
        }
    }
}
