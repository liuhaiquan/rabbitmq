package com.kavin.spring.convert;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;


/**
 * 自定义pdf消息转化器
 *
 * 实现MessageConverter 接口。重写toMessage和fromMessage方法
 */
public class        PDFMessageConverter implements MessageConverter {
    //从接收的消息java对象中获取message对象
	@Override
	public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
		throw new MessageConversionException(" convert error ! ");
	}


    // 从发送的消息的Message对象中获取java 对象
	@Override
	public Object fromMessage(Message message) throws MessageConversionException {
		System.err.println("-----------PDF MessageConverter----------");
		
		byte[] body = message.getBody();
		String fileName = UUID.randomUUID().toString();
		String path = "d:/010_test/" + fileName + ".pdf";
		File f = new File(path);
		try {
			Files.copy(new ByteArrayInputStream(body), f.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f;
	}

}
