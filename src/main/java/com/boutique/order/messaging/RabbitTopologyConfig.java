package com.boutique.order.messaging;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class RabbitTopologyConfig {
 public static final String EXCHANGE="boutique.events";
 @Bean TopicExchange boutiqueEventsExchange(){return new TopicExchange(EXCHANGE,true,false);}
}
