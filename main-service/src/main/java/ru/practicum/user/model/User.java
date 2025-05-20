package ru.practicum.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    String name;
    String email;
    @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
}
