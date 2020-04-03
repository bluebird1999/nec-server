package com.globe_sh.cloudplatform.server.entity;

import java.io.Serializable;

public class CodeEntity implements Serializable {

	private static final long serialVersionUID = 3558195671797896609L;

	private String codeId;
	private String codeName;
	private String codeType;

	public String getCodeId() {
		return codeId;
	}

	public void setCodeId(String codeId) {
		this.codeId = codeId;
	}

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	public String getCodeType() {
		return codeType;
	}

	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}

}
