package io.pivotal;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.pivotal.domain.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
@EnableCircuitBreaker
@EnableFeignClients
public class CloudNativeSpringUiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudNativeSpringUiApplication.class, args);
	}

	@FeignClient("https://cloud-native-spring")
	public interface CityClient {

		@RequestMapping(method= RequestMethod.GET, value="/cities", consumes="application/hal+json")
		Resources<City> getCities();
	}

	@Component
	public class CityService {

		private CloudNativeSpringUiApplication.CityClient _client;

		@Autowired
		public CityService(CloudNativeSpringUiApplication.CityClient client) {
			_client = client;
		}

		@HystrixCommand(fallbackMethod = "getCitiesFallback")
		public Resources<City> getCities() {
			return _client.getCities();
		}

		public Resources<City> getCitiesFallback() {
			//We'll just return an empty response
			return new Resources<City>(Collections.EMPTY_LIST);
		}
	}
}
