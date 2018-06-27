package io.shardingsphere.example.spring.namespace.mybatis.fixtrue.algorithm;

import com.google.common.collect.BoundType;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import io.shardingsphere.core.api.algorithm.sharding.ListShardingValue;
import io.shardingsphere.core.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.core.api.algorithm.sharding.RangeShardingValue;
import io.shardingsphere.core.api.algorithm.sharding.ShardingValue;
import io.shardingsphere.core.api.algorithm.sharding.complex.ComplexKeysShardingAlgorithm;
import io.shardingsphere.core.exception.ShardingConfigurationException;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class BillShardingAlgorithm implements ComplexKeysShardingAlgorithm {
    public static final String TABLE_NAME = "t_fi_bill1";

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, Collection<ShardingValue> shardingValues) {
        for (ShardingValue shardingValue : shardingValues) {
            if (!TABLE_NAME.equals(shardingValue.getLogicTableName())) {
                throw new ShardingConfigurationException(String.format("%s only apply for table %s and binding tables",
                        getClass().getSimpleName(), TABLE_NAME));
            }
            List<String> targetTables = Lists.newArrayList();
            List<Integer> years = Lists.newArrayList();
            if ("gmtCreate".equals(shardingValue.getColumnName())) {
                if (shardingValue instanceof PreciseShardingValue) {
                    // =
                    String createDateStr = (String) ((PreciseShardingValue) shardingValue).getValue();
                    int year = getYear(createDateStr);
                    years.add(year);
                } else if (shardingValue instanceof RangeShardingValue) {
                    // <=>
                    // BETWEEN
                    Range range = ((RangeShardingValue) shardingValue).getValueRange();
                    if (!range.isEmpty()) {
                        if (range.hasLowerBound() && range.hasUpperBound()) { // [start, end]
                            String startDateStr = (String) range.lowerEndpoint();
                            String endDateStr = (String) range.upperEndpoint();
                            int startYear = getYear(startDateStr);
                            int endYear = getYear(endDateStr);
                            for (int i = startYear; i <= endYear; i++) {
                                years.add(i);
                            }
                        } else if (range.hasLowerBound()) { // >
                            String startDateStr = (String) range.lowerEndpoint();
                            //todo
                        } else if (range.hasUpperBound()) { // <
                            //todo
                        }
                    }
                } else if (shardingValue instanceof ListShardingValue) {
                    // IN
                }
            }
            // payerId 收款 -> 运营商

        }
        return null;
    }

    private int getYear(String dateString) {
        try {
            Date createDate = DateUtils.parseDate(dateString, "%Y-%m-%d");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(createDate);
            return calendar.get(Calendar.YEAR);
        } catch (ParseException e) {
            // nothing
        }
        return Calendar.getInstance().get(Calendar.YEAR);// default current year
    }
}
