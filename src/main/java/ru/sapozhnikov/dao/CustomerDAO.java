package ru.sapozhnikov.dao;

import org.springframework.data.repository.CrudRepository;
import ru.sapozhnikov.entity.Customer;

public interface CustomerDAO extends CrudRepository<Customer, Integer> {
}
