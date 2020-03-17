package com.codechallenge.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codechallenge.domains.Account;

public interface AccountRepository extends JpaRepository<Account, String> {

}
