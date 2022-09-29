package com.example.statemachinetest.service;

import com.example.statemachinetest.domain.CardEvent;
import com.example.statemachinetest.domain.CardState;
import com.example.statemachinetest.entity.CardOrder;
import com.example.statemachinetest.repository.CardOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class CardOrderStateChangeInterceptor extends StateMachineInterceptorAdapter<CardState, CardEvent> {

    private final CardOrderRepository cardOrderRepository;

    @Override
    public void preStateChange(State<CardState, CardEvent> state, Message<CardEvent> message, Transition<CardState, CardEvent> transition, StateMachine<CardState, CardEvent> stateMachine, StateMachine<CardState, CardEvent> rootStateMachine) {
        Optional.ofNullable(message).flatMap(msg -> Optional.ofNullable((Long) msg.getHeaders().getOrDefault(CardOrderServiceImpl.CARD_ORDER_ID, -1L))).ifPresent(cardOrderId -> {
            CardOrder cardOrder = cardOrderRepository.findById(cardOrderId).orElseThrow();
            cardOrder.setState(state.getId());
            cardOrderRepository.save(cardOrder);
        });
    }
}
