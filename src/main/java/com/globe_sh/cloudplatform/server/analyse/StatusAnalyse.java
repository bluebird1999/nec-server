package com.globe_sh.cloudplatform.server.analyse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.globe_sh.cloudplatform.common.cache.JedisOperater;
import com.globe_sh.cloudplatform.common.util.ByteArrayUtil;
import com.globe_sh.cloudplatform.common.util.CRC8;
import com.globe_sh.cloudplatform.common.util.StaticMethod;
import com.globe_sh.cloudplatform.common.util.StaticVariable;
import com.globe_sh.cloudplatform.common.bean.DataPackage;
import com.globe_sh.cloudplatform.server.entity.EventMessage;
import com.globe_sh.cloudplatform.server.entity.EventStatusMessage;
import com.globe_sh.cloudplatform.server.entity.DecoderBean;
import com.globe_sh.cloudplatform.server.entity.StationBean;
import com.globe_sh.cloudplatform.server.explain.ExplainFactory;
import com.globe_sh.cloudplatform.server.explain.Resolve;
import com.globe_sh.cloudplatform.server.manager.SpringManager;
import com.globe_sh.cloudplatform.server.mq.ActivemqOperater;
import com.globe_sh.cloudplatform.server.service.StationService;
import com.globe_sh.cloudplatform.server.service.StationServiceImpl;

public class StatusAnalyse extends AbstractAnalyse {

	private static Logger logger = Logger.getLogger(StatusAnalyse.class);
	
	private List<EventStatusMessage> statusList = new ArrayList<EventStatusMessage>();
	private StationService stationService;
	private String uuid;
	private Timestamp statusTime;
	private int line;
	private int station;
	private int device;
	private int dataBlock;
	private Timestamp sampleTime;
	private int realDataLength;				//real data length
	private Map<Integer, List<DecoderBean>> decoderMap = new HashMap<Integer, List<DecoderBean>>();
	private byte[] sourceData;	
	private int dataStart = 0;
	private int dataLength = 0;
	private EventMessage eventMessage = null;
	private List<String> dataBlockDecoderList;
	private DataPackage response = new DataPackage();
	
	public void nextProcess() {
		if(eventMessage != null) {
			this.exchange.getIn().setHeader("send", StaticVariable.ROUTE_HEAD_STATUS);		
			String statusJsonString = eventMessage.getJsonString();
//			logger.info("Process Message:" + statusJsonString);
			this.exchange.getIn().setBody(statusJsonString.getBytes());
			
			//***send response directly to activeMq
//			response.setSourceData( getSendData() );
//			response.setClientId( dataPackage.getClientId() );
//			response.pack();
//			ActivemqOperater.getInstance().sendMessage("data-south", response.getTargetData());			
		}
	}
	
	public void processMessage() {
		if(!auth)
		{
			logger.info("Authentification failed!");
			return;
		}
		
		stationService = (StationServiceImpl)SpringManager.registerSpring().getApplicationContext().getBean("stationService");
		sourceData = this.dataPackage.getSourceData();
		
//		logger.info("Data: " + StaticMethod.bytesToHexString(sourceData));
		station = ByteArrayUtil.getIntLowEnd(sourceData, StaticVariable.PROTOCOL_CONTROL_STATION_START); 	
		uuid = StaticMethod.get32UUID();
		statusTime = StaticMethod.getTimestampOrigin(sourceData, StaticVariable.PROTOCOL_CONTROL_DATATIME_START);
		dataLength = ByteArrayUtil.getShortLowEnd(sourceData, StaticVariable.PROTOCOL_CONTROL_DATALENGTH_START);
		line = ByteArrayUtil.getIntLowEnd(sourceData, StaticVariable.PROTOCOL_CONTROL_LINE_START); 
		device = ByteArrayUtil.getIntLowEnd(sourceData, StaticVariable.PROTOCOL_CONTROL_DEVICE_START);
		dataBlock = ByteArrayUtil.getIntLowEnd(sourceData, StaticVariable.PROTOCOL_CONTROL_DB_START);	
		sampleTime = StaticMethod.getTimestampOrigin(sourceData, StaticVariable.PROTOCOL_CONTROL_SAMPLETIME_START);
		realDataLength = ByteArrayUtil.getShortLowEnd(sourceData, StaticVariable.PROTOCOL_CONTROL_REALDATALENGTH_START);
		StationBean stationBean = stationService.getStationBean(station);
		if(stationBean == null ) {
			logger.info("invalid station bean");
			return;
		}
		dataBlockDecoderList = JedisOperater.getDataDecoder(String.valueOf(dataBlock));
		if(StaticMethod.isNull(dataBlockDecoderList)) {
			logger.warn("Cannot find data decoder of station： " + station + " device: " + device + " data block: " + dataBlock );
			return;
		}
		
		buildDecoderMap();
		eventMessage = new EventMessage();
		eventMessage.setDataBatchId(uuid);
		eventMessage.setSampleTime(sampleTime);
		eventMessage.setDecodedTime(new Timestamp(System.currentTimeMillis()));
		eventMessage.setStation(station);
		eventMessage.setDataBlock(dataBlock);
		eventMessage.setDevice(device);
		eventMessage.setLine(line);
		eventMessage.setFactory(stationBean.getFactoryId());
		executeAnalyse();
		eventMessage.setStatusList(statusList);
	}
	
