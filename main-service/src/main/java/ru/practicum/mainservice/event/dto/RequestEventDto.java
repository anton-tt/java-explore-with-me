package ru.practicum.mainservice.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.mainservice.location.dto.RequestLocationDto;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class RequestEventDto {

    @NotBlank(message = "Отсутствует название события.")
    @Size(min = 3, max = 120, message = "Количество символов в названии события должно быть в интервале " +
            "от 3 до 120 символов.")
    private String title;

    @NotBlank(message = "Отсутствует аннотация события.")
    @Size(min = 20, max = 2000, message = "Количество символов в аннотации события должно быть в интервале " +
            "от 20 до 2000 символов.")
    private String annotation;

    @NotBlank(message = "Отсутствует описание события.")
    @Size(min = 20, max = 7000, message = "Количество символов в описании события должно быть в интервале " +
            "от 20 до 7000 символов.")
    private String description;

    @NotNull(message = "Не определена доступность события: необходима ли оплата.")
    private  Boolean paid;

    @NotNull(message = "Отсутствует категория события.")
    private Long categoryId;

    @NotNull(message = "Отсутствует место проведения события.")
    private RequestLocationDto location;

    @NotNull(message = "Отсутствует дата события.")
    @Future(message = "Время проведения события должно быть в будущем.")
    private LocalDateTime eventDate;

    @NotNull(message = "Не задано макимальное количество участников события.")
    private  Integer participantLimit;

    private  Boolean requestModeration;

}