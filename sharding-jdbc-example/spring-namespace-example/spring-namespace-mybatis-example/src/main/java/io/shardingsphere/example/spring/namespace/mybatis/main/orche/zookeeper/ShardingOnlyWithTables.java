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

package io.shardingsphere.example.spring.namespace.mybatis.main.orche.zookeeper;

import io.shardingsphere.example.spring.namespace.mybatis.fixtrue.service.DemoService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ShardingOnlyWithTables {

    private static final boolean LOAD_CONFIG_FROM_REG_CENTER = false;

    public static void main(final String[] args) {
        String configFileName = LOAD_CONFIG_FROM_REG_CENTER ? "META-INF/orche/zookeeper/cloud/shardingTables.xml" : "META-INF/orche/zookeeper/local/shardingTables.xml";
        try (ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(configFileName)) {
            DemoService demoService = applicationContext.getBean(DemoService.class);
//            demoService.demo();
//            demoService.test();
//            demoService.selectAgg();
            demoService.selectPage();
        }
    }
}
