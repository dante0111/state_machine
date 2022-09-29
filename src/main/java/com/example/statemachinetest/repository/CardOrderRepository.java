package com.example.statemachinetest.repository;

import com.example.statemachinetest.entity.CardOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardOrderRepository extends JpaRepository<CardOrder, Long> {

    @Override
    Optional<CardOrder> findById(Long aLong);
}
