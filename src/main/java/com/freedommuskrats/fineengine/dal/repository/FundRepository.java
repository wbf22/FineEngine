package com.freedommuskrats.fineengine.dal.repository;

import com.freedommuskrats.fineengine.dal.models.investments.Fund;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FundRepository extends CrudRepository<Fund, Long> {
}
