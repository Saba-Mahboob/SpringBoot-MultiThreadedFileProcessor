package com.example.multitreadreadfile.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@Entity
public class Customer {
    private String recordNumber;
    @Id
    private String id;
    private String name;
    private String lastName;
    private String address;
    private String zipCode;
    private String nationalId;
    private String birthDate;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accountList = new ArrayList<>();


    public Customer(String recordNumber, String id, String name, String lastName, String address, String zipCode, String nationalId, String birthDate) {
        this.recordNumber = recordNumber;
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.address = address;
        this.zipCode = zipCode;
        this.nationalId = nationalId;
        this.birthDate = birthDate;

    }
}

