package com.example.statemachinetest.entity;

import com.example.statemachinetest.domain.CardState;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "card_order")
public class CardOrder {

    @Transient
    private static final String sequenceName = "card_order_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CardState state;
}
