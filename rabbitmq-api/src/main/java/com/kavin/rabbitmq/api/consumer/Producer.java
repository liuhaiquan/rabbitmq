package com.kavin.rabbitmq.api.consumer;

import com.kavin.rabbitmq.api.utils.ChannelUtil;
import com.rabbitmq.client.Channel;



/**
 * 生产者发送消息
 */
public class Producer {

	
	public static void main(String[] args) throws Exception {
		Channel channel = ChannelUtil.getChannel();
		
		String exchange = "consumer_exchange";
		String routingKey = "consumer.save";
		
		String msg = "Hello RabbitMQ Consumer Message";
		
		//发送消息。将消息发送到exchange名称是consumer_exchange 的交换机上，根据交换机的类型使用routingKey进行匹配找到相应队列存储起来，等待消费
		//1.指明消息需要发送到哪个交换器中。如果设置为空字符串，则消息会被发送到 rabbitMQ 默认的交换器中，交换器名称是空字符串，路由键是队列名称，
		//2.路由键。交换器根据路由键和交换器自身类型将消息存储到相应的队列当中。
		//3.mandatory  true:交换器无法根据自身的类型和路由键找到一个符合条件的队列，那么 RabbitMQ会调用 Basic.Return 命令将消息返回给生产者 。false：出现上述情形，则消息直接被丢弃
		//4.消息的基本属性集
		//5.消息体
		channel.basicPublish(exchange, routingKey, true, null, msg.getBytes());

		
	}
}
