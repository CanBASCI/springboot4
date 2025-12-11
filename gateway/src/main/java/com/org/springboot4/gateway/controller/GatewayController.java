package com.org.springboot4.gateway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * WebFlux reverse proxy controller.
 * Routes: /users/** → user-service (round-robin), /orders/** → order-service
 */
@RestController
public class GatewayController {

	private static final Logger log = LoggerFactory.getLogger(GatewayController.class);
	private final WebClient webClient;
	
	// Round-robin counter for user-service instances
	private final AtomicInteger userServiceCounter = new AtomicInteger(0);
	
	// User service instances for round-robin
	private static final String[] USER_SERVICE_INSTANCES = {
		"http://user-service-1:8081",
		"http://user-service-2:8082"
	};
	
	// Order service instance
	private static final String ORDER_SERVICE = "http://order-service:8091";

	public GatewayController(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.build();
	}

	@RequestMapping("/users/**")
	public Mono<ResponseEntity<byte[]>> proxyToUserService(ServerWebExchange exchange) {
		ServerHttpRequest request = exchange.getRequest();
		String path = request.getURI().getPath();
		
		// Round-robin selection
		int index = userServiceCounter.getAndIncrement() % USER_SERVICE_INSTANCES.length;
		String baseUrl = USER_SERVICE_INSTANCES[index];
		
		log.debug("Round-robin: Request routed to {} (index: {}, counter: {})", baseUrl, index, userServiceCounter.get());
		
		return proxyRequest(exchange, baseUrl, path);
	}

	@RequestMapping("/orders/**")
	public Mono<ResponseEntity<byte[]>> proxyToOrderService(ServerWebExchange exchange) {
		ServerHttpRequest request = exchange.getRequest();
		String path = request.getURI().getPath();
		
		return proxyRequest(exchange, ORDER_SERVICE, path);
	}

	// Proxies request to target service, preserving method, headers, body, and query params
	private Mono<ResponseEntity<byte[]>> proxyRequest(ServerWebExchange exchange, String baseUrl, String targetPath) {
		ServerHttpRequest request = exchange.getRequest();
		HttpMethod method = request.getMethod();
		
		URI targetUri = URI.create(baseUrl + targetPath + 
			(request.getURI().getQuery() != null ? "?" + request.getURI().getQuery() : ""));
		
		HttpHeaders headers = new HttpHeaders();
		request.getHeaders().forEach((key, values) -> {
			if (!key.equalsIgnoreCase("host") && !key.equalsIgnoreCase("content-length")) {
				headers.put(key, values);
			}
		});
		
		WebClient.RequestBodySpec requestSpec = webClient
			.method(method)
			.uri(targetUri)
			.headers(h -> h.addAll(headers));
		
		WebClient.ResponseSpec responseSpec;
		if (hasBody(method)) {
			responseSpec = requestSpec
				.body(BodyInserters.fromDataBuffers(exchange.getRequest().getBody()))
				.retrieve();
		} else {
			responseSpec = requestSpec.retrieve();
		}
		
		return responseSpec
			.toEntity(byte[].class)
			.doOnNext(response -> {
				ServerHttpResponse responseToClient = exchange.getResponse();
				response.getHeaders().forEach((key, values) -> {
					if (!key.equalsIgnoreCase("content-length")) {
						responseToClient.getHeaders().put(key, values);
					}
				});
				responseToClient.setStatusCode(response.getStatusCode() != null ? 
					response.getStatusCode() : HttpStatus.INTERNAL_SERVER_ERROR);
			})
			.onErrorResume(error -> {
				ServerHttpResponse responseToClient = exchange.getResponse();
				responseToClient.setStatusCode(HttpStatus.BAD_GATEWAY);
				return Mono.just(ResponseEntity
					.status(HttpStatus.BAD_GATEWAY)
					.body(("Gateway error: " + error.getMessage()).getBytes()));
			});
	}

	private boolean hasBody(HttpMethod method) {
		return method == HttpMethod.POST || 
			   method == HttpMethod.PUT || 
			   method == HttpMethod.PATCH;
	}
}

