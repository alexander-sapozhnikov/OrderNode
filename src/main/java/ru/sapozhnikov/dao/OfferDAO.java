package ru.sapozhnikov.dao;

import org.springframework.data.repository.CrudRepository;
import ru.sapozhnikov.entity.Offer;

public interface OfferDAO extends CrudRepository<Offer, Integer> {
}
