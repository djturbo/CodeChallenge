package com.codechallenge.exceptions;

public class NoFundsException extends Exception {
	private static final long serialVersionUID = 5356844567809364726L;

	public NoFundsException(final String errorMessage) {
		super(errorMessage);
	}
}
