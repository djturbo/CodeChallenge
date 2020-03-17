package com.codechallenge;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Transaction implements Serializable {
	private static final long serialVersionUID = 7075931734841441869L;

	@Id
	String reference;
	String iban;
	Calendar date;
	float amount;
	float fee;
	String description;
}
