package cn.com.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.com.fallback.FeignFallbackTest;
import cn.com.pojo.User;

//feign集成hystrix需要配置开启,而hystrix默认是关闭的
//降级  执行  指定的方法 需要 在配置中打开hystrix:   feign.hystrix.enabled=true

//采用feign 实现客户端负载均衡
//服务ID
//fallback=FeignFallbackTest.class  服务调用失败后 指定的方法
@FeignClient(name="WMCUSER",fallback=FeignFallbackTest.class)
public interface UserService {
	
	// POST方法请求用户微服务
	//采用http的post请求  //user指的是@RequestMapping("/user")  类上面的
	@PostMapping("/user/addUser")			// 请求体参数
	public Map<String,Object> addUser(@RequestBody User use);
	
    //采用http的get请求
	@GetMapping("/user/find/{id}")
	public Map<String, Object> findById(@PathVariable("id") Integer id);

	//@RequestMapping(value = "/hellol", method= RequestMethod.POST)
	//@RequestMapping(value = "/hellol", method= RequestMethod.GET)
	//采用http的post请求
	@PostMapping("/user/update/{userName}")        // URL参数							// 请求头参数
	public  Map<String,Object>  updateUser(@PathVariable("userName") String userName,@RequestHeader("id") Integer id);
	
	// 调用用户微服务的timeout请求
	 @GetMapping("/user/timeout")
	 public String timeout() ;
	
	 @GetMapping("/user/circuitBreaker2timeout")
	public String circuitBreaker2timeout();
	 
	 
	 
}
