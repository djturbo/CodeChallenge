package com.codechallenge.models;

import com.codechallenge.models.enums.TransactionChannel;

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
@ApiModel(value = "Transaction Status Request", description = "Params to get a transaction status")
public class StatusRequest {
	@ApiModelProperty(value = "The transaction reference number")
	String reference;

	@ApiModelProperty(value = "(Optional) The type of the channel that is asking for the status. Defaults to CLIENT", allowableValues = "CLIENT, ATM, INTERNAL", required = false)
	TransactionChannel channel;
}
