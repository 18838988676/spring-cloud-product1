package cn.com.fallback;

import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Component;

import cn.com.pojo.User;
import cn.com.service.UserService;

//feign集成hystrix需要配置开启,而hystrix默认是关闭的
//降级  执行  指定的方法 需要 在配置中打开hystrix:   feign.hystrix.enabled=true

//feign服务降级后，指定的方法
@Component
public class FeignFallbackTest implements UserService{

	@Override
	public Map<String, Object> addUser(User user) {
		 Map<String, Object> map=new HashedMap();
		 map.put("error", "添加用户信息详情如下："+user+"，添加失败，这是降级后指定的方法");
		return map;
	}

	@Override
	public Map<String, Object> findById() {
		// TODO Auto-generated method stub
		 Map<String, Object> map=new HashedMap();
		 map.put("msg2", "888888888888888888888888888888888888");
		return map;
	}

	@Override
	public Map<String, Object> updateUser(String userName, Long id) {
		// TODO Auto-generated method stub
		return null;
	}

}
