package com.kavin.rabbitmq.api.exchange.direct;

import com.kavin.rabbitmq.api.utils.ChannelUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;

/**
 * direct类型交换器
 * 把消息路由到那些 BindingKey和RoutingKey完全匹配的队列中。
 *  关于下面方法参数的注释请参考 com.kavin.rabbitmq.api.consumer.consumer类
 */
public class Consumer4DirectExchange {

	public static void main(String[] args) throws Exception {
		
		

        
        Channel channel = ChannelUtil.getChannel();
		//4 声明
		String exchangeName = "direct_exchange";
		String exchangeType = "direct";
		String queueName = "direct_queue";
		String routingKey = "direct_key";
		
		//表示声明了一个交换机。direct类型的交换机会把消息路由到那些 BindingKey和RoutingKey完全匹配的队列中。
        //BindingKey：就是channel.queueBind()方法中的通过key绑定交换机和队列（交换机和交换机）的那个键
        //RoutingKey: 就是生产者执行channel.basicPublish()发送消息时带的那个键
        //官方解释：BindingKey和RoutingKey 其实就是一个键

		channel.exchangeDeclare(exchangeName, exchangeType, true, false, false, null);
		//表示声明了一个队列
		channel.queueDeclare(queueName, false, false, false, null);
		//建立一个绑定关系:
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
