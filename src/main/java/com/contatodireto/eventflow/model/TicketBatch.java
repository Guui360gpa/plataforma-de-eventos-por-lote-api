package com.contatodireto.eventflow.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@RequiredArgsConstructor
@Getter
@Setter
public class TicketBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event_id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer totQuantity;

    @Column(nullable = false)
    private Integer totAvailable;

    @Column(nullable = false)
    private Integer order;

    @Column(nullable = false)
    private LocalDateTime saleStart;

    @Column(nullable = false)
    private LocalDateTime saleEnd;

    @Column(nullable = false)
    private Integer version;

}
