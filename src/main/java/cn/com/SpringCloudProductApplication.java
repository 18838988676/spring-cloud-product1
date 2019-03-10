package cn.com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
//打开feign
@EnableFeignClients
//打开注册发现中心
@EnableEurekaClient
public class SpringCloudProductApplication {

	//初始化负载均衡
	@LoadBalanced  //多借点负载均衡  sss444
	@Bean(name="restTemplate")
	public RestTemplate initRestTemplate() {
		return new RestTemplate();
	}
	
	
	public static void main(String[] args) {
		SpringApplication.run(SpringCloudProductApplication.class, args);
	}

}
