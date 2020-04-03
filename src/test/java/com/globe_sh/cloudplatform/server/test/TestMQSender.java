package com.globe_sh.cloudplatform.server.test;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.globe_sh.cloudplatform.common.util.Bcc;

public class TestMQSender {

	private static final String USERNAME = "admin";

	private static final String PASSWORD = "jctech0731";

	private static final String BROKEN_URL = "tcp://62.234.79.17:61616";

	private ConnectionFactory connectionFactory;

	private Connection connection;

	private Session session;

	private Topic topic;

	private MessageProducer producer;

	public void init() {
		try {
			// 创建一个链接工厂
			connectionFactory = new ActiveMQConnectionFactory(USERNAME, PASSWORD, BROKEN_URL);
			// 从工厂中创建一个链接
			connection = connectionFactory.createConnection();
			// 启动链接,不启动不影响消息的发送，但影响消息的接收
			connection.start();
			// 创建一个事物session
			session = connection.createSession(true, Session.SESSION_TRANSACTED);
			// 获取消息发送的目的地，指消息发往那个地方
			topic = session.createTopic("data-north");
			// 获取消息发送的生产者
			producer = session.createProducer(topic);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			session.close();
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void sendMsg(byte[] data) {
		try {
			BytesMessage msg = session.createBytesMessage();
			msg.writeBytes(data);
			producer.send(msg);

			session.commit();
			System.out.println("消息已发送...");
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		TestMQSender test = new TestMQSender();
		test.init();
		byte[] data =  test.getParameterQuery();
		test.sendMsg(data);
		test.close();
	}
	
	public byte[] getParameterSetup() {

		byte[] data = new byte[35];
		data[0] = 0x23;
		data[1] = 0x23;
		data[2] = (byte)0x81;
		data[3] = (byte)0xFE;
		
		data[4] = 0x44;
		data[5] = 0x41;
		data[6] = 0x39;
		data[7] = 0x53;
		data[8] = 0x52;
		data[9] = 0x58;
		data[10] = 0x49;
		data[11] = 0x68;
		data[12] = 0x47;
		data[13] = 0x61;
		data[14] = 0x62;
		data[15] = 0x63;
		data[16] = 0x64;
		data[17] = 0x65;
		data[18] = 0x66;
		data[19] = 0x67;
		data[20] = 0x68;
		
		data[21] = 0x01;
		
		data[22] = 0x0A;
		data[23] = 0x00;
		
		data[24] = 0x13;
		data[25] = 0x04;
		data[26] = 0x0A;
		data[27] = 0x0E;
		data[28] = 0x1A;
		data[29] = 0x21;

		data[30] = 0x01;
		data[31] = 0x02;
		data[32] = (byte)0xFF;
		data[33] = 0x2D;	
		byte bcc = Bcc.getBccWithoutEnd(data);
		data[34] = bcc;
		
		String clientId = "525400fffe3a04b6-00007ba5-00000013-dd89e93a6b99a634-40008f86";
		int len = 99;
		byte[] dp = new byte[len];
		dp[0] = 0x24;
		dp[1] = 0x25;
		dp[len - 2] = 0x25;
		dp[len - 1] = 0x24;
		System.arraycopy(clientId.getBytes(), 0, dp, 2, 60);
		System.arraycopy(data, 0, dp, 62, 35);
		
		return dp;
	}
	
	public byte[] getParameterQuery() {

		byte[] data = new byte[48];
		data[0] = 0x23;
		data[1] = 0x23;
		data[2] = (byte)0x80;
		data[3] = (byte)0xFE;
		
		data[4] = 0x32;
		data[5] = 0x30;
		data[6] = 0x31;
		data[7] = 0x39;
		data[8] = 0x30;
		data[9] = 0x35;
		data[10] = 0x32;
		data[11] = 0x30;
		data[12] = 0x30;
		data[13] = 0x30;
		data[14] = 0x30;
		data[15] = 0x30;
		data[16] = 0x30;
		data[17] = 0x30;
		data[18] = 0x30;
		data[19] = 0x30;
		data[20] = 0x31;
		
		data[21] = 0x01;
		
		data[22] = 0x17;
		data[23] = 0x00;
		
		data[24] = 0x13;
		data[25] = 0x05;
		data[26] = 0x14;
		data[27] = 0x10;
		data[28] = 0x0A;
		data[29] = 0x21;

		data[30] = 0x10;
		
		for(byte i = 0x01; i < 0x11; i++) {
			data[30 + i] = i;
		}
		
		byte bcc = Bcc.getBccWithoutEnd(data);
		data[47] = bcc;
		
		String clientId = "525400fffe3a04b6-0000121d-00000003-606a4bb8cb397303-5ace8afe";
		int len = 112;
		byte[] dp = new byte[len];
		dp[0] = 0x24;
		dp[1] = 0x25;
		dp[len - 2] = 0x25;
		dp[len - 1] = 0x24;
		System.arraycopy(clientId.getBytes(), 0, dp, 2, 60);
		System.arraycopy(data, 0, dp, 62, 48);
		
		return dp;
	}
	
	public byte[] getControl() {

		byte[] data = new byte[32];
		data[0] = 0x23;
		data[1] = 0x23;
		data[2] = (byte)0x82;
		data[3] = (byte)0xFE;
		
		data[4] = 0x44;
		data[5] = 0x41;
		data[6] = 0x39;
		data[7] = 0x53;
		data[8] = 0x52;
		data[9] = 0x58;
		data[10] = 0x49;
		data[11] = 0x68;
		data[12] = 0x47;
		data[13] = 0x61;
		data[14] = 0x62;
		data[15] = 0x63;
		data[16] = 0x64;
		data[17] = 0x65;
		data[18] = 0x66;
		data[19] = 0x67;
		data[20] = 0x68;
		
		data[21] = 0x01;
		
		data[22] = 0x08;
		data[23] = 0x00;
		
		data[24] = 0x13;
		data[25] = 0x04;
		data[26] = 0x0A;
		data[27] = 0x0E;
		data[28] = 0x1A;
		data[29] = 0x21;

		data[30] = 0x02;	
		byte bcc = Bcc.getBccWithoutEnd(data);
		data[31] = bcc;
		
		String clientId = "525400fffe3a04b6-00007ba5-00000013-dd89e93a6b99a634-40008f86";
		byte[] dp = new byte[96];
		dp[0] = 0x24;
		dp[1] = 0x25;
		dp[94] = 0x25;
		dp[95] = 0x24;
		System.arraycopy(clientId.getBytes(), 0, dp, 2, 60);
		System.arraycopy(data, 0, dp, 62, 32);
		
		return dp;
	}
}
