package com.example.springBatchTutorial.job.core.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountsRepository extends JpaRepository<Accounts, Integer> {
}
