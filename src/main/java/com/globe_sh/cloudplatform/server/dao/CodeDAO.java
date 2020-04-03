package com.globe_sh.cloudplatform.server.dao;

import java.util.List;

import com.globe_sh.cloudplatform.server.base.BaseDAO;
import com.globe_sh.cloudplatform.server.entity.CodeEntity;

public interface CodeDAO extends BaseDAO<CodeEntity, String> {
	   
	String getCodeName(String codeId, String codeType);
	
	List<CodeEntity> getAllCode();
	
	List<CodeEntity> getStatusAttribute();
}
