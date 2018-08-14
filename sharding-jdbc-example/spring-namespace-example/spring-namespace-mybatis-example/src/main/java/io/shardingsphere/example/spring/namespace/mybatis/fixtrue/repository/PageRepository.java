package io.shardingsphere.example.spring.namespace.mybatis.fixtrue.repository;

import io.shardingsphere.example.spring.namespace.mybatis.fixtrue.entity.Order;
import io.shardingsphere.example.spring.namespace.mybatis.fixtrue.result.GroupSum;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PageRepository {

    List<Order> selectOrderPage(@Param("limitStart") int limitStart, @Param("pageSize") int pageSize);
}
