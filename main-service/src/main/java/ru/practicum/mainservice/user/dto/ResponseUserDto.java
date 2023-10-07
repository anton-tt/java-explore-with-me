package ru.practicum.mainservice.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseUserDto {

    private Long id;
    private String email;
    private String name;

}