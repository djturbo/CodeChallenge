package com.codechallenge.pojos;

import java.util.Calendar;

import com.codechallenge.domains.Transaction;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionRequest {
	String reference;
	String account_iban;
	Calendar date;
	float amount;
	float fee;
	String description;

	public Transaction getTransaction() {
		return new Transaction(this.reference, this.account_iban, this.date, this.amount, this.fee, this.description);
	}

}
