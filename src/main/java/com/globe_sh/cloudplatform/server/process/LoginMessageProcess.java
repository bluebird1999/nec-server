package com.globe_sh.cloudplatform.server.process;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.globe_sh.cloudplatform.common.util.Bcc;
import com.globe_sh.cloudplatform.common.util.StaticMethod;
import com.globe_sh.cloudplatform.common.util.StaticVariable;
import com.globe_sh.cloudplatform.server.manager.OnlineManager;
import com.globe_sh.cloudplatform.common.util.ByteArrayUtil;
import java.sql.Timestamp;

public class LoginMessageProcess implements Processor {

	private static Logger logger = Logger.getLogger(LoginMessageProcess.class);
		
	public void process(Exchange message) {
		byte[] data = new byte[StaticVariable.CONTROL_DATA_LENGTH];
		logger.info("Process Message For Login Operate. ###start###");
		byte[] exchange = (byte[])message.getIn().getBody();
    	System.arraycopy(exchange, 0, data, 0, StaticVariable.CONTROL_DATA_LENGTH);
    	boolean flag = this.processLogin(exchange);
		if(flag) {
			data[3] = 0x01;
		} else {
			data[3] = 0x02;
		}
    	byte bcc = Bcc.getBccWithoutEnd(data);
    	data[StaticVariable.CONTROL_DATA_LENGTH - 1] = bcc;
		this.processLoginResponse(message, data);
		logger.info("Process Message For Login Operate. ###end###");
	}
	
	private boolean processLogin(byte[] data) {
		String station = StaticMethod.ascii2String(data, StaticVariable.STATION_ID_START, StaticVariable.STATION_ID_LENGTH).trim();
		Timestamp loginTime = StaticMethod.getTimestampOrigin(data, StaticVariable.PROTOCOL_CONTROL_DATATIME_START);
		short loginSeq = ByteArrayUtil.getShortLowEnd(data, StaticVariable.PROTOCOL_CONTROL_LOGINSEQ_START);
		JSONObject json = new JSONObject();
		json.put("Status", StaticVariable.STATION_STATUS_ONLINE);
		json.put("LoginTime", loginTime);
		json.put("LoginSeq", loginSeq);
		json.put("Station", station);
		json.put("ProcessTime", StaticMethod.getTimeString(0));

		return OnlineManager.getInstance().online(station, json);
	}
	
	private void processLoginResponse(Exchange message, byte[] data) {
		logger.info("Process Login Response ");
		message.getIn().setHeader("send", StaticVariable.ROUTE_HEAD_SUCCESS);
    	message.getIn().setBody(data);
	}
}
