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
	private String station;
	private String device;
	private String dataBlock;
	private int dataBlockId;
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
			logger.info("Process Message:" + statusJsonString);
			this.exchange.getIn().setBody(statusJsonString.getBytes());
			
			//***send response directly to activeMq
			response.setSourceData( getSendData() );
			response.setClientId( dataPackage.getClientId() );
			response.pack();
			ActivemqOperater.getInstance().sendMessage("data-south", response.getTargetData());			
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
		
		logger.info("Data: " + StaticMethod.bytesToHexString(sourceData));
		station = StaticMethod.ascii2String(sourceData, 
				StaticVariable.PROTOCOL_CONTROL_STATION_START, StaticVariable.PROTOCOL_CONTROL_STATION_LENGTH).trim();
		uuid = StaticMethod.get32UUID();
		statusTime = StaticMethod.getTimestampOrigin(sourceData, StaticVariable.PROTOCOL_CONTROL_DATATIME_START);
		dataLength = ByteArrayUtil.getShortLowEnd(sourceData, StaticVariable.PROTOCOL_CONTROL_DATALENGTH_START);
		device = StaticMethod.ascii2String(sourceData, 
				StaticVariable.PROTOCOL_CONTROL_DEVICE_START, StaticVariable.PROTOCOL_CONTROL_DEVICE_LENGTH).trim();
		dataBlock = StaticMethod.ascii2String(sourceData, 
				StaticVariable.PROTOCOL_CONTROL_DB_START, StaticVariable.PROTOCOL_CONTROL_DB_LENGTH).trim();		
		sampleTime = StaticMethod.getTimestampOrigin(sourceData, StaticVariable.PROTOCOL_CONTROL_SAMPLETIME_START);
		realDataLength = ByteArrayUtil.getShortLowEnd(sourceData, StaticVariable.PROTOCOL_CONTROL_REALDATALENGTH_START);
		StationBean stationBean = stationService.getStationBean(station);
		if(stationBean == null ) {
			logger.info("invalid station bean");
			return;
		}
		
		String strId = JedisOperater.getDataBlock(device+dataBlock);
		dataBlockId = Integer.parseInt(strId);
		dataBlockDecoderList = JedisOperater.getDataDecoder(strId);
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
		eventMessage.setDataBlockId(dataBlockId);
		eventMessage.setDataBlock(dataBlock);
		eventMessage.setDevice(device);
		executeAnalyse();
		eventMessage.setStatusList(statusList);
	}
	
	/**Decode every necessary data**/
	private void executeAnalyse() {
		logger.info("Begin analyse station data of id: " + this.station);
		dataStart = StaticVariable.PROTOCOL_CONTROL_DATA_START;
		Resolve resolve = null;
		int tmpStart = dataStart;
		List<DecoderBean> infoList = getProtocolByInfoFlag(this.dataBlockId);
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
			List<DecoderBean> list = decoderMap.get(this.dataBlockId);
			if(list == null)
				list = new ArrayList<DecoderBean>();
			list.add(bean);
			decoderMap.put(this.dataBlockId, list);
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
		int staticLength = 53;
		byte[] data = new byte[staticLength];
	
		data[0] = (byte)0x23;        
		data[1] = (byte)0x23;
		data[2] = (byte)0x02; 
		data[3] = (byte)0x00;
		data[4] = (byte)0x00;
		data[5] = (byte)0x00;
	
		String station = eventMessage.getStation();
		System.arraycopy(station.getBytes(), 0, data, 6, station.getBytes().length);
		for(int i = station.getBytes().length; i<8; i++) {
			data[i + 6] = (byte)0x00;
		}
		
		Timestamp st = eventMessage.getDecodedTime();
		Timestamp sp = eventMessage.getSampleTime();
	
		byte[] st_byte = StaticMethod.timeStampToBytes(st);
		System.arraycopy(st_byte, 0, data, 14, 8);
		
		data[22] = (byte)0x00;
		data[23] = (byte)0x1c;	//28 bytes for 02 data without actual data (m bytes)
	
		String device = eventMessage.getDevice();
		System.arraycopy(device.getBytes(), 0, data, 24, device.getBytes().length);
		for(int i = device.getBytes().length; i<8; i++) {
			data[i + 24] = (byte)0x00;
		}
		
		data[32] = (byte)0x00;
		data[33] = (byte)0x00;
		
		String db = eventMessage.getDataBlock();
		System.arraycopy(db.getBytes(), 0, data, 34, db.getBytes().length);
		for(int i = db.getBytes().length; i<8; i++) {
			data[i + 34] = (byte)0x00;
		}
		
		byte[] sp_byte = StaticMethod.timeStampToBytes(sp);
		System.arraycopy(sp_byte, 0, data, 42, 8);
		
		data[50] = (byte)0x00;
		data[51] = (byte)0x00;
		
		data[52] = CRC8.calcCrc8WithoutPrefix(data);
		
		return data;
	}	
}