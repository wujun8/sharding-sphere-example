package io.shardingsphere.example.spring.namespace.mybatis.fixtrue.repository;

import io.shardingsphere.example.spring.namespace.mybatis.fixtrue.result.GroupSum;

import java.util.List;

public interface AggregateRepository {

    long sumSelectByUser(int user_id);

    List<GroupSum> sumSelectByUserGroup();

    long countSelectByUser();
    long countSelectStatus();
}
