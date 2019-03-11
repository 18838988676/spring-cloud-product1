使端点暴露
management.endpoints.web.exposure.include=health,info,hystrix,stream

https://blog.csdn.net/east123321/article/details/82385816
springcloud之Feign、ribbon设置超时时间和重试机制的总结
一、 Feign设置超时时间 
使用Feign调用接口分两层，ribbon的调用和hystrix的调用，所以ribbon的超时时间和Hystrix的超时时间的结合就是Feign的超时时间

#hystrix的超时时间
hystrix:
    command:
        default:
            execution:
              timeout:
                enabled: true
              isolation:
                    thread:
                        timeoutInMilliseconds: 9000
#ribbon的超时时间
ribbon:
  ReadTimeout: 3000   #指的是建立连接所用的时间，适用于网络状况正常的情况下，两端连接所用的时间。
  ConnectTimeout: 3000 #指的是建立连接后从服务器读取到可用资源所用的时间。


一般情况下 都是 ribbon 的超时时间（<）hystrix的超时时间（因为涉及到ribbon的重试机制） 
因为ribbon的重试机制和Feign的重试机制有冲突，所以源码中默认关闭Feign的重试机制，源码如下


要开启Feign的重试机制如下：（Feign默认重试五次 源码中有）
@Bean
Retryer feignRetryer() {
        return  new Retryer.Default();
}



二、ribbon的重试机制 

ribbon:
  ReadTimeout: 3000
  ConnectTimeout: 3000
  MaxAutoRetries: 1 #同一台实例最大重试次数,不包括首次调用
  MaxAutoRetriesNextServer: 1 #重试负载均衡其他的实例最大重试次数,不包括首次调用
  OkToRetryOnAllOperations: false  #是否所有操作都重试 

根据上面的参数计算重试的次数：MaxAutoRetries+MaxAutoRetriesNextServer+(MaxAutoRetries *MaxAutoRetriesNextServer) 即重试3次 则一共产生4次调用 
如果在重试期间，时间超过了hystrix的超时时间，便会立即执行熔断，fallback。所以要根据上面配置的参数计算hystrix的超时时间，使得在重试期间不能达到hystrix的超时时间，不然重试机制就会没有意义 
hystrix超时时间的计算： (1 + MaxAutoRetries + MaxAutoRetriesNextServer) * ReadTimeout 即按照以上的配置 hystrix的超时时间应该配置为 （1+1+1）*3=9秒

当ribbon超时后且hystrix没有超时，便会采取重试机制。当OkToRetryOnAllOperations设置为false时，只会对get请求进行重试。如果设置为true，便会对所有的请求进行重试，如果是put或post等写操作，如果服务器接口没做幂等性，会产生不好的结果，所以OkToRetryOnAllOperations慎用。

如果不配置ribbon的重试次数，默认会重试一次 
注意： 
默认情况下,GET方式请求无论是连接异常还是读取异常,都会进行重试 
非GET方式请求,只有连接异常时,才会进行重试



