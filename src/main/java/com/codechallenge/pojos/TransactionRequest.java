package com.codechallenge.pojos;

import java.time.ZonedDateTime;

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
	String iban;
	ZonedDateTime date;
	double amount;
	double fee;
	String description;

	public Transaction getTransaction() {
		return new Transaction(this.reference, this.iban, this.date, this.amount, this.fee, this.description);
	}

}
