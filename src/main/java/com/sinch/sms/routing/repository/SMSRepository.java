package com.sinch.sms.routing.repository;

import com.sinch.sms.routing.entity.SMSMessageEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Primary
public interface SMSRepository extends CrudRepository<SMSMessageEntity, Integer> {

    @Override
    Optional<SMSMessageEntity> findById(Integer integer);

    @Override
    <S extends SMSMessageEntity> S save(S entity);
}