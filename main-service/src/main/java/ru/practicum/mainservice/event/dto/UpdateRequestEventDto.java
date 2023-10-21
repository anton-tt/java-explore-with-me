package ru.practicum.mainservice.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.mainservice.event.states.StateAction;
import ru.practicum.mainservice.location.dto.RequestLocationDto;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class UpdateRequestEventDto {

    @Size(min = 3, max = 120, message = "Количество символов в названии события должно быть в интервале " +
            "от 3 до 120 символов.")
    private String title;

    @Size(min = 20, max = 2000, message = "Количество символов в аннотации события должно быть в интервале " +
            "от 20 до 2000 символов.")
    private String annotation;

    @Size(min = 20, max = 7000, message = "Количество символов в описании события должно быть в интервале " +
            "от 20 до 7000 символов.")
    private String description;

    private StateAction stateAction;

    private  Boolean paid;

    @PositiveOrZero
    private Long category;

    private RequestLocationDto location;

    @Future(message = "Время проведения события должно быть в будущем.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @PositiveOrZero
    private  Integer participantLimit;

    private  Boolean requestModeration;

}