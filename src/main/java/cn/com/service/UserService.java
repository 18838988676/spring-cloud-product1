package cn.com.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import cn.com.fallback.FeignFallbackTest;
import cn.com.pojo.User;

//feign集成hystrix需要配置开启,而hystrix默认是关闭的
//降级  执行  指定的方法 需要 在配置中打开hystrix:   feign.hystrix.enabled=true

//采用feign 实现客户端负载均衡
//服务ID
//fallback=FeignFallbackTest.class  服务调用失败后 指定的方法
@FeignClient(name="WMCUSER",fallback=FeignFallbackTest.class)
public interface UserService {
	
	//采用http的post请求
	@PostMapping("/user/addUser")
	public Map<String,Object> addUser(@RequestBody User use);
	
    //采用http的get请求
	@GetMapping("/user/user333")
	public Map<String, Object> findById();

	//@RequestMapping(value = "/hellol", method= RequestMethod.POST)
	//@RequestMapping(value = "/hellol", method= RequestMethod.GET)
	//采用http的post请求
	@PostMapping("/user/user/{userName}")
	public  Map<String,Object>  updateUser(@PathVariable("userName") String userName,@RequestHeader("id") Long id);
	
	
}
