package ru.sapozhnikov.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name="offers")
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private double price;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "paidtypeid", referencedColumnName = "id")
    private PaidType paidType;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "categoryid", referencedColumnName = "id")
    private Category category;
    @ManyToMany
    @JoinTable(name = "characteristicsoffers",
            joinColumns=@JoinColumn(name="offerid"),
            inverseJoinColumns=@JoinColumn(name="characteristicid"))
    private List<Characteristic> characteristics = new ArrayList<>();
}
