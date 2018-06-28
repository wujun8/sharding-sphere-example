package io.shardingsphere.example.spring.namespace.mybatis.fixtrue.algorithm;

import com.google.common.collect.*;
import io.shardingsphere.core.api.algorithm.sharding.ListShardingValue;
import io.shardingsphere.core.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.core.api.algorithm.sharding.RangeShardingValue;
import io.shardingsphere.core.api.algorithm.sharding.ShardingValue;
import io.shardingsphere.core.api.algorithm.sharding.complex.ComplexKeysShardingAlgorithm;
import io.shardingsphere.core.exception.ShardingConfigurationException;
import io.shardingsphere.core.exception.ShardingException;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BillShardingAlgorithm implements ComplexKeysShardingAlgorithm {
    public static final String TABLE_NAME = "t_fi_bill1";
    private static final Pattern TABLE_PATTERN = Pattern.compile(TABLE_NAME + "_(\\d{4})_(\\d{1,2})");

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, Collection<ShardingValue> shardingValues) {
        Set<Integer> availableYears = Sets.newHashSet();
        Set<Integer> availablePaySections = Sets.newHashSet();
        List<String> targetTables = Lists.newArrayList();
        for (String availableTable : availableTargetNames) {
            Matcher matcher = TABLE_PATTERN.matcher(availableTable);
            if (!matcher.matches() || matcher.groupCount() != 3) {
                throw new ShardingConfigurationException(String.format("%s only apply for table %s, check the routing rule",
                        getClass().getSimpleName(), TABLE_NAME));
            }
            availableYears.add(Integer.valueOf(matcher.group(1)));
            availablePaySections.add(Integer.valueOf(matcher.group(2)));
        }
        // default all available years
        List<Integer> targetYears = Lists.newArrayList(availableYears);
        List<Integer> targetPaySections = Lists.newArrayList(availablePaySections);
        for (ShardingValue shardingValue : shardingValues) {
            if (!TABLE_NAME.equals(shardingValue.getLogicTableName())) {
                throw new ShardingConfigurationException(String.format("%s only apply for table %s and binding tables",
                        getClass().getSimpleName(), TABLE_NAME));
            }
            if ("gmtCreate".equals(shardingValue.getColumnName())) {
                ImmutableSortedSet<Integer> years = ContiguousSet.of();
                if (shardingValue instanceof PreciseShardingValue) { // =
                    String createDateStr = (String) ((PreciseShardingValue) shardingValue).getValue();
                    int year = getYear(createDateStr);
                    if (!availableYears.contains(year)) {
                        throw new ShardingException(String.format("can't route, available value: %s, sharding value: %d", availableYears, year));
                    }
                    years = ContiguousSet.of(year);
                } else if (shardingValue instanceof RangeShardingValue) { // >=<  BETWEEN
                    Range range = ((RangeShardingValue) shardingValue).getValueRange();
                    if (!range.isEmpty()) {
                        if (range.hasLowerBound() && range.hasUpperBound()) { // [start, end]
                            String startDateStr = (String) range.lowerEndpoint();
                            String endDateStr = (String) range.upperEndpoint();
                            int startYear = getYear(startDateStr);
                            int endYear = getYear(endDateStr);
                            years = ContiguousSet.create(Range.closed(startYear, endYear), DiscreteDomain.integers());
                        } else if (range.hasLowerBound()) { // >
                            String startDateStr = (String) range.lowerEndpoint();
                            int startYear = getYear(startDateStr);
                            years = ContiguousSet.create(Range.atLeast(startYear), DiscreteDomain.integers());
                        } else if (range.hasUpperBound()) { // <
                            String endDateStr = (String) range.upperEndpoint();
                            int endYear = getYear(endDateStr);
                            years = ContiguousSet.create(Range.atMost(endYear), DiscreteDomain.integers());
                        }
                        if (!availableYears.containsAll(years)) {
                            throw new ShardingException(String.format("can't route, available value: %s, sharding value: %s", availableYears, years));
                        }
                    }
                } else if (shardingValue instanceof ListShardingValue) { // IN
                    Collection createDates = ((ListShardingValue) shardingValue).getValues();
                    List<Integer> yearValues = Lists.newArrayList();
                    for (Object dateString : createDates) {
                        yearValues.add(getYear((String) dateString));
                    }
                    if (!availableYears.containsAll(yearValues)) {
                        throw new ShardingException(String.format("can't route, available value: %s, sharding value: %s", availableYears, yearValues));
                    }
                    years = ContiguousSet.copyOf(yearValues);
                }
                if (!years.isEmpty()) {
                    targetYears = Lists.newArrayList(Sets.intersection(years, availableYears).iterator());
                }
            }
            // payeeId 收款 -> 运营商
            else if ("payeeId".equals(shardingValue.getColumnName())) {
                if (shardingValue instanceof PreciseShardingValue) { // =
                    int payeeId = (Integer) ((PreciseShardingValue) shardingValue).getValue();
                    int paySection = payeeId % 2;
                    if (!availablePaySections.contains(payeeId)) {
                        throw new ShardingException(String.format("can't route, available value: %s, sharding value: %d", availableYears, payeeId));
                    }
                    targetPaySections = Lists.newArrayList(payeeId);
                } else if (shardingValue instanceof RangeShardingValue) { // >=<  BETWEEN
                    Range range = ((RangeShardingValue) shardingValue).getValueRange();
                    throw new ShardingException(String.format("can't route, not support sharding value: %s", range));
                } else if (shardingValue instanceof ListShardingValue) { // IN
                    Collection values = ((ListShardingValue) shardingValue).getValues();
                    List<Integer> payeeIds = Lists.newArrayList();
                    for (Object value : values) {
                        payeeIds.add((Integer) value);
                    }
                }
            }

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
