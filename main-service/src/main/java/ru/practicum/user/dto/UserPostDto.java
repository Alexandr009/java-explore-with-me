package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserPostDto {
    @NotBlank
    @Size(min = 2, max = 250, message = "Name length must be between 2 and 250 characters.")
    String name;
    @NotNull
    @NotBlank
    @Email
    @Size(min = 6, max = 254, message = "Email length must be between 6 and 254 characters.")
    String email;
}
