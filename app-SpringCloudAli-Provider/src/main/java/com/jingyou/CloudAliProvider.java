package com.jingyou;


import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.jingyou.main.config.Conf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

import java.io.IOException;
import java.io.StringReader;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executor;

@Slf4j
@EnableHystrix
@SpringBootApplication
@EnableDiscoveryClient
public class CloudAliProvider {
    public static void main(String[] args) throws NacosException, IOException {
        SpringApplication.run(CloudAliProvider.class, args);
        String serverAddr = "localhost";
        String dataId = "insure.properties";
        String group = "DEFAULT_GROUP";
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
        ConfigService configService = NacosFactory.createConfigService(properties);
        String content = configService.getConfig(dataId, group, 5000);
        if (!Objects.isNull(content)) Conf.getInstance().properties.load(new StringReader(content));
        log.info("保司配置信息: {}", Conf.getInstance().properties.toString());
        configService.addListener(dataId, group, new Listener() {
            @Override
            public void receiveConfigInfo(String configInfo) {
                try {
                    Conf.getInstance().properties.load(new StringReader(configInfo));
                    log.info("刷新保司配置信息: {}", Conf.getInstance().properties.toString());
                } catch (Exception e) {
                    log.info("nacos刷新配置失败 :\n {}", e);
                }
            }
            @Override
            public Executor getExecutor() {
                return null;
            }
        });
    }
}
