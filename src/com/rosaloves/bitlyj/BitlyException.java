package com.rosaloves.bitlyj;

public class BitlyException extends RuntimeException {
	private static final long serialVersionUID = 8300631062123036696L;

	BitlyException(String message) {
		super(message);
	}

	BitlyException(String message, Throwable cause) {
		super(message, cause);
	}
}