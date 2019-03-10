package cn.com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import cn.com.pojo.UserPo;

@RestController
@RequestMapping("/product")
public class ProductController {
	
	@Autowired
	private RestTemplate restTemplate=null;
	
	@GetMapping("/ribbon")
	public UserPo testRibbon() {
		UserPo userPo=null;
		for (int i = 0; i <20; i++) {
			userPo=restTemplate.getForObject("http://WMCUSER/user/user/"+(i+1), UserPo.class);
		}
		return userPo;
	}
	

}
