package com.pao.proiect.aibilet.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T, ID> {
    T create(T entity);

    void save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    void update(T entity);

    void delete(ID id);
}
