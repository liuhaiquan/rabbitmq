package com.kavin.spring.convert;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * 自定义文本消息转换器
 * 实现MessageConverter 接口。重写toMessage和fromMessage方法
 *
 */
public class TextMessageConverter implements MessageConverter {
    //从接收的消息java对象中获取message对象	@Override
    @Override
	public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
		return new Message(object.toString().getBytes(), messageProperties);
	}
    // 从发送的消息的Message对象中获取java 对象
	@Override
	public Object fromMessage(Message message) throws MessageConversionException {
		String contentType = message.getMessageProperties().getContentType();
		if(null != contentType && contentType.contains("text")) {  //如果发送消息的contentType 包含text ,则把消息转成字符串返回
			return new String(message.getBody());//根据这个返回类型调用消息委托类中（MessageDelegate.java）的参数是字符串的方法）  public void consumeMessage(String messageBody)
		}
        // 否则返回字节数组（根据这个返回类型调用消息委托类中（MessageDelegate.java）的参数是字节数组的方法）  public void consumeMessage(byte[] messageBody)
		return message.getBody();
	}

}
