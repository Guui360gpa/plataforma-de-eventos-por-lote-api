package com.contatodireto.eventflow.repository;

import com.contatodireto.eventflow.model.TicketBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketBatchRepository extends JpaRepository<TicketBatch, Long> {
}
