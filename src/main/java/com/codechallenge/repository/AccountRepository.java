package com.codechallenge.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codechallenge.domain.Account;

public interface AccountRepository extends JpaRepository<Account, String> {
	Account findByIban(String iban);
}
