package com.effectivemobile.testtask.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "telephone", nullable = true, unique = true)
    private String telephone;

    @Column(name = "email", nullable = true, unique = true)
    private String email;

    @Column(name = "current_balance", nullable = true, unique = false)
    private double currentBalance;

    @Column(name = "initial_balance", nullable = true, unique = false)
    private double initialBalance;
}
