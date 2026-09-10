package com.contatodireto.eventflow.dto;

import java.time.LocalDateTime;

public record EventRequest(
        String title,
        String description,
        String local,
        LocalDateTime dateStart,
        LocalDateTime dateEnd
) {}
