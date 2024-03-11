package com.effectivemobile.testtask.controller;

import com.effectivemobile.testtask.model.User;
import com.effectivemobile.testtask.repo.UserRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserRepo repo;

    @Autowired
    private EntityManager em;

    //Круды
    @PostMapping("/")
    public ResponseEntity addNewUser(@RequestBody User user) {
        //Email и телефон не могут оба быть незаполненными
        if (user.getEmail()==null && user.getTelephone()==null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        user.setCurrentBalance(user.getInitialBalance());
        repo.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public User get(@PathVariable long id) {
        return repo.findById(id);
    }

    @GetMapping("/")
    public List<User> search(@RequestParam(required = false) String firstName,
                             @RequestParam(required = false) String lastName,
                             @RequestParam(required = false) LocalDate birthDate,
                             @RequestParam(required = false) String telephone,
                             @RequestParam(required = false) String email) {

        //Будем использовать Criteria API
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        //Список предикатов
        List<Predicate> predicates = new LinkedList<>();

        //Имя и фамилию ищем по фильтру like
        if (firstName != null) {
            Predicate p = cb.like(root.get("firstName"), firstName + "%");
            predicates.add(p);
        }
        if (lastName != null) {
            Predicate p = cb.like(root.get("lastName"), lastName + "%");
            predicates.add(p);
        }
        //Дата рождения
        if (birthDate != null) {
            //Попробуем
            Predicate p = cb.greaterThan(root.get("birthDate"), birthDate);
            predicates.add(p);
        }
        //Телефон
        if (telephone != null) {
            Predicate p = cb.equal(root.get("telephone"), telephone);
            predicates.add(p);
        }
        //Почта
        if (telephone != null) {
            Predicate p = cb.equal(root.get("email"), email);
            predicates.add(p);
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        TypedQuery<User> query = em.createQuery(cq);

        return query.getResultList();
    }

    @DeleteMapping("/{id}/email")
    public ResponseEntity deleteUserEmail(@PathVariable long id) {
        User u = repo.findById(id);
        //Не нашли юзера - возвращаем 404
        if (u==null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        //Оба поля не могут быть null
        if (u.getTelephone()==null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        u.setEmail(null);
        repo.save(u);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/telephone")
    public ResponseEntity deleteUserTelephone(@PathVariable long id) {
        User u = repo.findById(id);
        //Не нашли юзера - возвращаем 404
        if (u==null) {
            System.out.println("NULL USER");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        //Оба поля не могут быть null
        if (u.getEmail()==null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        u.setTelephone(null);
        repo.save(u);

        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity updateUser(@PathVariable long id,
                                     @RequestParam(required = false) String email,
                                     @RequestParam(required = false) String telephone) {
        User u = repo.findById(id);
        //Не нашли юзера - возвращаем 404
        if (u==null) {
            System.out.println("NULL USER");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        if (email != null) {
            u.setEmail(email);
        }
        if (telephone != null) {
            u.setTelephone(telephone);
        }

        repo.save(u);

        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    //Перевод денег
    @GetMapping("/transfer")
    public ResponseEntity transfer (@RequestParam long senderId,
                                    @RequestParam long receiverId,
                                    @RequestParam double amount) {
        User sender = repo.findById(senderId);
        if (sender==null) {
            //Не найден отправитель
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        if (sender.getCurrentBalance() < amount) {
            //Недостаточно средств
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        User receiver = repo.findById(receiverId);
        if (receiver==null) {
            //Не найден получатель
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        sender.setCurrentBalance(sender.getCurrentBalance()-amount);
        receiver.setCurrentBalance(receiver.getCurrentBalance()+amount);

        repo.save(sender);
        repo.save(receiver);
        return new ResponseEntity(HttpStatus.OK);
    }
}
