package com.yas.search.config;

import common.container.ContainerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;

@TestConfiguration
public class KafkaIntegrationTestConfiguration {

    @Value("${kafka.version}")
    private String kafkaVersion;

    @Value("${elasticsearch.version}")
    private String elasticSearchVersion;

    @Bean
    @ServiceConnection
    public KafkaContainer kafkaContainer(DynamicPropertyRegistry registry) {
        return ContainerFactory.kafkaContainer(registry, kafkaVersion)
            .withEnv("KAFKA_HEAP_OPTS", "-Xms256m -Xmx256m");
    }

    @Bean
    @ServiceConnection
    public ElasticTestContainer elasticTestContainer() {
        return new ElasticTestContainer(elasticSearchVersion);
    }


    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        ElasticTestContainer esContainer = new ElasticTestContainer("8.15.3");
        registry.add("elasticsearch.url", () -> esContainer.getHost() + ":" + esContainer.getMappedPort(9200));
        registry.add("spring.elasticsearch.uris", () -> "http://" + esContainer.getHost() + ":" + esContainer.getMappedPort(9200));
    }
}
