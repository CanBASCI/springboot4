package com.org.springboot4.gateway.config;

import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Excludes Spring Cloud Gateway's NettyConfiguration to use custom NettyReactiveWebServerFactory
 */
public class GatewayNettyConfigurationFilter implements AutoConfigurationImportFilter {

	private static final Set<String> EXCLUDED_CLASSES = new HashSet<>(
			Arrays.asList("org.springframework.cloud.gateway.config.GatewayAutoConfiguration$NettyConfiguration")
	);

	@Override
	public boolean[] match(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
		boolean[] matches = new boolean[autoConfigurationClasses.length];
		for (int i = 0; i < autoConfigurationClasses.length; i++) {
			matches[i] = !EXCLUDED_CLASSES.contains(autoConfigurationClasses[i]);
		}
		return matches;
	}
}

