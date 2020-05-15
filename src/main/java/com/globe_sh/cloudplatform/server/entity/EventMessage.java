package com.globe_sh.cloudplatform.server.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.globe_sh.cloudplatform.common.util.StaticMethod;

public class EventMessage implements Serializable {

	private static final long serialVersionUID = -1355737059240580486L;
	
	private String dataBatchId;
	private int factory;
	private int line;
	private int device;	
	private int dataBlock;
	private int station;
	private Timestamp decodedTime;
	private Timestamp sampleTime;
	private List<EventStatusMessage> statusList;
	
	public String getDataBatchId() {
		return dataBatchId;
	}
	public void setDataBatchId(String dataBatchId) {
		this.dataBatchId = dataBatchId;
	}
	public int getFactory() {
		return factory;
	}
	public void setFactory(int factory) {
		this.factory = factory;
	}
	public int getLine() {
		return line;
	}
	public void setLine(int line) {
		this.line = line;
	}	
	public int getDevice() {
		return device;
	}
	public void setDevice(int device) {
		this.device = device;
	}
	public int getDataBlock() {
		return dataBlock;
	}
	public void setDataBlock(int dataBlock) {
		this.dataBlock = dataBlock;
	}	
	public int getStation() {
		return station;
	}
	public void setStation(int station) {
		this.station = station;
	}	
	public Timestamp getDecodedTime() {
		return decodedTime;
	}
	public void setDecodedTime(Timestamp decodedTime) {
		this.decodedTime = decodedTime;
	}
	public Timestamp getSampleTime() {
		return sampleTime;
	}
	public void setSampleTime(Timestamp sampleTime) {
		this.sampleTime = sampleTime;
	}
	public List<EventStatusMessage> getStatusList() {
		return statusList;
	}
	public void setStatusList(List<EventStatusMessage> statusList) {
		this.statusList = statusList;
	}
	
	@Override
	public String toString() {
		String str = "EventMessage ["
				+ "data_batch_id=" + dataBatchId
				+ ", sample_time=" + StaticMethod.getTimeString(this.sampleTime) 				
				+ ", decoded_time=" + StaticMethod.getTimeString(0)
				+ ", station=" + station
				+ ", data_block=" + dataBlock 
				+ ", device=" + device 
				+ ", line=" + line
				+ ", factory=" + factory
				+ "]\n";
		
		for(EventStatusMessage statusMessage : statusList) {
			str = str + statusMessage.toString() + "\n";
		}
		
		return str;
	}
	
	public void fromJsonString(String text) {
		JSONObject json = JSONObject.parseObject(text);
		fromJsonObject(json);
	}
	
	public void fromJsonObject(JSONObject json) {
		this.dataBatchId = json.getString("data_batch_id");
		this.sampleTime = StaticMethod.getTimestamp(json.getString("sample_time"));
		this.decodedTime = StaticMethod.getTimestamp(json.getString("decoded_time"));
		this.factory = json.getIntValue("factory");
		this.line = json.getIntValue("line");
		this.device = json.getIntValue("device");
		this.dataBlock = json.getIntValue("data_block");			
		this.station = json.getIntValue("station");
		
		JSONArray statusArray = json.getJSONArray("data");
		statusList = new ArrayList<EventStatusMessage>();
		JSONObject statusJson = null;
		EventStatusMessage obj = null;
		for(int i = 0; i < statusArray.size(); i++) {
			statusJson = statusArray.getJSONObject(i);
			obj = new EventStatusMessage(dataBatchId, statusJson.getIntValue("code"), statusJson.getString("value"));
			statusList.add(obj);
		}
	}
	
	public String getJsonString() {
		JSONObject json = new JSONObject();
		JSONObject statusjson = null;
		JSONArray statusArray = new JSONArray();
		json.put("data_batch_id", this.dataBatchId);
		json.put("decoded_time", StaticMethod.getTimeString(0));
		json.put("sample_time", StaticMethod.getTimeString(this.sampleTime));
		json.put("station", this.station);
		json.put("data_block", this.dataBlock);
		json.put("device", this.device);
		json.put("line", this.line);
		json.put("factory", this.factory);
		
		for(EventStatusMessage obj : statusList) {
			statusjson = new JSONObject();
			statusjson.put("code", obj.getAttributeId());
			statusjson.put("value", obj.getAttributeValue());
			statusArray.add(statusjson);
		}
		json.put("data", statusArray);
		
		return json.toJSONString();
	}
}