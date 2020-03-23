package com.codechallenge.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.codechallenge.domain.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
	List<Transaction> findByIban(String iban, Sort sort);

	Transaction findByReference(String reference);
}
