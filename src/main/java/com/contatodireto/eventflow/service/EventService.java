package com.contatodireto.eventflow.service;

import com.contatodireto.eventflow.Enum.Status;
import com.contatodireto.eventflow.dto.EventRequest;
import com.contatodireto.eventflow.model.Event;
import com.contatodireto.eventflow.model.User;
import com.contatodireto.eventflow.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {


    final EventRepository eventRepository;

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
    public List<Event> listPublished() {
        return eventRepository.findByStatus(Status.PUBLISHED);
    }

    public Event getById(Long id) {
        return eventRepository.findById(id).orElseThrow(RuntimeException::new);
        // EventNotFoundException
    }

    public Event updateEvent(Long id, EventRequest request, User user) {
        Event event = getById(id);
        validarDono(event, user);

        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setLocal(request.local());
        event.setStartDate(request.dateStart());
        event.setEndDate(request.dateEnd());

        return eventRepository.save(event);
    }

    private void validarDono(Event event, User user) {
        if (!event.getOrganizer().getId().equals(user.getId())) {
            throw new RuntimeException("Você não é o dono deste evento");
        }
        //AccessDeniedException
    }

    public void deleteEvent(Long id, User user) {
        Event event = getById(id);
        validarDono(event, user);
        event.setStatus(Status.CANCELED);
        eventRepository.save(event);
    }

    public void publishEvent( Long id, User user) {
        Event event = getById(id);
        validarDono(event, user);
        if (event.getStatus() == Status.DRAFT) {
            event.setStatus(Status.PUBLISHED);
            eventRepository.save(event);
        }
    }
}

