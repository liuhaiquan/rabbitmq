package com.kavin.rabbitmq.api.exchange.topic;

import com.kavin.rabbitmq.api.utils.ChannelUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;

/**
 * topic类型交换器
 * 将消息路由到 BindingKey和RoutingKey 相匹配的队列中。例如user.* 可以匹配 user.key,只能匹配点号后面的一个单词
 *                                                   user.#  可以匹配user.key.value,可以匹配点号后面的多个单词。
 *  * 关于下面方法参数的注释请参考 com.kavin.rabbitmq.api.consumer.consumer类
 */

public class Consumer4TopicExchange {

	public static void main(String[] args) throws Exception {
        
        Channel channel = ChannelUtil.getChannel();
		//声明
		String exchangeName = "topic_exchange";
		String exchangeType = "topic";
		String queueName = "topic_queue";
		//String routingKey = "user.*";
		String routingKey = "user.*";
		// 1 声明交换机   topic类型的交换器是进行模糊匹配的
		channel.exchangeDeclare(exchangeName, exchangeType, true, false, false, null);
		// 2 声明队列
		channel.queueDeclare(queueName, false, false, false, null);
		// 3 建立交换机和队列的绑定关系:
		channel.queueBind(queueName, exchangeName, routingKey);
        //实际开发中不建议使用QueueingConsumer，此处只是为了方便代码演示。
        // 这个方法已经被弃用，建议使用DefaultConsumer类并重写handleDelivery方法
        QueueingConsumer consumer = new QueueingConsumer(channel);
        //参数：队列名称、是否自动ACK、Consumer
        channel.basicConsume(queueName, true, consumer);  
        //循环获取消息  
        while(true){  
            //获取消息，如果没有消息，这一步将会一直阻塞  
            Delivery delivery = consumer.nextDelivery();  
            String msg = new String(delivery.getBody());    
            System.out.println("收到消息：" + msg);  
        } 
	}
}
