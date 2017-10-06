package com.steg.sweethome.threedviewer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.eteks.sweethome3d.model.Home;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class WebServer extends NanoHTTPD {
	private static final int PORT = 8080;

	private static final String OBJ_FILE_NAME = "/output.obj";
	private static final String MTL_FILE_NAME = "/output.mtl";
	private static final double SCALE = 0.035;

	private final Home home;

	private Path currentDirectory;

	public WebServer(Home home) {
		super(PORT);
		this.home = home;
	}

	@Override
	public Response serve(IHTTPSession session) {
		log("HTTP request: " + session.getUri());

		String uri = session.getUri();
		if ("/".equals(uri)) {
			long start = System.currentTimeMillis();
			currentDirectory = HomeToObjExporter.export(home);
			log("Converting to OBJ took " + (System.currentTimeMillis() - start) + " ms");
			return getIndexHtml();
		} else {
			if (currentDirectory == null) {
				return newFixedLengthResponse("");
			}
			if (uri.contains("/")) {
				uri = uri.substring(uri.lastIndexOf('/') + 1);
			}
			Path path = currentDirectory.resolve(uri);
			log("Serving " + path.toFile().getAbsolutePath());
			return sendFile(path);
		}
	}

	private void log(String string) {
		if (false) {
			System.err.println(string);
		}
	}

	private Response sendFile(Path objPath) {
		try {
			return newFixedLengthResponse(Status.OK, "", Files.newInputStream(objPath), Files.size(objPath));
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to send " + HomeToObjExporter.OUTPUT_OBJ, e);
		}
	}

	private Response getIndexHtml() {
		String camera = home.getCamera().getX() * SCALE + " " +
				home.getCamera().getZ() * SCALE + " " +
				home.getCamera().getY() * SCALE;
		log("Camera: " + camera);

		return newFixedLengthResponse(
				"<!DOCTYPE html>" +
						"<html>" +
						"  <head>" +
						"    <meta charset=\"utf-8\">" +
						"    <title>" + home.getName() + "</title>" +
						"    <script src=\"https://aframe.io/releases/0.7.0/aframe.min.js\"></script>" +
						"  </head>" +
						"  <body>" +
						"	<a-scene inspector>" +
						"	  <a-assets>" +
						"		<a-asset-item id=\"obj\" src=\"" + OBJ_FILE_NAME + "\"></a-asset-item>" +
						"		<a-asset-item id=\"mtl\" src=\"" + MTL_FILE_NAME + "\"></a-asset-item>" +
						"	  </a-assets>" +
						"	  <a-entity obj-model=\"obj: #obj; mtl: #mtl\" scale=\"" + SCALE + " " + SCALE + " " + SCALE
						+ "\"></a-entity>" +
						"      " +
						"	  <a-camera position=\"" + camera + "\" user-height=\"0\" />" +
						"	</a-scene>" +
						"" +
						"    <script>" +
						"      var scene = document.querySelector('a-scene');" +
						"      var camera = document.querySelector('a-camera');" +
						"      camera.object3D.rotation.set(1,1,1);" +
						"    </script>" +
						"  </body>" +
						"</html>");
	}
}