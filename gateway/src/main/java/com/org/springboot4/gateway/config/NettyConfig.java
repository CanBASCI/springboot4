package com.org.springboot4.gateway.config;

import org.springframework.boot.reactor.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.reactor.netty.NettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.netty.http.server.HttpServer;

/**
 * Netty configuration for Spring Boot 4.0.0 compatibility.
 * Provides NettyReactiveWebServerFactory bean to override Spring Cloud Gateway's NettyConfiguration.
 */
@Configuration
public class NettyConfig {

	@Bean
	@Primary
	public NettyReactiveWebServerFactory nettyReactiveWebServerFactory() {
		NettyReactiveWebServerFactory factory = new NettyReactiveWebServerFactory();
		factory.addServerCustomizers(new NettyServerCustomizer() {
			@Override
			public HttpServer apply(HttpServer httpServer) {
				return httpServer;
			}
		});
		
		return factory;
	}
}
