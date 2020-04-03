package com.globe_sh.cloudplatform.server.analyse;

import java.util.Arrays;
import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.globe_sh.cloudplatform.common.util.ByteArrayUtil;
import com.globe_sh.cloudplatform.common.util.StaticMethod;
import com.globe_sh.cloudplatform.common.util.StaticVariable;
import com.globe_sh.cloudplatform.server.bean.LoginBean;
import com.globe_sh.cloudplatform.server.manager.OnlineManager;
import com.globe_sh.cloudplatform.common.util.CRC8;

public class LoginAnalyse extends AbstractAnalyse {

	private static Logger logger = Logger.getLogger(LoginAnalyse.class);
	
	public void authentication() {
		
		this.auth = true;
	}
	
	public void nextProcess() {
		if(this.dataPackage.isValid()) {
			this.exchange.getIn().setHeader("send", StaticVariable.ROUTE_HEAD_SUCCESS);
			this.exchange.getIn().setBody(this.dataPackage.getTargetData());
		} else {
			this.exchange.getIn().setHeader("send", StaticVariable.ROUTE_HEAD_LOGIN);
			this.exchange.getIn().setBody(this.dataPackage.getTargetData());
		}
	}

	public void processMessage() {
		byte[] sourceData = this.dataPackage.getSourceData();
		int dataLength = sourceData.length;
		
		String station = new String( Arrays.copyOfRange(sourceData, StaticVariable.PROTOCOL_CONTROL_STATION_START, 
				StaticVariable.PROTOCOL_CONTROL_STATION_START + StaticVariable.PROTOCOL_CONTROL_STATION_LENGTH ) ).trim();
//		Timestamp loginTime = StaticMethod.getTimestampOrigin( sourceData, StaticVariable.PROTOCOL_CONTROL_DATATIME_START);
		short loginSeq = ByteArrayUtil.getShortLowEnd(sourceData, StaticVariable.PROTOCOL_CONTROL_LOGINSEQ_START);
		short number = ByteArrayUtil.getShortLowEnd(sourceData, StaticVariable.PROTOCOL_CONTROL_NUMBER_START);
		
		Timestamp loginTime = new Timestamp(System.currentTimeMillis());
		LoginBean bean = new LoginBean(this.dataPackage.getClientId(), station, loginTime, loginSeq, number);
		OnlineManager.getInstance().login(bean);
		sourceData[3] = bean.getLoginResult();
		byte bcc = CRC8.calcCrc8WithoutPrefix(sourceData);
		sourceData[dataLength - 1] = bcc;
		
		this.dataPackage.setSourceData(sourceData);
		this.dataPackage.pack();
		
		logger.info("Station: " + station + "\t Has Login And Result: " + bean.getLoginResult());
	}
}
