package com.contatodireto.eventflow.repository;

import com.contatodireto.eventflow.Enum.Status;
import com.contatodireto.eventflow.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(Status status);
}
