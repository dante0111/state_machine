package com.example.statemachinetest.controller;

import com.example.statemachinetest.entity.CardOrder;
import com.example.statemachinetest.service.CardOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/api")
public class CardOrderController {

    private final CardOrderService cardOrderService;

    @GetMapping
    public Mono<?> create(){
        return Mono.justOrEmpty(cardOrderService.create());
    }

    @PutMapping("/pend/{id}")
    public Mono<?> pendCard(@PathVariable Long id){
        return Mono.justOrEmpty(cardOrderService.pendCard(id));
    }

    @PutMapping("/process/{id}")
    public Mono<?> processCard(@PathVariable Long id){
        return Mono.justOrEmpty(cardOrderService.processCard(id));
    }

    @PutMapping("/deliver/{id}")
    public Mono<?> deliverCard(@PathVariable Long id){
        return Mono.justOrEmpty(cardOrderService.deliverCard(id));
    }
}
