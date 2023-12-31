package ru.practicum.mainservice.event.requestListDto;

import lombok.Data;
import ru.practicum.mainservice.request.status.RequestStatus;
import java.util.List;

@Data
public class InitialRequestListDto {

    private List<Long> requestIds;
    private RequestStatus status;

}