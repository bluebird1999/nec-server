package com.globe_sh.cloudplatform.server.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import com.alibaba.fastjson.JSONObject;
import com.globe_sh.cloudplatform.common.util.StaticVariable;

public class EventStatusMessage implements Serializable {

	private static final long serialVersionUID = -7577685733460657229L;

	private String dataBatchId;
	private String attributeId;
	private String attributeValue;
	
	public EventStatusMessage() {
		
	}
	
	public EventStatusMessage(String dataBatchId, String attributeId, String attributeValue) {
		this.dataBatchId = dataBatchId;
		this.attributeId = attributeId;
		this.attributeValue = attributeValue;
	}

	public String getDataBatchId() {
		return dataBatchId;
	}
	public void setDataBatchId(String dataBatchId) {
		this.dataBatchId = dataBatchId;
	}
	public String getAttributeId() {
		return attributeId;
	}
	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}
	public String getAttributeValue() {
		return attributeValue;
	}
	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	@Override
	public String toString() {
		return "EventStatusMessage [data_batch_id=" + dataBatchId + ", code=" + attributeId + ", value="
				+ attributeValue + "]";
	}
}
