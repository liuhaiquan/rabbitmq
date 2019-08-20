package com.kavin.rabbitmq.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * 在服务和rabbitmq中间添加一层中间件
 */
public interface Barista {

    String OUTPUT_CHANNEL = "output_channel";


    //@Output注解区分了一个输出channel，通过它发送消息到rabbitmq中，
    // 使用@Output注解 区分输入channel，消息通过它发送到rabbitmq
    // 使用这两个注解可以带一个channel的名字作为参数，如果未提供channel名称，
    // 则使用带注释的方法的名称。
    @Output(Barista.OUTPUT_CHANNEL)
    MessageChannel logoutput();  


      
}  