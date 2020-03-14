package com.codechallenge;

public class SearchForm {
	String iban;
	String sort;
	public SearchForm(String iban, String sort) {
		super();
		this.iban = iban;
		this.sort = sort;
	}
	public String getIban() {
		return iban;
	}
	public void setIban(String iban) {
		this.iban = iban;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}

	
}
