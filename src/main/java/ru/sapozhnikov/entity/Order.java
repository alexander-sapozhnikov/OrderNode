package ru.sapozhnikov.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;



@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "offerid", referencedColumnName = "id")
    private Offer offer;
    private String name;
    @Column(name = "deliverytime")
    private LocalDateTime deliveryTime;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "statusid", referencedColumnName = "id")
    private Status status;
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "customerid", referencedColumnName = "id")
    private Customer customer;
    private boolean paid;

}
