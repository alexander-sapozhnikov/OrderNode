package ru.sapozhnikov.dao;

import org.springframework.data.repository.CrudRepository;
import ru.sapozhnikov.entity.Order;

public interface OrderDAO extends CrudRepository<Order, Integer> {
}
