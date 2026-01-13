package com.sinch.sms.routing.repository;

import com.sinch.sms.routing.entity.NumberOptoutEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OptoutRepository extends CrudRepository<NumberOptoutEntity, String> {
    @Override
    Optional<NumberOptoutEntity> findById(String aLong);

    @Override
    <S extends NumberOptoutEntity> S save(S entity);

    @Override
    void deleteById(String aLong);

    // long deleteByPhoneNumber(String phoneNumber);


}
