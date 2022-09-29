package com.example.statemachinetest.service;

import com.example.statemachinetest.entity.CardOrder;

public interface CardOrderService {

    CardOrder create();

    CardOrder pendCard(Long cardId);

    CardOrder cancelCard(Long cardId);

    CardOrder processCard(Long cardId);

    CardOrder deliverCard(Long cardId);
}
