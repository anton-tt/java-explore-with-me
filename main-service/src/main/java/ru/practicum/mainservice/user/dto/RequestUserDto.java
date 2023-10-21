package ru.practicum.mainservice.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class RequestUserDto {

    @NotEmpty(message = "Отсутствует значение электронной почты.")
    @Size(min = 6, max = 254, message = "Количество символов в эл.почте должно быть в интервале от 6 до 254 символов.")
    @Email
    private String email;

    @NotBlank(message = "Отсутствует значение имени пользователя.")
    @Size(min = 2, max = 250, message = "Количество символов в имени должно быть в интервале от 2 до 250 символов.")
    private String name;

}