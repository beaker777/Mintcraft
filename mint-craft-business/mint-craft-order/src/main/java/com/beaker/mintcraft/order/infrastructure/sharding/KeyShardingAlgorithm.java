package com.beaker.mintcraft.order.infrastructure.sharding;

import com.beaker.mintcraft.order.sharding.id.DistributeID;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.hint.HintShardingAlgorithm;
import org.apache.shardingsphere.sharding.api.sharding.hint.HintShardingValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @Author beaker
 * @Date 2026/5/13 14:15
 * @Description
 */
public class KeyShardingAlgorithm implements ComplexKeysShardingAlgorithm<String>, HintShardingAlgorithm<String> {

    private static Logger logger = LoggerFactory.getLogger(KeyShardingAlgorithm.class);

    private Properties props;

    private static final String PROP_MAIN_COLUM = "mainColum";

    private static final String PROP_TABLE_COUNT = "tableCount";

    @Override
    public Properties getProps() {
        return props;
    }

    @Override
    public void init(Properties props) {
        this.props = props;
    }


    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, ComplexKeysShardingValue<String> shardingValue) {
        Collection<String> result = new HashSet<>();

        // 获取主分片字段名 userId
        String mainColum = props.getProperty(PROP_MAIN_COLUM);
        // 在 SQL 语句中获取主分片字段的值 userId: 1
        Collection<String> mainColumns = shardingValue.getColumnNameAndShardingValuesMap().get(mainColum);

        // 如果 userId 不为空, 直接计算出分表结果
        if (CollectionUtils.isNotEmpty(mainColumns)) {
            for (String colum : mainColumns) {
                String shardingTarget = calculateShardingTarget(colum);
                result.add(shardingTarget);
            }

            return getMatchedTables(result, availableTargetNames);
        }

        // 移除 userId 获取其他字段名
        shardingValue.getColumnNameAndShardingValuesMap().remove(mainColum);
        Collection<String> otherColumns = shardingValue.getColumnNameAndShardingValuesMap().keySet();

        if (CollectionUtils.isNotEmpty(otherColumns)) {
            for (String colum : otherColumns) {
                Collection<String> otherColumValues = shardingValue.getColumnNameAndShardingValuesMap().get(colum);
                for (String value : otherColumValues) {
                    // 根据订单 id 获取到分表结果
                    String shardingTarget = extractShardingTarget(value);
                    result.add(shardingTarget);
                }
            }

            return getMatchedTables(result, availableTargetNames);
        }

        return null;
    }

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, HintShardingValue<String> shardingValue) {
        return List.of();
    }

    private String calculateShardingTarget(String buyerId) {
        // 获取表数
        String tableCount = props.getProperty(PROP_TABLE_COUNT);
        // 得到分表结果
        return DistributeID.getShardingTable(buyerId, Integer.parseInt(tableCount));
    }

    private Collection<String> getMatchedTables(Collection<String> results, Collection<String> availableTargetNames) {
        // 获取到匹配到的表全名
        Collection<String> matchedTables = new HashSet<>();
        for (String result : results) {
            matchedTables.addAll(availableTargetNames.parallelStream()
                    .filter(each -> each.endsWith(result)).collect(Collectors.toSet()));
        }

        return matchedTables;
    }

    private String extractShardingTarget(String orderId) {
        return DistributeID.getShardingTable(orderId);
    }

}
