package com.kavin.rabbitmq.api.confirm;

import java.io.IOException;

import com.kavin.rabbitmq.api.utils.ChannelUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 生产者监听消息，开启消息确认模式
 * 下面一些方法的详细解释可参考com.kavin.rabbitmq.api.consumer.Producer 类
 */
public class Producer {

	
	public static void main(String[] args) throws Exception {

		//创建Channel
		Channel channel = ChannelUtil.getChannel();
		
		
		//生产者指定我们的消息投递模式: 消息的确认模式
		channel.confirmSelect();
		
		String exchangeName = "confirm_exchange";
		String routingKey = "confirm.save";
		
		//5 发送一条消息
		String msg = "Hello RabbitMQ Send confirm message!";
		channel.basicPublish(exchangeName, routingKey, null, msg.getBytes());
		
		//6 添加一个确认监听
		channel.addConfirmListener(new ConfirmListener() {
			@Override  //监听被拒绝的消息
			public void handleNack(long deliveryTag, boolean multiple) throws IOException {
				System.err.println("-------no ack!-----------");
			}
			
			@Override  //监听被确认的消息
			public void handleAck(long deliveryTag, boolean multiple) throws IOException {
				System.err.println("-------ack!-----------");
			}
		});
		
		
		
		
		
	}
}
