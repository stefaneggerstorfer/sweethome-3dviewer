package com.steg.sweethome.threedviewer;

import java.io.IOException;

import com.eteks.sweethome3d.plugin.Plugin;
import com.eteks.sweethome3d.plugin.PluginAction;

public class ThreedPlugin extends Plugin {

	@Override
	public PluginAction[] getActions() {
		WebServer server = new WebServer(getHome());
		try {
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
			getHomeController().getView().showError(e.getMessage());
		}

		return new PluginAction[] {
				new ThreedExportAction(getHome()),
				new ServerStatusAction(server)
		};
	}

}
