package com.codechallenge.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ApiModel(value = "Transaction", description = "A bank transaction")
public class TransactionRequest {
	@ApiModelProperty(value = "(Optional) The transaction unique reference number in our system. If not present, the system will generate one", required = false)
	String reference;

	@JsonProperty("account_iban")
	@ApiModelProperty(value = "The IBAN number of the account where the transaction has happened")
	String iban;

	@ApiModelProperty(value = "(Optional) Date when the transaction took place", required = false)
	ZonedDateTime date;

	@ApiModelProperty(value = "If positive the transaction is a credit (add money) to the account. If negative it is a debit (deduct money from the account)")
	BigDecimal amount;

	@ApiModelProperty(value = "(Optional) Fee that will be deducted from the amount, regardless on the amount being positive or negative", required = false)
	BigDecimal fee;

	@ApiModelProperty(value = "(Optional) The description of the transaction", required = false)
	String description;
}
