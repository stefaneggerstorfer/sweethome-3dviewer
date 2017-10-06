package com.steg.sweethome.threedviewer;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.eteks.sweethome3d.plugin.PluginAction;

public class ServerStatusAction extends PluginAction {

	public ServerStatusAction(WebServer server) {
		String localHost = "localhost";
		try {
			localHost = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		putPropertyValue(Property.NAME,
				"Threed server running at http://" + localHost + ":" + server.getListeningPort());
		putPropertyValue(Property.MENU, "Tools");
		setEnabled(false);
	}

	@Override
	public void execute() {
	}

}
