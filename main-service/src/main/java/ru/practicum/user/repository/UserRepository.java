package ru.practicum.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.user.model.User;


public interface UserRepository extends JpaRepository<User, Long> {

//    @Query("SELECT id, name, email FROM user " +
//    "WHERE id in "
//    )
//    User findAllByParamters (UserParameters parameters);
}
