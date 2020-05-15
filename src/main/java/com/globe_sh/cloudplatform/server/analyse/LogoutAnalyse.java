package com.globe_sh.cloudplatform.server.analyse;

import java.sql.Timestamp;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.globe_sh.cloudplatform.common.util.ByteArrayUtil;
import com.globe_sh.cloudplatform.common.util.CRC8;
import com.globe_sh.cloudplatform.common.util.StaticMethod;
import com.globe_sh.cloudplatform.common.util.StaticVariable;
import com.globe_sh.cloudplatform.server.bean.LogoutBean;
import com.globe_sh.cloudplatform.server.manager.OnlineManager;

public class LogoutAnalyse extends AbstractAnalyse {

	private static Logger logger = Logger.getLogger(LogoutAnalyse.class);
	public void authentication() {
		this.auth = true;
	}

	public void nextProcess() {
		if(this.dataPackage.isValid()) {
			this.exchange.getIn().setHeader("send", StaticVariable.ROUTE_HEAD_SUCCESS);
			this.exchange.getIn().setBody(this.dataPackage.getTargetData());
		} else {
			this.exchange.getIn().setHeader("send", StaticVariable.ROUTE_HEAD_LOGOUT);
			this.exchange.getIn().setBody(this.dataPackage.getTargetData());
		}
	}
	
	public void processMessage() {
		byte[] sourceData = this.dataPackage.getSourceData();
		int dataLength = sourceData.length;
		int station = ByteArrayUtil.getIntLowEnd(sourceData, StaticVariable.PROTOCOL_CONTROL_STATION_START); 	
//		Timestamp logoutTime = StaticMethod.getTimestampOrigin( sourceData, StaticVariable.PROTOCOL_CONTROL_DATATIME_START);
		short logoutSeq = ByteArrayUtil.getShortLowEnd(sourceData, StaticVariable.PROTOCOL_CONTROL_LOGINSEQ_START);
		Timestamp logoutTime = new Timestamp(System.currentTimeMillis());
		LogoutBean bean = new LogoutBean(this.dataPackage.getClientId(), station, logoutTime, logoutSeq);
		OnlineManager.getInstance().logout(bean);
		sourceData[3] = bean.getLogoutResult();
		byte bcc = CRC8.calcCrc8WithoutPrefix(sourceData);
		sourceData[dataLength - 1] = bcc;
		
		this.dataPackage.setSourceData(sourceData);
		this.dataPackage.pack();
		logger.info("Station: " + station + "\t Has Logout And Result: " + bean.getLogoutResult());
	}
}