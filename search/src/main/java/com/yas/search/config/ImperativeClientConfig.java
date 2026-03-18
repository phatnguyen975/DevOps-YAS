package com.yas.search.config;

import lombok.RequiredArgsConstructor;
import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.yas.search.repository")
@ComponentScan(basePackages = "com.yas.search.service")
@RequiredArgsConstructor
public class ImperativeClientConfig extends ElasticsearchConfiguration {

    private final ElasticsearchDataConfig elasticsearchConfig;

    @Value("${spring.elasticsearch.uris:}")
    private String elasticsearchUris;

    @Override
    public ClientConfiguration clientConfiguration() {
        String endpoint = resolveEndpoint();
        ClientConfiguration.MaybeSecureClientConfigurationBuilder builder = ClientConfiguration.builder()
            .connectedTo(endpoint);

        String username = elasticsearchConfig.getUsername();
        String password = elasticsearchConfig.getPassword();
        if (username != null && !username.isBlank() && password != null) {
            return builder
                .withBasicAuth(username, password)
                .build();
        }

        return builder.build();
    }

    private String resolveEndpoint() {
        if (elasticsearchUris != null && !elasticsearchUris.isBlank()) {
            String firstUri = elasticsearchUris.split(",")[0].trim();
            if (firstUri.startsWith("http://") || firstUri.startsWith("https://")) {
                URI uri = URI.create(firstUri);
                int port = uri.getPort() > 0 ? uri.getPort() : 9200;
                return uri.getHost() + ":" + port;
            }
            return firstUri;
        }

        String configuredUrl = elasticsearchConfig.getUrl();
        if (configuredUrl == null || configuredUrl.isBlank()) {
            return "localhost:9200";
        }

        if (configuredUrl.startsWith("http://") || configuredUrl.startsWith("https://")) {
            URI uri = URI.create(configuredUrl);
            int port = uri.getPort() > 0 ? uri.getPort() : 9200;
            return uri.getHost() + ":" + port;
        }

        if (!configuredUrl.contains(":")) {
            return configuredUrl + ":9200";
        }

        return configuredUrl;
    }
}
