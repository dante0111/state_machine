package com.example.statemachinetest.config;

import com.example.statemachinetest.domain.CardEvent;
import com.example.statemachinetest.domain.CardState;
import com.example.statemachinetest.service.CardOrderServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

@EnableStateMachineFactory
@Configuration
public class StateMachineConfig extends StateMachineConfigurerAdapter<CardState, CardEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<CardState, CardEvent> states) throws Exception {
        states.withStates()
                .initial(CardState.NEW)
                .states(EnumSet.allOf(CardState.class))
                .end(CardState.CANCELLED)
                .end(CardState.DELIVERED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<CardState, CardEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(CardState.NEW)
                .target(CardState.PENDING)
                .event(CardEvent.PEND)
                .guard(guard())
                .action(pendCardOrder())
                .and()
                .withExternal()
                .source(CardState.NEW)
                .target(CardState.CANCELLED)
                .event(CardEvent.CANCEL)
                .guard(guard())
                .and()
                .withExternal()
                .source(CardState.PENDING)
                .target(CardState.READY)
                .event(CardEvent.PROCESS)
                .guard(guard())
                .and()
                .withExternal()
                .source(CardState.PENDING)
                .target(CardState.CANCELLED)
                .event(CardEvent.CANCEL)
                .guard(guard())
                .and()
                .withExternal()
                .source(CardState.READY)
                .target(CardState.DELIVERED)
                .event(CardEvent.DELIVER)
                .guard(guard());
    }

    @Bean
    public Action<CardState, CardEvent> pendCardOrder() {
        return context -> {
            final Long orderId = context.getExtendedState().get(CardOrderServiceImpl.CARD_ORDER_ID, Long.class);
            System.out.println(String.format("CardOrder with id={%s} is in pending state", orderId));
        };
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<CardState, CardEvent> config) throws Exception {
        StateMachineListenerAdapter<CardState, CardEvent> adapter = new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<CardState, CardEvent> from, State<CardState, CardEvent> to) {
                System.out.println(String.format("stateChanged(from: %s, to: %s)", from, to));
            }
        };

        config.withConfiguration().autoStartup(true).listener(adapter);
    }

    @Bean
    public Guard<CardState, CardEvent> guard() {
        return context -> true;
    }
}
