package ru.practicum.mainservice.location.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.mainservice.location.dto.RequestLocationDto;
import ru.practicum.mainservice.location.dto.ResponseLocationDto;
import ru.practicum.mainservice.location.model.Location;

@UtilityClass
public class LocationMapper {

    public Location toLocation(RequestLocationDto locationDto) {
        return Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }

    public ResponseLocationDto toResponseLocationDto(Location location) {
        return ResponseLocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }

}