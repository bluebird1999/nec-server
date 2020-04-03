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
	private String station;
	private String device;	
	private String dataBlock;
	private Timestamp decodedTime;
	private Timestamp sampleTime;
	private List<EventStatusMessage> statusList;
	
	public String getDataBatchId() {
		return dataBatchId;
	}
	public void setDataBatchId(String dataBatchId) {
		this.dataBatchId = dataBatchId;
	}
	public String getStation() {
		return station;
	}
	public void setStation(String station) {
		this.station = station;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public String getDataBlock() {
		return dataBlock;
	}
	public void setDataBlock(String dataBlock) {
		this.dataBlock = dataBlock;
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
				+ ", data_block=" + dataBlock 
				+ ", device=" + device 
				+ ", station=" + station				
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
		this.device = json.getString("device");
		this.dataBlock = json.getString("data_block");			
		this.station = json.getString("station");
		
		JSONArray statusArray = json.getJSONArray("data");
		statusList = new ArrayList<EventStatusMessage>();
		JSONObject statusJson = null;
		EventStatusMessage obj = null;
		for(int i = 0; i < statusArray.size(); i++) {
			statusJson = statusArray.getJSONObject(i);
			obj = new EventStatusMessage(dataBatchId, statusJson.getString("code"), statusJson.getString("value"));
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
		json.put("data_block", this.dataBlock);
		json.put("device", this.device);
		json.put("station", this.station);
		
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