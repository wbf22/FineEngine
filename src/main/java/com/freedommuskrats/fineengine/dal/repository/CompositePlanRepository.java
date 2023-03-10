package com.freedommuskrats.fineengine.dal.repository;

import com.freedommuskrats.fineengine.service.comparison.CompositePlan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompositePlanRepository extends CrudRepository<CompositePlan, Long> {
}
