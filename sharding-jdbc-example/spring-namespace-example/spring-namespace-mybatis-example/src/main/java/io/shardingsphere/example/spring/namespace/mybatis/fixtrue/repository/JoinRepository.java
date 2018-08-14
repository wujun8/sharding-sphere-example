package io.shardingsphere.example.spring.namespace.mybatis.fixtrue.repository;

import io.shardingsphere.example.spring.namespace.mybatis.fixtrue.entity.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JoinRepository {

    List<Order> selectJoin();
    List<Order> selectJoinByUser(@Param("user_id") int userId);
}
