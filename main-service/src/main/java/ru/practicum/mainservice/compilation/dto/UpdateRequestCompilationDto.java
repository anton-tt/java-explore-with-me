package ru.practicum.mainservice.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Size;
import java.util.List;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class UpdateRequestCompilationDto {

    private List<Long> events;

    private Boolean pinned;

    @Size(min = 3, max = 120, message = "Количество символов в названии подборки должно быть в интервале " +
            "от 3 до 120 символов.")
    private String title;

}