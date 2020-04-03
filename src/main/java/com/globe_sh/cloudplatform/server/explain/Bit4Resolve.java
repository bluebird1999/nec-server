package com.globe_sh.cloudplatform.server.explain;

import com.globe_sh.cloudplatform.common.util.StaticMethod;
import com.globe_sh.cloudplatform.server.entity.DecoderBean;
import com.globe_sh.cloudplatform.server.util.DictUtil;


public class Bit4Resolve extends AbsResolve {

	
	public Bit4Resolve(DecoderBean decoder) {
		super(decoder);
	}
	
	public void execute() {
		int startIndex = start + decoder.getStartByte();
		byte b = (byte)(0xff & data[startIndex]);
		String value = "";
		try {
			int bitIndex = decoder.getStartBit();
			int v = (b & (1 << bitIndex)) + (b & (1 << (bitIndex + 1))) + (b & (1 << (bitIndex + 2))) + (b & (1 << (bitIndex + 3)));
			String dict = decoder.getDataDictionary();
			value = "" + v;
			if(!StaticMethod.isNull(dict)) {
				value = DictUtil.getInstance().getDictValue(StaticMethod.intToHexString(v), dict);
			}
		} catch(Exception e) {
			value = "N/A";
		}
		
		endResolve(value);		
	}
}
