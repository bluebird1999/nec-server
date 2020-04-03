package com.globe_sh.cloudplatform.server.service;

import java.util.List;

import com.globe_sh.cloudplatform.server.base.BaseService;
import com.globe_sh.cloudplatform.server.entity.CodeEntity;

public interface CodeService extends BaseService<CodeEntity, String> {

	String getCodeName(String codeId, String codeType);
	List<CodeEntity> getAllCode();
	List<CodeEntity> getStatusAttribute();
	
}
