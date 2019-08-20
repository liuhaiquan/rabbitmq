package com.kavin.rabbitmq.api.ack;

import java.util.HashMap;
import java.util.Map;

import com.kavin.rabbitmq.api.utils.ChannelUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

/**
 * 消息生产者发送消息
 *
 * 下面发方法的一些详细解释可以参考com.kavin.rabbitmq.api.consumer.Producer类
 */
public class Producer {

	
	public static void main(String[] args) throws Exception {

	    //获取channel
		Channel channel = ChannelUtil.getChannel();
		
		String exchange = "ack_exchange";
		String routingKey = "ack.save";
		
		
		
		for(int i =0; i<5; i ++){
			//自定义一些参数
			Map<String, Object> headers = new HashMap<String, Object>();
			headers.put("num", i);
			
			AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
					.deliveryMode(2) //// 设置消息是否，1： 非 2：持久化
					.contentEncoding("UTF-8") //消息字符集
					.headers(headers)  // 设置用户自定义的一些参数集合
					.build();
			String msg = "Hello RabbitMQ ACK Message " + i;
			channel.basicPublish(exchange, routingKey, true, properties, msg.getBytes());
		}
		
	}
}
