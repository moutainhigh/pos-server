package com.dianba.pos.order.repository;

import com.dianba.pos.order.po.LifeOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LifeOrderJpaRepository extends JpaRepository<LifeOrder, Long> {

    LifeOrder findBySequenceNumber(String sequenceNumber);

}
