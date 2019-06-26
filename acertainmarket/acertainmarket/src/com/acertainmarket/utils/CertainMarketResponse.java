package com.acertainmarket.utils;

import java.util.List;

import com.acertainmarket.business.Item;

/**
 * Data Structure that we use to communicate objects and error messages from the
 * server to the client.
 * 
 */
public class CertainMarketResponse {
	private CertainMarketException exception;
	private List<?> list;

	public CertainMarketException getException() {
		return exception;
	}

	public void setException(CertainMarketException exception) {
		this.exception = exception;
	}

	public CertainMarketResponse(CertainMarketException exception,
			List<Item> list) {
		this.setException(exception);
		this.setList(list);
	}

	public CertainMarketResponse() {
		this.setException(null);
		this.setList(null);
	}

	public List<?> getList() {
		return list;
	}

	public void setList(List<?> list) {
		this.list = list;
	}
}
