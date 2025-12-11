package com.org.springboot4.gateway.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient configuration with tracing support via ObservationRegistry
 */
@Configuration
public class WebClientConfig {

	@Bean
	public WebClient.Builder webClientBuilder(ObservationRegistry observationRegistry) {
		return WebClient.builder()
				.observationRegistry(observationRegistry);
	}
}

