package com.globe_sh.cloudplatform.server.explain;

import com.globe_sh.cloudplatform.common.util.StaticVariable;
import com.globe_sh.cloudplatform.server.entity.DecoderBean;

public class ExplainFactory {

	private static ExplainFactory instance;
	
	private ExplainFactory() {
		
	}
	
	public static synchronized ExplainFactory getInstance() {
		if(instance == null)
			instance = new ExplainFactory();
		
		return instance;
	}
	
	public Resolve getResolve(DecoderBean bean) {
		
		if(StaticVariable.PARAMETER_TYPE_WORD.equals(bean.getDataType())) 
			return new WordResolve(bean);
		if(StaticVariable.PARAMETER_TYPE_BYTE.equals(bean.getDataType())) 
			return new ByteResolve(bean);
		if(StaticVariable.PARAMETER_TYPE_DWORD.equals(bean.getDataType())) 
			return new DwordResolve(bean);
		if(StaticVariable.PARAMETER_TYPE_BIT.equals(bean.getDataType())) 
			return new BitResolve(bean);
		if(StaticVariable.PARAMETER_TYPE_BIT2.equals(bean.getDataType())) 
			return new Bit2Resolve(bean);
		if(StaticVariable.PARAMETER_TYPE_BIT4.equals(bean.getDataType())) 
			return new Bit4Resolve(bean);
		if(StaticVariable.PARAMETER_TYPE_REAL.equals(bean.getDataType())) 
			return new RealResolve(bean);
		if(StaticVariable.PARAMETER_TYPE_INT.equals(bean.getDataType())) 
			return new IntResolve(bean);
		if(StaticVariable.PARAMETER_TYPE_DINT.equals(bean.getDataType())) 
			return new DIntResolve(bean);
		
		return null;
	}
}
