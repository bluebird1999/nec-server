package com.globe_sh.cloudplatform.server.analyse;

import java.sql.Timestamp;
import java.util.Date;

import com.globe_sh.cloudplatform.common.util.CRC8;
import com.globe_sh.cloudplatform.common.util.StaticMethod;
import com.globe_sh.cloudplatform.common.util.StaticVariable;
import com.globe_sh.cloudplatform.server.manager.OnlineManager;

public class HeartAnalyse extends AbstractAnalyse {

	
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
		if (auth) {
			byte[] sourceData = this.dataPackage.getSourceData();
			String station = StaticMethod.ascii2String(sourceData, StaticVariable.PROTOCOL_CONTROL_STATION_START,
					StaticVariable.PROTOCOL_CONTROL_STATION_LENGTH);
			OnlineManager.getInstance().heart(station);
			
			byte[] data = new byte[25];
			System.arraycopy(sourceData, 0, data, 0, 24);
			data[3] = (byte)0x00;		//response
			byte bcc = CRC8.calcCrc8WithoutPrefix(data);
			data[24] = bcc;
			
			this.dataPackage.setSourceData(data);
			this.dataPackage.pack();	
		}
	}
}
