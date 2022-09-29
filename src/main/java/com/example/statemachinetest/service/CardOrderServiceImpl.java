package com.example.statemachinetest.service;

import com.example.statemachinetest.domain.CardEvent;
import com.example.statemachinetest.domain.CardState;
import com.example.statemachinetest.entity.CardOrder;
import com.example.statemachinetest.repository.CardOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CardOrderServiceImpl implements CardOrderService {
    public static final String CARD_ORDER_ID = "cardOrder_id";
    private final CardOrderRepository cardOrderRepository;
    private final StateMachineFactory<CardState, CardEvent> stateMachineFactory;
    private final CardOrderStateChangeInterceptor orderStateChangeInterceptor;

    @Override
    public CardOrder create() {

        CardOrder cardOrder = CardOrder.builder()
                .state(CardState.NEW).build();

        return cardOrderRepository.save(cardOrder);
    }

    @Override
    public CardOrder pendCard(Long cardId) {
        StateMachine<CardState, CardEvent> stateMachine = buildStateMachine(cardId);
        sendEvent(cardId, stateMachine, CardEvent.PEND);
        return cardOrderRepository.findById(stateMachine.getExtendedState().get(CARD_ORDER_ID, Long.class)).orElseThrow();
    }

    @Override
    public CardOrder cancelCard(Long cardId) {
        StateMachine<CardState, CardEvent> stateMachine = buildStateMachine(cardId);
        sendEvent(cardId, stateMachine, CardEvent.CANCEL);
        return cardOrderRepository.findById(stateMachine.getExtendedState().get(CARD_ORDER_ID, Long.class)).orElseThrow();
    }

    @Override
    public CardOrder processCard(Long cardId) {
        StateMachine<CardState, CardEvent> stateMachine = buildStateMachine(cardId);
        sendEvent(cardId, stateMachine, CardEvent.PROCESS);
        return cardOrderRepository.findById(stateMachine.getExtendedState().get(CARD_ORDER_ID, Long.class)).orElseThrow();
    }

    @Override
    public CardOrder deliverCard(Long cardId) {
        StateMachine<CardState, CardEvent> stateMachine = buildStateMachine(cardId);
        sendEvent(cardId, stateMachine, CardEvent.DELIVER);
        return cardOrderRepository.findById(stateMachine.getExtendedState().get(CARD_ORDER_ID, Long.class)).orElseThrow();
    }

    private void sendEvent(Long cardId, StateMachine<CardState, CardEvent> sm, CardEvent event) {
        Message<CardEvent> msg = MessageBuilder.withPayload(event)
                .setHeader(CARD_ORDER_ID, cardId)
                .build();

        sm.sendEvent(Mono.justOrEmpty(msg)).doOnComplete(() -> System.out.println("Event hangling compele!")).subscribe();
    }

    private StateMachine<CardState, CardEvent> buildStateMachine(Long cardId) {
        CardOrder cardOrder = cardOrderRepository.findById(cardId).orElseThrow(() -> {
            throw new RuntimeException("CardOrder not found");
        });

        StateMachine<CardState, CardEvent> sm = stateMachineFactory.getStateMachine(Long.toString(cardOrder.getId()));
        sm.stopReactively().subscribe();

        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(orderStateChangeInterceptor);
                    sma.resetStateMachineReactively(new DefaultStateMachineContext<>(cardOrder.getState(), null, null, null)).subscribe();
                });

        sm.startReactively().subscribe();

        sm.getExtendedState().getVariables().put(CARD_ORDER_ID, cardOrder.getId());
        sm.getExtendedState().getVariables().put("CARDORDER", cardOrder);

        return sm;
    }
}
