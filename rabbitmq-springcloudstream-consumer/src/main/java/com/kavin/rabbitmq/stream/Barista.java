package com.kavin.rabbitmq.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * 在服务和rabbitmq中间添加一层中间件
 */

public interface Barista {

    String INPUT_CHANNEL = "input_channel";

    //@Input注解区分了一个输入channel，通过它接收消息到应用中，
    // 使用@Output注解 区分输出channel，消息通过它离开应用，
    // 使用这两个注解可以带一个channel的名字作为参数，如果未提供channel名称，
    // 则使用带注释的方法的名称。
    @Input(Barista.INPUT_CHANNEL)
    SubscribableChannel loginput();  
    
      
}  