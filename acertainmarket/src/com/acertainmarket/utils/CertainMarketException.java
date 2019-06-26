package com.acertainmarket.utils;

public class CertainMarketException extends Exception {

	private static final long serialVersionUID = 1L;

	public CertainMarketException() {
		super();
	}

	public CertainMarketException(String message) {
		super(message);
	}

	public CertainMarketException(String message, Throwable cause) {
		super(message, cause);
	}

	public CertainMarketException(Throwable ex) {
		super(ex);
	}

}
