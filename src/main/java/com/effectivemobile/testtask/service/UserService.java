package com.effectivemobile.testtask.service;

import com.effectivemobile.testtask.model.User;
import com.effectivemobile.testtask.repo.UserRepo;
import com.github.javafaker.Faker;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;

@Service
public class UserService {
    @Autowired
    private UserRepo repo;

    //Заполняем БД тестовыми данными
    @PostConstruct
    public void fillDatabase() {
        Faker faker = new Faker();
        for (int i=0; i<100; i++) {

            User u = new User();
            u.setFirstName(faker.name().firstName());
            u.setLastName(faker.name().lastName());
            u.setEmail(faker.internet().emailAddress());
            u.setTelephone(faker.phoneNumber().cellPhone());
            u.setInitialBalance(10000 + (long) (Math.random() * 10000));
            u.setCurrentBalance(u.getInitialBalance());
            u.setBirthDate(faker.date().birthday(20, 60).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

            repo.save(u);
        }
    }
}
