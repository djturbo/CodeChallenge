package com.codechallenge.exception;

public class NoFundsException extends RuntimeException {
	private static final long serialVersionUID = 5356844567809364726L;

	public NoFundsException(final String errorMessage) {
		super(errorMessage);
	}
}
