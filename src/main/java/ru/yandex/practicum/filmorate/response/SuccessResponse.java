package ru.yandex.practicum.filmorate.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SuccessResponse {
    private final String status = "success";
}