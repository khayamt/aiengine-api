package za.co.kpolit.aiengine;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class AiengineApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiengineApplication.class, args);
	}

}
