package com.circunvalar.edu.co.agendavirtual.modulos.ia.configuracion;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;

import io.netty.resolver.DefaultAddressResolverGroup;
import io.netty.channel.ChannelOption;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Value("${ai.provider.url:https://api-inference.huggingface.co/models/gpt2}")
    private String aiUrl;

    @Value("${ai.provider.key:}")
    private String aiKey;

    @Bean
    public WebClient webClient() {
        // Use JDK resolver (DefaultAddressResolverGroup) to avoid some Netty DNS issues
        HttpClient httpClient = HttpClient.create()
                .resolver(DefaultAddressResolverGroup.INSTANCE)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10_000)
                .responseTimeout(Duration.ofSeconds(30));

        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        WebClient.Builder builder = WebClient.builder()
                .clientConnector(connector);

        if (aiKey != null && !aiKey.isEmpty()) {
            builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + aiKey);
        }

        if (aiUrl != null && !aiUrl.isEmpty()) {
            builder.baseUrl(aiUrl);
        }

        // sensible default headers
        builder.defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        return builder.build();
    }

}