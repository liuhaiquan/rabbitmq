package com.kavin.rabbitmq.api.ack;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;


/**
 * 订阅消息
 * 继承DefaultConsumer 重写handleDelivery 方法
 */
public class MyConsumer extends DefaultConsumer {


	private Channel channel ;
	
	public MyConsumer(Channel channel) {
		super(channel);
		this.channel = channel;
	}

    //接收消息
    //1.消费者标签，用来区分多个消费者:
    //2.打包消息的数据
    //3.消息属性集
    //4.消息体
	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
		System.err.println("-----------consume message----------");
		System.err.println("body: " + new String(body));
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

        //num为0的消息会一直被发送，因为返回到队列中只有num为0 的这个消息.所以消息过来还是会被拒绝，然后重回队列，这样一直循环
		if((Integer)properties.getHeaders().get("num") == 0) {
		    //手动拒绝消息
            //1.消息的唯一标志编号
            //2.是否批量处理。true为批量拒绝该DeliveryTag编号之前的所有消息
            //3.是否重回队列. true为重新回到监听的队列尾部
			channel.basicNack(envelope.getDeliveryTag(), false, true);
		} else {
		    //手工确认消息
            //1.消息的唯一标志编号
            //2.是否批量处理。true为批量确认该DeliveryTag编号之前的所有消息
			channel.basicAck(envelope.getDeliveryTag(), false);
		}
		
	}


}
