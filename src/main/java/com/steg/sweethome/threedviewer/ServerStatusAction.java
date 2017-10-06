package com.steg.sweethome.threedviewer;

import java.awt.Desktop;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import com.eteks.sweethome3d.plugin.PluginAction;

public class ServerStatusAction extends PluginAction {

	private final String serverUri;

	public ServerStatusAction(WebServer server) {
		String localHost = "localhost";
		try {
			Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
			while (nics.hasMoreElements()) {
				NetworkInterface nic = nics.nextElement();
				Enumeration<InetAddress> inetAddresses = nic.getInetAddresses();
				while (inetAddresses.hasMoreElements()) {
					InetAddress inetAddress = inetAddresses.nextElement();
					if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
						localHost = inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

		if ("localhost".equals(localHost)) {
			try {
				localHost = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}

		serverUri = "http://" + localHost + ":" + server.getListeningPort();
		putPropertyValue(Property.NAME, "Threed server running at " + serverUri);
		putPropertyValue(Property.MENU, "Tools");
		setEnabled(true);
	}

	@Override
	public void execute() {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(Desktop.Action.BROWSE)) {
				try {
					desktop.browse(new URI(serverUri));
				} catch (IOException | URISyntaxException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
