package ru.practicum.mainservice.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class RequestLocationDto {

    @NotNull(message = "Отсутствует значение широты.")
    @DecimalMin(value = "-180.0", message = "Значение широты не может быть меньше -180 градусов.")
    @DecimalMax(value = "180.0", message = "Значение широты не может быть больше 180 градусов.")
    private Double lat;

    @NotNull(message = "Отсутствует значение долготы.")
    @DecimalMin(value = "-180.0", message = "Значение долготы не может быть меньше -180 градусов.")
    @DecimalMax(value = "180.0", message = "Значение широты не может быть больше 180 градусов.")
    private Double lon;

}