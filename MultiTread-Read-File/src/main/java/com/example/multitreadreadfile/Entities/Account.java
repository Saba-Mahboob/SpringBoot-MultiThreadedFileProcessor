package com.example.multitreadreadfile.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Account {
    private String recordNumber;
    @Id
    private String accountNumber;
    private String type;
    @ManyToOne
    @JoinColumn(name = "costumerId",nullable = false)
    private Customer customer;
    private String limitation;
    private String openDate;
    private String balance;
}
