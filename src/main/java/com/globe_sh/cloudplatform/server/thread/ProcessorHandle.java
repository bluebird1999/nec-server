package com.globe_sh.cloudplatform.server.thread;

import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.globe_sh.cloudplatform.common.cache.JedisOperater;
import com.globe_sh.cloudplatform.common.util.Bcc;
import com.globe_sh.cloudplatform.common.util.ByteArrayUtil;
import com.globe_sh.cloudplatform.common.util.CRC8;
import com.globe_sh.cloudplatform.common.util.StaticMethod;
import com.globe_sh.cloudplatform.server.mq.ActivemqOperater;

public class ProcessorHandle extends Thread {

	private static Logger logger = LogManager.getLogger(ProcessorHandle.class);

	private String name;
	
	private String vin;
	
	private String clientId;
	
	private int index;

	private Map<Integer, byte[]> dataMap;

	public ProcessorHandle(String vin, int index, Map<Integer, byte[]> dataMap) {
		this.name = "ProcessorHandle-" + vin;
		this.vin = vin;
		this.index = index;
		this.dataMap = dataMap;
	}

	public void run() {
		try {
			logger.info("Start Send Data To Terminal: " + name + "\tIndex: " + index);
			queryClientId();
			
			byte[] data = dataMap.get(index);
			byte[] sendData = getSendData((short)index, data);
			ActivemqOperater.getInstance().sendMessage("data-south", sendData);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void queryClientId() {
		String status = JedisOperater.getOnlineStatus(vin);
		try {
			JSONObject json = JSONObject.parseObject(status);
			clientId = json.getString("ClientId");
		} catch(Exception e) {
			logger.error("Can Not Find Client-ID By Vin: " + vin);
		}
	}
	
	private byte[] getSendData(short idx, byte[] data) throws Exception {
		int len = data.length + 34;
		byte[] sendData = new byte[len];
		sendData[0] = 0x23;
		sendData[1] = 0x23;
		sendData[2] = (byte)0xF1;
		sendData[3] = (byte)0xFE;
		byte[] vinBytes= vin.getBytes("US-ASCII");
		for(int i = 4; i < 21; i++) {
			sendData[i] = vinBytes[i - 4];
		}
		sendData[21] = 0x01;
		
		byte[] dateBytes = StaticMethod.getTimeBytes(0);
		for(int i = 0; i < 6; i++) {
			sendData[24 + i] = dateBytes[i];
		}
		byte[] isize = ByteArrayUtil.getByteArrayLowEnd(idx);
		System.arraycopy(isize, 0, sendData, 30, 2);
		byte crc8 = CRC8.calcCrc8(data);
		sendData[32] = crc8;
		System.arraycopy(data, 0, sendData, 33, data.length);
		
		short dataLength = (short)(data.length + 9);
		byte[] dsize = ByteArrayUtil.getByteArrayLowEnd(dataLength);
		System.arraycopy(dsize, 0, sendData, 22, 2);
		
		byte bcc = Bcc.getBccWithoutEnd(sendData);
		sendData[len - 1] = bcc;
		
		int total = len + 64;
		byte[] dp = new byte[total];
		dp[0] = 0x24;
		dp[1] = 0x25;
		dp[total - 2] = 0x25;
		dp[total - 1] = 0x24;
		System.arraycopy(clientId.getBytes(), 0, dp, 2, 60);
		System.arraycopy(sendData, 0, dp, 62, len);
		
		return dp;
	}
}
