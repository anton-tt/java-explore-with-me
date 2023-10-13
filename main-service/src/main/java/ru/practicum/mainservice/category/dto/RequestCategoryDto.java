package ru.practicum.mainservice.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class RequestCategoryDto {

    @NotBlank(message = "Отсутствует название категории.")
    @Size(min = 3, max = 50, message = "Количество символов в названии категории должно быть в интервале " +
            "от 3 до 50 символов.")
    private String name;

}