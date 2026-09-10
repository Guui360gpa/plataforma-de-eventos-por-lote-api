package com.contatodireto.eventflow.service;

import com.contatodireto.eventflow.Enum.Status;
import com.contatodireto.eventflow.dto.EventRequest;
import com.contatodireto.eventflow.model.Event;
import com.contatodireto.eventflow.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    public Event createEvent(EventRequest request, User organizer) {
        Event event = new Event();
        event.setTitle(request.title());
        event.setLocal(request.local());
        event.setDescription(request.description());
        event.setStartDate(request.dateStart());
        event.setEndDate(request.dateEnd());
        event.setStatus(Status.DRAFT);
        event.setOrganizer(organizer);
        return eventRepository.save(event);

    }
}
