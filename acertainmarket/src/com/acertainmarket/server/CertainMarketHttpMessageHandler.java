package com.acertainmarket.server;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.acertainmarket.business.Bid;
import com.acertainmarket.business.CertainMarket;
import com.acertainmarket.business.Item;
import com.acertainmarket.utils.CertainMarketException;
import com.acertainmarket.utils.CertainMarketMessageTags;
import com.acertainmarket.utils.CertainMarketResponse;
import com.acertainmarket.utils.CertainMarketUtility;

/**
 * CertainMarketHttpMessageHandler implements the message handler class which is
 * invoked to handle messages received by the CertainMarketHttpServerUtility. It
 * decodes the HTTP message and invokes the CertainMarket server API
 * 
 * 
 */
public class CertainMarketHttpMessageHandler extends AbstractHandler {
	private CertainMarket myMarket = null;

	public CertainMarketHttpMessageHandler(CertainMarket market) {
		myMarket = market;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		CertainMarketMessageTags messageTag;
		String requestURI;
		CertainMarketResponse certainMarketResponse = null;
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		requestURI = request.getRequestURI();
		// Need to do request multiplexing
		messageTag = CertainMarketUtility.convertURItoMessageTag(requestURI);
		if (messageTag == null) {
			System.out.println("Unknown message tag");
		} else {
			switch (messageTag) {
			case ADD_ITEMS:
				String xml = CertainMarketUtility
						.extractPOSTDataFromRequest(request);
				Set<Item> items = (Set<Item>) CertainMarketUtility
						.deserializeXMLStringToObject(xml);
				certainMarketResponse = new CertainMarketResponse();
				try {
					myMarket.addItems(items);
				} catch (CertainMarketException ex) {
					certainMarketResponse.setException(ex);
				}
				String listItemsXmlString = CertainMarketUtility
						.serializeObjectToXMLString(certainMarketResponse);
				response.getWriter().println(listItemsXmlString);
				break;
			case BID:
				xml = CertainMarketUtility.extractPOSTDataFromRequest(request);
				Set<Bid> bids = (Set<Bid>) CertainMarketUtility
						.deserializeXMLStringToObject(xml);
				certainMarketResponse = new CertainMarketResponse();
				try {
					myMarket.bid(bids);
				} catch (CertainMarketException ex) {
					certainMarketResponse.setException(ex);
				}
				String listBidsXmlString = CertainMarketUtility
						.serializeObjectToXMLString(certainMarketResponse);
				response.getWriter().println(listBidsXmlString);
				break;
			case QUERY_ITEMS:
				certainMarketResponse = new CertainMarketResponse();
				certainMarketResponse.setList(myMarket.queryItems());
				listItemsXmlString = CertainMarketUtility
						.serializeObjectToXMLString(certainMarketResponse);
				response.getWriter().println(listItemsXmlString);
				break;
			case SWITCH_EPOCH:
				xml = CertainMarketUtility.extractPOSTDataFromRequest(request);

				certainMarketResponse = new CertainMarketResponse();
				try {
					myMarket.switchEpoch();
				} catch (CertainMarketException ex) {
					certainMarketResponse.setException(ex);
				}
				String xmlString = CertainMarketUtility
						.serializeObjectToXMLString(certainMarketResponse);
				response.getWriter().println(xmlString);
				break;
			default:
				System.out.println("Invalid message tag");
				break;
			}
		}
		// Mark the request as handled so that the HTTP response can be sent
		baseRequest.setHandled(true);
	}

}
