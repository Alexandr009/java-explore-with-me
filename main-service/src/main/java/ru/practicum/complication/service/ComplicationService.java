package ru.practicum.complication.service;

import ru.practicum.complication.dto.ComplicationDtoIn;
import ru.practicum.complication.dto.ComplicationDtoOut;
import ru.practicum.complication.dto.UpdateComplicationDtoIn;

import java.util.List;

public interface ComplicationService {

    ComplicationDtoOut create(ComplicationDtoIn complicationDtoIn);
    void deleteComplication(Long complicationId);
    ComplicationDtoOut updateComplication(Long complicationId, UpdateComplicationDtoIn complicationDtoIn);
    List<ComplicationDtoOut> getComplications(Boolean pinned, Integer from, Integer size);
    ComplicationDtoOut getComplication(Long complicationId);
}
