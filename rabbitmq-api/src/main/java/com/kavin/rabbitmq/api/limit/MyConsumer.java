package com.kavin.rabbitmq.api.limit;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class MyConsumer extends DefaultConsumer {


	private Channel channel ;
	
	public MyConsumer(Channel channel) {
		super(channel);
		this.channel = channel;
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
		System.err.println("-----------consume message----------");
		System.err.println("consumerTag: " + consumerTag);
		System.err.println("envelope: " + envelope);
		System.err.println("properties: " + properties);
		System.err.println("body: " + new String(body));

		//手动确认消息。 使用了消息限流，必须手动确认消息。消息确认后，生产者才会发送下一批次规定数量的消息
        // 1.消息编号
        // 2.是否批量确认DeliveryTag编号之前的所有消息
		channel.basicAck(envelope.getDeliveryTag(), false);
		
	}


}
