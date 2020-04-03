package com.globe_sh.cloudplatform.server;

import com.globe_sh.cloudplatform.server.manager.OnlineManager;
import com.globe_sh.cloudplatform.server.manager.SpringManager;

public class ServerMain {

	public void init() {
		SpringManager.registerSpring();
		OnlineManager.getInstance().rebuildLoginMap();
	}
	
	public void run() {
		OnlineManager.getInstance().startMonitor();
	}
	
	public static void main(String args[]) {	
		ServerMain server = new ServerMain();
		server.init();
		server.run();
	}
}