	/**Decode every necessary data**/
	private void executeAnalyse() {
		logger.info("Begin analyse station data of id: " + this.station);
		dataStart = StaticVariable.PROTOCOL_CONTROL_DATA_START;
		Resolve resolve = null;
		int tmpStart = dataStart;
		List<DecoderBean> infoList = getProtocolByInfoFlag(this.dataBlock);
		for(DecoderBean bean : infoList) {
			resolve = ExplainFactory.getInstance().getResolve(bean);
			resolve.preExecute(sourceData, tmpStart, uuid, statusList);
			resolve.execute();
		}
	}
	
	private void buildDecoderMap() {
		DecoderBean bean = null;
		for(String decoder : dataBlockDecoderList) {
			bean = new DecoderBean();
			bean.fromJsonString(decoder);
			List<DecoderBean> list = decoderMap.get(this.dataBlock);
			if(list == null)
				list = new ArrayList<DecoderBean>();
			list.add(bean);
			decoderMap.put(this.dataBlock, list);
		}
		for (Map.Entry<Integer,List<DecoderBean>> entry : decoderMap.entrySet()) {
			List<DecoderBean> list = entry.getValue();
			Integer param = entry.getKey();
			logger.info("The decoder build code: " + param + "\tSize: " + list.size());
		}
	}
	
	private List<DecoderBean> getProtocolByInfoFlag(Integer paramCode) {
		return decoderMap.get(paramCode);
	}
	
	private byte[] getSendData() {	
		int staticLength = 45;
		byte[] data = new byte[staticLength];
	
		data[0] = (byte)0x23;        
		data[1] = (byte)0x23;
		data[2] = (byte)0x02; 
		data[3] = (byte)0x00;
		data[4] = (byte)0x00;
		data[5] = (byte)0x00;
	
		int station = eventMessage.getStation();
		System.arraycopy(StaticMethod.intToByteArray(station), 0, data, StaticVariable.PROTOCOL_CONTROL_STATION_START, 4);		
		Timestamp st = eventMessage.getDecodedTime();
		Timestamp sp = eventMessage.getSampleTime();
	
		byte[] st_byte = StaticMethod.timeStampToBytes(st);
		System.arraycopy(st_byte, 0, data, StaticVariable.PROTOCOL_CONTROL_DATATIME_START, 8);
		
		data[StaticVariable.PROTOCOL_CONTROL_DATALENGTH_START] = (byte)0x00;
		data[StaticVariable.PROTOCOL_CONTROL_DATALENGTH_START+1] = (byte)0x18;	//24 bytes for 02 data without actual data (m bytes)
	
		int line = eventMessage.getDevice();
		System.arraycopy(StaticMethod.intToByteArray(line), 0, data, StaticVariable.PROTOCOL_CONTROL_LINE_START, 4);
		
		int device = eventMessage.getDevice();
		System.arraycopy(StaticMethod.intToByteArray(device), 0, data, StaticVariable.PROTOCOL_CONTROL_DEVICE_START, 4);		
		data[StaticVariable.PROTOCOL_CONTROL_DEVICE_START+4] = (byte)0x00;
		data[StaticVariable.PROTOCOL_CONTROL_DEVICE_START+5] = (byte)0x00;
		
		int db = eventMessage.getDataBlock();
		System.arraycopy(StaticMethod.intToByteArray(db), 0, data, StaticVariable.PROTOCOL_CONTROL_DB_START, 4);
		
		byte[] sp_byte = StaticMethod.timeStampToBytes(sp);
		System.arraycopy(sp_byte, 0, data, StaticVariable.PROTOCOL_CONTROL_SAMPLETIME_START, 8);
		
		data[StaticVariable.PROTOCOL_CONTROL_REALDATALENGTH_START] = (byte)0x00;
		data[StaticVariable.PROTOCOL_CONTROL_REALDATALENGTH_START+1] = (byte)0x00;
		
		data[44] = CRC8.calcCrc8WithoutPrefix(data);
		
		return data;
	}	
}