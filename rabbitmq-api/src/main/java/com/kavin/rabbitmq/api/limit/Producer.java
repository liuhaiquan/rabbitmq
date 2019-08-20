package com.kavin.rabbitmq.api.limit;

import com.kavin.rabbitmq.api.utils.ChannelUtil;
import com.rabbitmq.client.Channel;

/**
 * 消息生产者
 * 下面一些方法的详细解释请参考com.kavin.rabbitmq.api.consumer.Producer类
 */
public class Producer {

	
	public static void main(String[] args) throws Exception {
		

		Channel channel = ChannelUtil.getChannel();
		
		String exchange = "qos_exchange";
		String routingKey = "qos.save";
		
		String msg = "Hello RabbitMQ QOS Message";
		
		for(int i =0; i<5; i ++){
			channel.basicPublish(exchange, routingKey, true, null, msg.getBytes());
		}
		
	}
}
