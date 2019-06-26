package com.acertainmarket.server;

import com.acertainmarket.business.CertainMarket;
import com.acertainmarket.utils.CertainMarketConstants;

/**
 * Starts the certain market HTTP server that the clients will communicate with.
 */
public class CertainMarketHttpServer {
	public static void main(String[] args) {
		CertainMarket certainMarket = new CertainMarket();
		int listen_on_port = 8081;
		CertainMarketHttpMessageHandler handler = new CertainMarketHttpMessageHandler(
				certainMarket);
		String server_port_string = System
				.getProperty(CertainMarketConstants.PROPERTY_KEY_SERVER_PORT);
		if (server_port_string != null) {
			try {
				listen_on_port = Integer.parseInt(server_port_string);
			} catch (NumberFormatException ex) {
				System.err.println(ex);
			}
		}
		if (CertainMarketHttpServerUtility
				.createServer(listen_on_port, handler)) {
			;
		}
	}
}
