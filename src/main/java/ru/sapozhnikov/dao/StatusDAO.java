package ru.sapozhnikov.dao;

import org.springframework.data.repository.CrudRepository;
import ru.sapozhnikov.entity.Status;

public interface StatusDAO extends CrudRepository<Status, Integer> {
}
