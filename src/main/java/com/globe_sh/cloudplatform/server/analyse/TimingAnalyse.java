package com.globe_sh.cloudplatform.server.analyse;

import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.globe_sh.cloudplatform.common.util.Bcc;
import com.globe_sh.cloudplatform.common.util.CRC8;
import com.globe_sh.cloudplatform.common.util.StaticMethod;
import com.globe_sh.cloudplatform.common.util.StaticVariable;

public class TimingAnalyse extends AbstractAnalyse {

	private static Logger logger = Logger.getLogger(TimingAnalyse.class);
	
	public void authentication() {
		
		this.auth = true;
	}
	
	public void nextProcess() {
		if(this.dataPackage.isValid()) {
			this.exchange.getIn().setHeader("send", StaticVariable.ROUTE_HEAD_SUCCESS);
			this.exchange.getIn().setBody(this.dataPackage.getTargetData());
		} else {
			this.exchange.getIn().setHeader("send", StaticVariable.ROUTE_HEAD_TIMING);
			this.exchange.getIn().setBody(this.dataPackage.getTargetData());
		}
	}

	public void processMessage() {
		byte[] sourceData = this.dataPackage.getSourceData();		
		byte[] data = new byte[33];
		System.arraycopy(sourceData, 0, data, 0, 24);
		Timestamp st = new Timestamp(System.currentTimeMillis());
		byte[] time = StaticMethod.timeStampToBytes(st);
		System.arraycopy(time, 0, data, 24, 8);
		data[3] = (byte)0x00;		//response
		data[22] = (byte)0x00;		//
		data[23] = (byte)0x08;		//data length
		byte bcc = CRC8.calcCrc8WithoutPrefix(data);
		data[32] = bcc;
		
		this.dataPackage.setSourceData(data);
		this.dataPackage.pack();
		
		logger.info("Response Data: " + StaticMethod.bytesToHexString(data));
	}
	
}
