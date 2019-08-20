package com.kavin.rabbitmq.api.confirm;

import com.kavin.rabbitmq.api.utils.ChannelUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;

/**
 * 消费消息，开启自动确认 ack。 ack之后生产之会执行监听器的handleAck方法
 * 下面一些方法的详细解释可参考com.kavin.rabbitmq.api.consumer.Consumer 类
 */
public class Consumer {

	
	public static void main(String[] args) throws Exception {
		
		
		//创建Channel
		Channel channel = ChannelUtil.getChannel();
		
		String exchangeName = "confirm_exchange";
		String routingKey = "confirm.#";
		String queueName = "confirm_queue";
		
		//声明交换机和队列 然后进行绑定设置, 最后制定路由Key
		channel.exchangeDeclare(exchangeName, "topic", true);
		channel.queueDeclare(queueName, true, false, false, null);
		channel.queueBind(queueName, exchangeName, routingKey);
		
		//5 创建消费者 
		QueueingConsumer queueingConsumer = new QueueingConsumer(channel);
		//此处开启了自动确认
		channel.basicConsume(queueName, true, queueingConsumer);
		
		while(true){
			Delivery delivery = queueingConsumer.nextDelivery();
			String msg = new String(delivery.getBody());
			
			System.err.println("消费端: " + msg);
		}
		
		
	}
}
