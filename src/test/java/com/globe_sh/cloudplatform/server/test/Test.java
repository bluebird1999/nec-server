package com.globe_sh.cloudplatform.server.test;

import com.globe_sh.cloudplatform.common.util.StaticMethod;

public class Test {

	public static void main(String args[]) {
		byte b = (byte)0x0f;
		String value = "";
		try {
			int bitIndex = 2;
			b = (byte)(b >> bitIndex);
			int v = (b & (1 << 0)) + (b & (1 << 1));
			System.out.println(StaticMethod.intToHexString(v));
			value = "" + v;
			System.out.println(value);
		} catch(Exception e) {
			value = "N/A";
		}
	}
}
