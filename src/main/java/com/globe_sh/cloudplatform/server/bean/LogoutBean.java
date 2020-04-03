package com.globe_sh.cloudplatform.server.bean;

import java.sql.Timestamp;

public class LogoutBean {

	private String station;
	private String clientId;
	private Timestamp logoutTime;
	private short loginSeq;
	private byte logoutResult;
	
	public LogoutBean(String clientId, String station, Timestamp logoutTime, short loginSeq) {
		this.clientId = clientId;
		this.station = station;
		this.logoutTime = logoutTime;
		this.loginSeq = loginSeq;
		this.logoutResult = (byte)0x00;
	}

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public short getLoginSeq() {
		return loginSeq;
	}

	public void setLoginSeq(short loginSeq) {
		this.loginSeq = loginSeq;
	}

	public byte getLogoutResult() {
		return logoutResult;
	}

	public void setLogoutResult(byte logoutResult) {
		this.logoutResult = logoutResult;
	}

	public Timestamp getLogoutTime() {
		return logoutTime;
	}

	public void setLogoutTime(Timestamp logoutTime) {
		this.logoutTime = logoutTime;
	}
	
}
