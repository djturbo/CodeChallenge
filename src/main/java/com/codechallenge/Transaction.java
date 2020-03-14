package com.codechallenge;

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Transaction {
	@Id
	private String reference;
	private String iban;
	private Calendar date;
	private float amount;
	private float fee;
	private String description;
	public Transaction() {
		
		// TODO Auto-generated constructor stub
	}
	public Transaction(String reference, String iban, Calendar date, float amount, float fee, String description) {
		
		this.reference = reference;
		this.iban = iban;
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
	public String getIban() {
		return iban;
	}
	public void setIban(String account_iban) {
		this.iban = account_iban;
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
	
	

}
