package com.yas.search.config;

import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchConnectionDetails;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.ClientConfiguration.MaybeSecureClientConfigurationBuilder;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.yas.search.repository")
@ComponentScan(basePackages = "com.yas.search.service")
@RequiredArgsConstructor
public class ImperativeClientConfig extends ElasticsearchConfiguration {

    private final ElasticsearchDataConfig elasticsearchConfig;
    private final ObjectProvider<ElasticsearchConnectionDetails> elasticsearchConnectionDetailsProvider;

    @Value("${spring.elasticsearch.uris:}")
    private String elasticsearchUris;

    @Override
    public ClientConfiguration clientConfiguration() {
        EndpointConfig endpointConfig = resolveEndpointConfig();
        ClientConfiguration.MaybeSecureClientConfigurationBuilder builder = ClientConfiguration.builder()
            .connectedTo(endpointConfig.endpoints());

        if (endpointConfig.useSsl()) {
            builder = (MaybeSecureClientConfigurationBuilder) builder.usingSsl();
        }

        String username = endpointConfig.username();
        String password = endpointConfig.password();
        if (username != null && !username.isBlank() && password != null) {
            return builder
                .withBasicAuth(username, password)
                .build();
        }

        return builder.build();
    }

    private EndpointConfig resolveEndpointConfig() {
        ElasticsearchConnectionDetails connectionDetails = elasticsearchConnectionDetailsProvider.getIfAvailable();
        if (connectionDetails != null && connectionDetails.getNodes() != null && !connectionDetails.getNodes().isEmpty()) {
            List<ElasticsearchConnectionDetails.Node> nodes = connectionDetails.getNodes();
            String[] endpoints = nodes.stream()
                .map(node -> node.hostname() + ":" + node.port())
                .toArray(String[]::new);
            boolean useSsl = nodes.stream()
                .anyMatch(node -> node.protocol() == ElasticsearchConnectionDetails.Node.Protocol.HTTPS);
            return new EndpointConfig(endpoints, useSsl, connectionDetails.getUsername(), connectionDetails.getPassword());
        }

        if (elasticsearchUris != null && !elasticsearchUris.isBlank()) {
            String firstUri = elasticsearchUris.split(",")[0].trim();
            if (firstUri.startsWith("http://") || firstUri.startsWith("https://")) {
                URI uri = URI.create(firstUri);
                int port = uri.getPort() > 0 ? uri.getPort() : 9200;
                boolean useSsl = firstUri.startsWith("https://");
                return new EndpointConfig(
                    new String[] { uri.getHost() + ":" + port },
                    useSsl,
                    elasticsearchConfig.getUsername(),
                    elasticsearchConfig.getPassword());
            }
            return new EndpointConfig(
                new String[] { firstUri },
                false,
                elasticsearchConfig.getUsername(),
                elasticsearchConfig.getPassword());
        }

        String configuredUrl = elasticsearchConfig.getUrl();
        if (configuredUrl == null || configuredUrl.isBlank()) {
            return new EndpointConfig(
                new String[] { "localhost:9200" },
                false,
                elasticsearchConfig.getUsername(),
                elasticsearchConfig.getPassword());
        }

        if (configuredUrl.startsWith("http://") || configuredUrl.startsWith("https://")) {
            URI uri = URI.create(configuredUrl);
            int port = uri.getPort() > 0 ? uri.getPort() : 9200;
            boolean useSsl = configuredUrl.startsWith("https://");
            return new EndpointConfig(
                new String[] { uri.getHost() + ":" + port },
                useSsl,
                elasticsearchConfig.getUsername(),
                elasticsearchConfig.getPassword());
        }

        if (!configuredUrl.contains(":")) {
            configuredUrl = configuredUrl + ":9200";
        }

        return new EndpointConfig(
            new String[] { configuredUrl },
            false,
            elasticsearchConfig.getUsername(),
            elasticsearchConfig.getPassword());
    }

    private record EndpointConfig(String[] endpoints, boolean useSsl, String username, String password) {
    }
}
