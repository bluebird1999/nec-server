package com.globe_sh.cloudplatform.server.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import com.alibaba.fastjson.JSONObject;
import com.globe_sh.cloudplatform.common.util.StaticMethod;

public class StationBean implements Serializable {

	private static final long serialVersionUID = 376794392941850652L;

	private int id;
	private String stationCode;
	private Timestamp createTime;
	private String factoryCode;
	private String stationName;
	private String stationDescription;
	private int stationStatus;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}	
	public String getStationCode() {
		return stationCode;
	}
	public void setStationCode(String stationCode) {
		this.stationCode = stationCode;
	}	
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public String getFactoryCode() {
		return factoryCode;
	}
	public void setFactoryCode(String factoryCode) {
		this.factoryCode = factoryCode;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}	
	public String getStationDescription() {
		return stationDescription;
	}
	public void setStationDescription(String stationDescription) {
		this.stationDescription = stationDescription;
	}		
	public int getStationStatus() {
		return stationStatus;
	}
	public void setStationStatus(int stationStatus) {
		this.stationStatus = stationStatus;
	}
	public void fromJsonString(String jsonString) {
		JSONObject json = JSONObject.parseObject(jsonString);
		this.fromJson(json);
	}
	public void fromJson(JSONObject json) {
		this.id = json.getIntValue("id");
		this.stationCode = json.getString("station_code");
		this.createTime = StaticMethod.getTimestamp(json.getString("create_time"));
		this.factoryCode = json.getString("factory_code");
		this.stationName = json.getString("station_name");
		this.stationDescription = json.getString("station_description");
		this.stationStatus = json.getIntValue("station_status");		
	}
}


