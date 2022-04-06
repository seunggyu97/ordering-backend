package com.server.ordering.domain.Order;

import com.server.ordering.domain.OrderFood;
import com.server.ordering.domain.OrderType;
import com.server.ordering.domain.Review;
import com.server.ordering.domain.member.Customer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;
import static javax.persistence.InheritanceType.*;

@Entity
@Getter
@Table(name = "orders")
@Inheritance(strategy = JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = ALL, orphanRemoval = true)
    private final List<OrderFood> orderedFoods = new ArrayList<>();

    @Column(name = "order_time")
    private LocalDateTime orderTime;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @Enumerated(value = EnumType.STRING)
    private OrderType orderType;
}