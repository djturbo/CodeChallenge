package com.codechallenge.models.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TransactionChannel {
	CLIENT("CLIENT"), ATM("ATM"), INTERNAL("INTERNAL");

	private String displayName;

	TransactionChannel(final String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return this.displayName;
	}
}
