/*
 * Copyright 2016-2018 shardingsphere.io.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package io.shardingsphere.example.spring.namespace.mybatis.fixtrue.service;

import io.shardingsphere.example.spring.namespace.mybatis.fixtrue.entity.Order;
import io.shardingsphere.example.spring.namespace.mybatis.fixtrue.entity.OrderItem;
import io.shardingsphere.example.spring.namespace.mybatis.fixtrue.repository.AggregateRepository;
import io.shardingsphere.example.spring.namespace.mybatis.fixtrue.repository.OrderItemRepository;
import io.shardingsphere.example.spring.namespace.mybatis.fixtrue.repository.OrderRepository;
import io.shardingsphere.example.spring.namespace.mybatis.fixtrue.repository.PageRepository;
import io.shardingsphere.example.spring.namespace.mybatis.fixtrue.result.GroupSum;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class DemoService {
    static Logger logger = LoggerFactory.getLogger(DemoService.class);
    
    @Resource
    private OrderRepository orderRepository;
    @Resource
    private OrderItemRepository orderItemRepository;
    @Resource
    private AggregateRepository aggregateRepository;
    @Resource
    private PageRepository pageRepository;

    private ExecutorService executorService = Executors.newFixedThreadPool(8);
    
    public void demo() {
        orderRepository.createIfNotExistsTable();
        orderItemRepository.createIfNotExistsTable();
        orderRepository.truncateTable();
        orderItemRepository.truncateTable();
        List<Long> orderIds = new ArrayList<>(10);
        System.out.println("1.Insert--------------");
        for (int i = 0; i < 10; i++) {
            Order order = new Order();
            order.setUserId(51);
            order.setStatus("INSERT_TEST");
            orderRepository.insert(order);
            long orderId = order.getOrderId();
            orderIds.add(orderId);
            
            OrderItem item = new OrderItem();
            item.setOrderId(orderId);
            item.setUserId(51);
            item.setStatus("INSERT_TEST");
            orderItemRepository.insert(item);
        }
        System.out.println(orderItemRepository.selectAll());
        System.out.println("2.Delete--------------");
        for (Long each : orderIds) {
            orderRepository.delete(each);
            orderItemRepository.delete(each);
        }
        System.out.println(orderItemRepository.selectAll());
        orderItemRepository.dropTable();
        orderRepository.dropTable();
    }

    public void test() {
        orderRepository.createIfNotExistsTable();
        orderItemRepository.createIfNotExistsTable();

        for (int i = 0; i < 200; i++) {
            executorService.submit(new Runnable() {
                @Override public void run() {
                    int userId = RandomUtils.nextInt(1, 1000);
                    insert(userId);
                }
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //        System.out.println(orderItemRepository.selectAll());
    }

    private void insert(int userId) {

        System.out.println("Insert--------------");
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus("INSERT_TEST");
        orderRepository.insert(order);
        long orderId = order.getOrderId();

        OrderItem item = new OrderItem();
        item.setOrderId(orderId);
        item.setUserId(userId);
        item.setStatus("INSERT_TEST");
        orderItemRepository.insert(item);
    }

    public void selectAgg() {
        int userId = 104;
        long total = aggregateRepository.sumSelectByUser(userId);
        System.out.println(String.format("user(%s): %s ", userId, total));

        List<GroupSum> groupSums = aggregateRepository.sumSelectByUserGroup();
        System.out.println(String.format("user group sum[%s]: %s ", groupSums.size(), groupSums));

        long count = aggregateRepository.countSelectStatus();
        System.out.println(String.format("countSelectStatus: %s ", count));
    }

    public void selectPage() {
        List<Order> orders = pageRepository.selectOrderPage(4000000, 10);
        logger.info("orders: {}", orders);
        orders = pageRepository.selectOrderPage(4000010, 10);
        logger.info("orders: {}", orders);
    }

    public void buildData() {
        for (int i = 0; i < 1000; i++) {
            Order order = new Order();
            order.setUserId(100);
            order.setStatus("INSERT_TEST" + i);
            orderRepository.insert(order);
        }
        // INSERT INTO t_order_0 (user_id, `status`) SELECT user_id, `status` FROM t_order_0;

        for (int i = 0; i < 100; i++) {
            Order order = new Order();
            order.setUserId(101);
            order.setStatus("INSERT_TEST" + i);
            orderRepository.insert(order);
        }
    }
}
