package com.globe_sh.cloudplatform.server.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.globe_sh.cloudplatform.server.base.BaseDAO;
import com.globe_sh.cloudplatform.server.base.BaseServiceImpl;
import com.globe_sh.cloudplatform.server.dao.CodeDAO;
import com.globe_sh.cloudplatform.server.entity.CodeEntity;

import io.netty.util.internal.StringUtil;

@Service("codeService")
public class CodeServiceImpl extends BaseServiceImpl<CodeEntity, String> implements CodeService {

	@Autowired
    private CodeDAO codeDAO;
	
	@Override
	public BaseDAO<CodeEntity, String> getDAO() {
		
		return codeDAO;
	}
	
	public String getCodeName(String codeId, String codeType) {
		String codeName = codeDAO.getCodeName(codeId, codeType);
		
		return StringUtil.isNullOrEmpty(codeName) ? "Unknown: " : codeName;
	}
	
	public List<CodeEntity> getAllCode() {
		
		return codeDAO.getAllCode();
	}
	
	public List<CodeEntity> getStatusAttribute() {
		
		return codeDAO.getStatusAttribute();
	}
	
	
}
