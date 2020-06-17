package com.globe_sh.cloudplatform.server.entity;

import org.apache.log4j.Logger;
import java.sql.Timestamp;
import com.alibaba.fastjson.JSONObject;
import com.globe_sh.cloudplatform.common.util.StaticMethod;

public class DecoderBean {

	private static Logger logger = Logger.getLogger(DecoderBean.class);
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDataCode() {
		return dataCode;
	}
	public void setDataCode(String dataCode) {
		this.dataCode = dataCode;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}	
	public int getDataBlock() {
		return dataBlock;
	}
	public void setDataBlock(int dataBlock) {
		this.dataBlock = dataBlock;
	}	
	public String getDataName() {
		return dataName;
	}
	public void setDataName(String dataName) {
		this.dataName = dataName;
	}		
	public String getDataDescription() {
		return dataDescription;
	}	
	public void setDataDescription(String dataDescription) {
		this.dataDescription = dataDescription;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getDataKind() {
		return dataKind;
	}
	public void setDataKind(String dataKind) {
		this.dataKind = dataKind;
	}	
	public int getStartByte() {
		return startByte;
	}
	public void setStartByte(int startByte) {
		this.startByte = startByte;
	}
	public int getStartBit() {
		return startBit;
	}
	public void setStartBit(int startBit) {
		this.startBit = startBit;
	}
	public int getDataLength() {
		return dataLength;
	}
	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}
	public double getDataPrecision() {
		return dataPrecision;
	}
	public void setDataPrecision(double dataPrecision) {
		this.dataPrecision = dataPrecision;
	}
	public int getDataDeviation() {
		return dataDeviation;
	}
	public void setDataDeviation(int dataDeviation) {
		this.dataDeviation = dataDeviation;
	}
	public int getDataUnit() {
		return dataUnit;
	}
	public void setDataUnit(int dataUnit) {
		this.dataUnit = dataUnit;
	}
	public String getDataDictionary() {
		return dataDictionary;
	}
	public void setDataDictionary(String dataDictionary) {
		this.dataDictionary = dataDictionary;
	}
	public String getLowPrecede() {
		return lowPrecede;
	}
	public void setLowPrecede(String lowPrecede) {
		this.lowPrecede = lowPrecede;
	}	
	public void fromJsonString(String jsonString) {
		try {
			JSONObject json = JSONObject.parseObject(jsonString);
			this.fromJson(json);
		} catch(Exception e) {
			e.printStackTrace();
			logger.error(jsonString);
		}
	}
	public void fromJson(JSONObject json) {
		this.id = json.getIntValue("id");													//参数表主键ID
		this.dataCode = json.getString("data_code");												//信息类型标识
		this.createTime = StaticMethod.getTimestamp(json.getString("create_time"));												//参数描述
		this.dataBlock = json.getIntValue("data_block");												//参数类型(byte,word,dword,bit,real)
		this.dataName = json.getString("data_name");
		this.dataDescription = json.getString("data_description");
		this.dataType = json.getString("data_type");
		this.dataKind = json.getString("data_kind");
		this.startByte = json.getIntValue("start_byte");												//起始字节
		this.startBit = json.getIntValue("start_bit");												//起始位
		this.dataLength = json.getIntValue("data_length");	
		this.dataPrecision = json.getDoubleValue("data_precision");								//参数精度
		this.dataDeviation = json.getIntValue("data_deviation");							//参数长度
		this.dataUnit = json.getIntValue("data_unit");												//参数单位
		this.dataDictionary = json.getString("data_dictionary");											//低字节在前
		this.lowPrecede = String.valueOf( json.getIntValue("low_precede") );
	}

	private int id;
	private String dataCode;
	private Timestamp createTime;
	private int dataBlock;
	private String dataName;
	private String dataDescription;
	private String dataType;
	private String dataKind;
	private int startByte;
	private int startBit;
	private int dataLength;
	private double dataPrecision;
	private int dataDeviation;
	private int dataUnit;
	private String dataDictionary;
	private String lowPrecede;
}