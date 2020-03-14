package com.codechallenge;

import java.util.Calendar;

public class TransactionForm {
	private String reference;
	private String account_iban;
	private Calendar date;
	private float amount;
	private float fee;
	private String description;
	public TransactionForm(String reference, String account_iban, Calendar date, float amount, float fee,
			String description) {
		super();
		this.reference = reference;
		this.account_iban = account_iban;
		this.date = date;
		this.amount = amount;
		this.fee = fee;
		this.description = description;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getAccount_iban() {
		return account_iban;
	}
	public void setAccount_iban(String account_iban) {
		this.account_iban = account_iban;
	}
	public Calendar getDate() {
		return date;
	}
	public void setDate(Calendar date) {
		this.date = date;
	}
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}
	public float getFee() {
		return fee;
	}
	public void setFee(float fee) {
		this.fee = fee;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Transaction getTransaction() {
		Transaction transaction = new Transaction (reference,
				account_iban,
				date,
				amount,
				fee,
				description
				);
		return transaction;
	}
	
	
}
