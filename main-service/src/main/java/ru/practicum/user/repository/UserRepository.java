package ru.practicum.user.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.user.model.User;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u ORDER BY u.id")
    List<User> findAllOrderById();

    List<User> findUserByEmail(@NotNull @NotBlank @Email String email);

    @Query("SELECT u FROM User u WHERE :follower MEMBER OF u.follower")
    List<User> findUsersByFollowerContains(@Param("follower") User follower);
}
