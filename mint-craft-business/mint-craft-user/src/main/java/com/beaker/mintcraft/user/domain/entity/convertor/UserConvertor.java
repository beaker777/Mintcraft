package com.beaker.mintcraft.user.domain.entity.convertor;

import com.beaker.mintcraft.api.user.response.data.BasicUserInfo;
import com.beaker.mintcraft.api.user.response.data.UserInfo;
import com.beaker.mintcraft.user.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @Author beaker
 * @Date 2026/5/5 22:27
 * @Description 用户转换类
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserConvertor {

    UserConvertor INSTANCE = Mappers.getMapper(UserConvertor.class);

    /**
     * 转换成 VO
     *
     * @param request
     * @return
     */
    @Mapping(target = "userId", source = "request.id")
    @Mapping(target = "createTime", source = "request.gmtCreate")
    public UserInfo mapToVO(User request);

    /**
     * 转换为VO
     *
     * @param request
     * @return
     */
    public List<UserInfo> mapToVo(List<User> request);

    /**
     * 转换为简单的VO
     * @param request
     * @return
     */
    @Mapping(target = "userId", source = "request.id")
    public BasicUserInfo mapToBasicVo(User request);

    /**
     * 转换为实体
     *
     * @param request
     * @return
     */
    @Mapping(target = "id", source = "request.userId")
    public User mapToEntity(UserInfo request);
}
