package com.freedommuskrats.fineengine.dal.repository;

import com.freedommuskrats.fineengine.dal.models.property.Home;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeRepository extends CrudRepository<Home, Long> {
}
