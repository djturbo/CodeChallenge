package com.codechallenge;

import java.util.Calendar;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionForm {
	private String reference;
	private String account_iban;
	private Calendar date;
	private float amount;
	private float fee;
	private String description;

	public Transaction getTransaction() {
		return new Transaction(this.reference, this.account_iban, this.date, this.amount, this.fee, this.description);
	}

}
