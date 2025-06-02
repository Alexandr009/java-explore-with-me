package ru.practicum.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    @NotBlank
    String name;
    @Column(name = "email", unique = true)
    @NotNull
    @NotBlank
    @Email
    String email;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
}
