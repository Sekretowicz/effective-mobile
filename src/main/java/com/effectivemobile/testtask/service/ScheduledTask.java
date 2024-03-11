package com.effectivemobile.testtask.service;

import com.effectivemobile.testtask.repo.UserRepo;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTask {

    @Autowired
    UserRepo userRepo;

    @Scheduled(fixedRate = 60000) // Выполнять каждую минуту
    public void executeTask() {
        userRepo.updateDeposit();
    }

}