package com.codechallenge.domains;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@RequiredArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "transaction_7075931734841441869")
public class Transaction implements Serializable {
	private static final long serialVersionUID = 7075931734841441869L;

	@Id
	String reference;
	@NonNull
	@JsonProperty("account_iban")
	String iban;
	ZonedDateTime date;
	@NonNull
	BigDecimal amount;
	BigDecimal fee;
	String description;
}
