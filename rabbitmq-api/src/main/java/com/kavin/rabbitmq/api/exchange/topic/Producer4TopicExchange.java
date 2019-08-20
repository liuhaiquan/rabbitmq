package com.kavin.rabbitmq.api.exchange.topic;

import com.kavin.rabbitmq.api.utils.ChannelUtil;
import com.kavin.rabbitmq.api.utils.ConnectionUtil;
import com.rabbitmq.client.Channel;


/**
 * topic类型交换器
 * 将消息路由到 BindingKey和RoutingKey 相匹配的队列中。例如user.* 可以匹配 user.key,只能匹配点号后面的一个单词
 *  *                                                user.#  可以匹配user.key.value,可以匹配点号后面的多个单词
 * 关于下面方法参数的注释请参考 com.kavin.rabbitmq.api.consumer.Producer类
 *
 */
public class Producer4TopicExchange {

	
	public static void main(String[] args) throws Exception {
		

		//创建Channel
		Channel channel = ChannelUtil.getChannel();
		//声明
		String exchangeName = "topic_exchange";
		String routingKey1 = "user.save"; // routingKey 为user.* 可以匹配
		String routingKey2 = "user.update"; // routingKey 为user.* 可以匹配
		String routingKey3 = "user.delete.abc"; // routingKey 为user.# 可以匹配
		//发送
		
		String msg = "Hello World RabbitMQ 4 Topic Exchange Message ...";
		channel.basicPublish(exchangeName, routingKey1 , null , msg.getBytes()); 
		channel.basicPublish(exchangeName, routingKey2 , null , msg.getBytes()); 	
		channel.basicPublish(exchangeName, routingKey3 , null , msg.getBytes()); 
		ChannelUtil.close(channel);
        ConnectionUtil.close();
	}
	
}
