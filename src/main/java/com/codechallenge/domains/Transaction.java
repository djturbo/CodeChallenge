package com.codechallenge.domains;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NaturalId;

import com.codechallenge.serialization.MoneySerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;
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
@ApiModel(value = "Transaction", description = "A bank transaction")
public class Transaction implements Serializable {
	private static final long serialVersionUID = 7075931734841441869L;

	@Id
	String reference;

	@NonNull
	@NaturalId
	@JsonProperty("account_iban")
	String iban;

	ZonedDateTime date;

	@NonNull
	@JsonSerialize(using = MoneySerializer.class)
	BigDecimal amount;

	@JsonSerialize(using = MoneySerializer.class)
	BigDecimal fee;

	String description;
}
