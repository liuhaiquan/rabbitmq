package com.kavin.rabbitmq.api.returnlistener;

import com.kavin.rabbitmq.api.utils.ChannelUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;


/**
 * 消费者
 *
 *  * 关于下面方法参数的注释请参考 com.kavin.rabbitmq.api.consumer.Consumer类
 */
public class Consumer {

	
	public static void main(String[] args) throws Exception {

		Channel channel = ChannelUtil.getChannel();
		
		String exchangeName = "return_exchange";
		String routingKey = "return.#";
		String queueName = "return_queue";
		
		channel.exchangeDeclare(exchangeName, "topic", true, false, null);
		channel.queueDeclare(queueName, true, false, false, null);
		channel.queueBind(queueName, exchangeName, routingKey);
		
		QueueingConsumer queueingConsumer = new QueueingConsumer(channel);
		
		channel.basicConsume(queueName, true, queueingConsumer);
		
		while(true){
			
			Delivery delivery = queueingConsumer.nextDelivery();
			String msg = new String(delivery.getBody());
			System.err.println("消费者: " + msg);
		}
		
		
		
		
		
	}
}
