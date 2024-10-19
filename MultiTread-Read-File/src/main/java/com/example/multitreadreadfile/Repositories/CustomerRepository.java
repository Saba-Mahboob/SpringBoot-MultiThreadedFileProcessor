package com.example.multitreadreadfile.Repositories;

import com.example.multitreadreadfile.Entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, String> {
}
