package com.globe_sh.cloudplatform.server.explain;

import com.globe_sh.cloudplatform.common.util.StaticMethod;
import com.globe_sh.cloudplatform.server.entity.DecoderBean;

public class StringResolve extends AbsResolve {
	
	public StringResolve(DecoderBean decoder) {
		super(decoder);
	}
	
	public void execute() {
		int startIndex = start + decoder.getStartByte();
		int length = decoder.getDataLength();
		String value = new String(data, startIndex, length); 
		value = value.split("\u0000")[0];
		endResolve(value);
	}
}
