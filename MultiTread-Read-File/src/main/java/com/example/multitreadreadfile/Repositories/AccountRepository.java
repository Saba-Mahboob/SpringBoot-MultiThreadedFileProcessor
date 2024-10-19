package com.example.multitreadreadfile.Repositories;

import com.example.multitreadreadfile.Entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,String> {
}
