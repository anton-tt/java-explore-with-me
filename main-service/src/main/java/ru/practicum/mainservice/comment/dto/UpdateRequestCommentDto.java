package ru.practicum.mainservice.comment.dto;

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
public class UpdateRequestCommentDto {

    @NotBlank(message = "Отсутствует новое содержание комментария.")
    @Size(min = 1, max = 1000, message = "Количество символов в комментарии к событию должно быть в интервале " +
            "от 1 до 1000 символов.")
    private String text;

}