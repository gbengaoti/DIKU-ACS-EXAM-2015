package com.acertainmarket.client;
import java.util.List;
import java.util.Set;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.acertainmarket.interfaces.AuctionMarket;
import com.acertainmarket.utils.CertainMarketException;
import com.acertainmarket.utils.CertainMarketMessageTags;
import com.acertainmarket.utils.CertainMarketUtility;
import com.acertainmarket.business.Bid;
import com.acertainmarket.business.Item;
import com.acertainmarket.client.CertainMarketClientConstants;

/**
 * CertainMarketHTTPProxyHTTPProxy implements the client level synchronous
 * CertainBookStore API declared in the CertainMarket class
 * 
 */
public class CertainMarketHTTPProxy implements AuctionMarket {
	protected HttpClient client;
	protected String serverAddress;

	/**
	 * Initialize the client object
	 */
	public CertainMarketHTTPProxy(String serverAddress) throws Exception {
		setServerAddress(serverAddress);
		client = new HttpClient();
		client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
		// max concurrent connections to every address
		client.setMaxConnectionsPerAddress(CertainMarketClientConstants.CLIENT_MAX_CONNECTION_ADDRESS);
		client.setThreadPool(new QueuedThreadPool(
				CertainMarketClientConstants.CLIENT_MAX_THREADSPOOL_THREADS));// max threads
		// if no server replies the request expires
		client.setTimeout(CertainMarketClientConstants.CLIENT_MAX_TIMEOUT_MILLISECS);
		client.start();
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	@Override
	public void addItems(Set<Item> items) throws CertainMarketException {
		ContentExchange exchange = new ContentExchange();
		String urlString = serverAddress + "/"
				+ CertainMarketMessageTags.ADD_ITEMS;
		String listISBNsxmlString = CertainMarketUtility
				.serializeObjectToXMLString(items);
		exchange.setMethod("POST");
		exchange.setURL(urlString);
		Buffer requestContent = new ByteArrayBuffer(listISBNsxmlString);
		exchange.setRequestContent(requestContent);
		CertainMarketUtility.SendAndRecv(this.client, exchange);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Item> queryItems() throws CertainMarketException {
		ContentExchange exchange = new ContentExchange();
		String urlString = serverAddress + "/"
				+ CertainMarketMessageTags.QUERY_ITEMS;
		exchange.setURL(urlString);
		return (List<Item>) CertainMarketUtility.SendAndRecv(this.client,
				exchange);
	}

	@Override
	public void bid(Set<Bid> bids) throws CertainMarketException {
		ContentExchange exchange = new ContentExchange();
		String urlString = serverAddress + "/" + CertainMarketMessageTags.BID;
		String listISBNsxmlString = CertainMarketUtility
				.serializeObjectToXMLString(bids);
		exchange.setMethod("POST");
		exchange.setURL(urlString);
		Buffer requestContent = new ByteArrayBuffer(listISBNsxmlString);
		exchange.setRequestContent(requestContent);
		CertainMarketUtility.SendAndRecv(this.client, exchange);
	}

	@Override
	public void switchEpoch() throws CertainMarketException {
		ContentExchange exchange = new ContentExchange();
		String urlString;
		urlString = serverAddress + "/" + CertainMarketMessageTags.SWITCH_EPOCH;

		String test = "test";
		exchange.setMethod("POST");
		exchange.setURL(urlString);
		Buffer requestContent = new ByteArrayBuffer(test);
		exchange.setRequestContent(requestContent);

		CertainMarketUtility.SendAndRecv(this.client, exchange);

	}

	public void stop() {
		try {
			client.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
