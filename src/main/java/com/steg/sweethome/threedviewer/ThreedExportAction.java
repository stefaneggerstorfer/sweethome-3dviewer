package com.steg.sweethome.threedviewer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.plugin.PluginAction;

public class ThreedExportAction extends PluginAction {
	private final Home home;

	public ThreedExportAction(Home home) {
		this.home = home;
		putPropertyValue(Property.NAME, "Export ThreeD");
		putPropertyValue(Property.MENU, "Tools");
		setEnabled(true);
	}

	@Override
	public void execute() {
		try {
			Path tempDirectory = HomeToObjExporter.export(home);
			Path indexHtmlPath = tempDirectory.resolve("index.html");
			try (InputStream is = ThreedExportAction.class.getResourceAsStream("/index.html")) {
				Files.copy(is, indexHtmlPath);
			}
			Runtime.getRuntime()
					.exec(new String[] { "C:\\Program Files\\Mozilla Firefox\\firefox.exe", indexHtmlPath.toString() });
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
