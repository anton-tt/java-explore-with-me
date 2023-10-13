package ru.practicum.mainservice.event.requestListDto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.mainservice.request.dto.ResponseRequestDto;
import java.util.List;

@Data
@Builder
public class ResultRequestListDto {

    List<ResponseRequestDto> confirmedRequests;
    List<ResponseRequestDto> rejectedRequests;

}