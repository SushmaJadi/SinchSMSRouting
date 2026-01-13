package com.sinch.sms.routing.repository;

import com.sinch.sms.routing.entity.SMSMessageEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SMSRepository extends CrudRepository<SMSMessageEntity, Integer> {

    @Override
    Optional<SMSMessageEntity> findById(Integer integer);
}