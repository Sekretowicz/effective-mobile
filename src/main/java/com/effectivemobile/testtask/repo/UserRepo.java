package com.effectivemobile.testtask.repo;

import com.effectivemobile.testtask.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    public User findById(long id);
    public List<User> findByBirthDateAfter(LocalDate date);
    public User findByTelephone(String telephone);
    public List<User> findByFirstNameAndLastName(String firstName, String lastName);
    public User findByEmail(String email);

    @Modifying
    @Query(value = "UPDATE users\n" +
            "SET current_balance = CASE\n" +
            "    WHEN current_balance * 1.05 <= \n" +
            "initial_balance * 2.07 THEN \n" +
            "current_balance * 1.05\n" +
            "    ELSE initial_balance * 2.07\n" +
            "    END", nativeQuery = true)
    @Transactional
    public void updateDeposit();
}
