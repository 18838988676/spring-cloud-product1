package cn.com.controller;

import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import cn.com.pojo.User;
import cn.com.service.UserService;

@Component(value="productController1")
@RestController
@RequestMapping("/product")
public class ProductController {
	
	@Autowired
	private RestTemplate restTemplate=null;
	
	@Autowired
	private UserService userService;
	
	
	
	
	/**
	 * 1:http://localhost:9001/product/addUser
	 * 2:http://localhost:9001/product/find
	 * 3:http://localhost:9001/product/update
	 * 4:http://localhost:9001/product/testOutTime
	 * 5:http://localhost:9001/product/ribbonHystrix
	 * 6:http://localhost:9001/product/feignHystrix
	 * @return
	 */
	
	@GetMapping("/ribbon")
	public User testRibbon() {
		User userPo=null;
		for (int i = 0; i <20; i++) {
			userPo=restTemplate.getForObject("http://WMCUSER/user/user/"+(i+1), User.class);
		}
		return userPo;
	}
	
	
	//addUser
	@GetMapping("/addUser")
	public Map<String,Object> addUser(){
		Map<String, Object> map=new HashedMap();
		User user=new User();
		user.setId(111);
		user.setLevel(111);
		user.setNote("111note");
		user.setUserName("111wmcUserNs");
		Map<String,Object> aa=userService.addUser(user);
		return aa;
	}
	
	@GetMapping("/find")
	public Map<String, Object> findById(){
		Integer id=666;
		return userService.findById(id);
	}
	
	//udpate
	@GetMapping("/update")
	public Map<String, Object> update(){
		return userService.updateUser("测试数据",123);
	}
	
	
	 /*短路测试--------------------------------------------------------start*/
	//熔断测试     使用feign超时 ，测试超时时间
	@GetMapping("/testOutTime") 
	public String testOutTime(){
		return userService.timeout();
	}
	
	
	
	
	
	//0:Hystrix默认1000毫秒 1秒就超时
	//1：保持用户 在Thread.sleep(ms)在ms是在 3000s中随机出现的条件下不变，可以改变商品中的@HystrixCommand注解中的commandProperties中的@HystrixProperty 中的value 时间，指定value后就算超时  进行测试！！
	//2：http://localhost:9001/product/ribbonHystrix  负载均衡下的短路测试 2个 user中的circuitBreaker1timeout 交换出现
	//3：可以在配置文件中设置超时时间：hystrix。command。default。execution。isolation。thread。timeoutInMilliseconds: 3000
	//ribbon短路测试
	
	//@HystrixCommand(fallbackMethod="thiserrorRibbon",commandProperties= {@HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value="1000")})
	
	//此时不用再方法上设置hystrix的超时时间了，我在配置文件中配置了   9s.ribbon是3s；
	//@HystrixCommand(fallbackMethod="thiserrorRibbon")
	@HystrixCommand(fallbackMethod="thiserrorRibbon",commandProperties= {@HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value="1000")})
	@GetMapping("/ribbonHystrix")
	public String circuitBreaker1() {
		return restTemplate.getForObject("http://WMCUSER/user/circuitBreaker1timeout", String.class);
	}
	
	
	//0::http://localhost:9001/product/feignHystrix  
	//1:在feign的负载均衡下，0地址会负载均衡下 调用2个circuitBreaker2timeout中的一个  至于多少算超时时间  已经指定了  4000
	//feign短路测试
//	@HystrixCommand(fallbackMethod="thiserrorFeign",commandProperties= {@HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value="4000")})
	//此时不用再方法上设置hystrix的超时时间了，我在配置文件中配置了   9s.ribbon是3s；
	@HystrixCommand(fallbackMethod="thiserrorFeign")
	@GetMapping("/feignHystrix")
	public String circuitBreaker2() {
		return userService.circuitBreaker2timeout();
	}
	
	
	public String thiserrorRibbon(Throwable e) {
		System.out.println(e.getMessage());
		e.printStackTrace();
		return "这是错误方法thiserrorRibbon";
	}
	public String thiserrorFeign(Throwable e) {
		System.out.println(e.getMessage());
		e.printStackTrace();
		return "这是错误方法thiserrorFeign";
	}
	 /*短路测试--------------------------------------------------------end*/
}
