package ru.practicum.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.user.model.User;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u ORDER BY u.id")
    List<User> findAllOrderById();
}
