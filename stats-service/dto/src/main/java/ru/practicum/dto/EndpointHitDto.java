package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class EndpointHitDto {
    @NotBlank(message = "Отсутствует значение идентификатора сервиса (app), для которого записывается информация.")
    @Size(max = 100)
    private String app;

    @NotBlank(message = "Отсутствует значение URI, для которого был осуществлен запрос.")
    @Size(max = 100)
    private String uri;

    @NotBlank(message = "Отсутствует значение IP-адреса пользователя, осуществившего запрос.")
    @Size(max = 100)
    private String ip;

    @NotBlank(message = "Отсутствует значение даты и времени (timestamp), когда был совершен запрос к эндпоинту.")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String timestamp;

}