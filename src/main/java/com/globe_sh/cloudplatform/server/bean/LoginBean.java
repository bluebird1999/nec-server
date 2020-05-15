package com.globe_sh.cloudplatform.server.bean;

import java.sql.Timestamp;

public class LoginBean {
	private int station;			//
	private String clientId;		//60 bytes
	private Timestamp loginTime;		//8 bytes
	private short loginSeq;	
	private short number;
	private byte loginResult;
	
	public LoginBean(String clientId, int station, Timestamp loginTime, short loginSeq, short number) {
		this.clientId = clientId;
		this.station = station;
		this.loginTime = loginTime;
		this.loginSeq = loginSeq;
		this.number = number;
		this.loginResult = (byte)0x00;
	}

	public int getStation() {
		return station;
	}

	public void setStation(int station) {
		this.station = station;
	}
	
	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public Timestamp getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Timestamp loginTime) {
		this.loginTime = loginTime;
	}

	public short getLoginSeq() {
		return loginSeq;
	}

	public void setLoginSeq(short loginSeq) {
		this.loginSeq = loginSeq;
	}
	
	public short getNumber() {
		return number;
	}

	public void setNumber(short number) {
		this.number = number;
	}

	public byte getLoginResult() {
		return loginResult;
	}

	public void setLoginResult(byte loginResult) {
		this.loginResult = loginResult;
	}
}
