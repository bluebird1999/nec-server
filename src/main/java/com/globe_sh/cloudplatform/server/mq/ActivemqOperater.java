package com.globe_sh.cloudplatform.server.mq;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.globe_sh.cloudplatform.common.util.StaticMethod;
import com.globe_sh.cloudplatform.server.manager.SpringManager;

public class ActivemqOperater {

	private static Logger logger = Logger.getLogger(ActivemqOperater.class);

	private static ActivemqOperater instance;

	private JmsTemplate jmsTemplate;

	private ActivemqOperater() {
		jmsTemplate = (JmsTemplate) SpringManager.registerSpring().getApplicationContext().getBean("jmsTemplate");
	}

	public static synchronized ActivemqOperater getInstance() {
		if (instance == null)
			instance = new ActivemqOperater();

		return instance;
	}

	public void sendMessageDefault(final byte[] data) {
		try {
			jmsTemplate.send(new MessageCreator() {
				public Message createMessage(Session session) throws JMSException {
					BytesMessage bytesMessage = session.createBytesMessage();
					bytesMessage.writeBytes(data);

					return bytesMessage;
				}
			});
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public void sendMessage(String topicName, final byte[] data) {
		if (StaticMethod.isNull(topicName)) 
			return;
		try {
			Topic topic =  jmsTemplate.getConnectionFactory().createConnection()   
					.createSession(false, Session.AUTO_ACKNOWLEDGE).createTopic(topicName);   
		
			jmsTemplate.send(topic, new MessageCreator() {
				public Message createMessage(Session session) throws JMSException {
					BytesMessage bytesMessage = session.createBytesMessage();
					bytesMessage.writeBytes(data);

					return bytesMessage;
				}
			});
		} catch (Exception e) {
			logger.error("", e);
		}
	}
}
