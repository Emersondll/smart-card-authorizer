package io.github.emersondll.transactions.config;

import java.util.Objects;

import jakarta.annotation.PostConstruct;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Configuration;

import io.github.emersondll.transactions.config.constants.RabbitMqConstants;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuration class responsible for declaring RabbitMQ queues, exchange,
 * and bindings at application startup.
 *
 * <p>All queues use a {@code amq.direct} exchange and are declared as durable
 * so they survive broker restarts. Bindings use the queue name as the routing key.</p>
 *
 * <p>Declared queues:
 * <ul>
 *   <li>{@code ACCOUNT} — account creation events</li>
 *   <li>{@code PAYMENT} — payment transaction events</li>
 *   <li>{@code PURCHASE} — purchase transaction events</li>
 *   <li>{@code WITHDRAWAL} — withdrawal transaction events</li>
 * </ul>
 *
 * @author Emerson Lima
 * @version 1.0
 * @since 1.0.0
 * @see io.github.emersondll.transactions.config.constants.RabbitMqConstants for queue name constants
 */
@Configuration
@Slf4j
public class RabbitMqConnections {

    private static final String EXCHANGE_NAME = "amq.direct";

    private final AmqpAdmin amqpAdmin;

    /**
     * Constructor-based dependency injection.
     *
     * @param amqpAdmin Spring AMQP admin for declaring broker resources (non-null)
     * @throws NullPointerException if {@code amqpAdmin} is null
     */
    public RabbitMqConnections(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = Objects.requireNonNull(amqpAdmin, "AmqpAdmin cannot be null");
    }

    /**
     * Declares all queues, the direct exchange, and their bindings in the RabbitMQ broker.
     * Executed once after the Spring context is fully initialised.
     */
    @PostConstruct
    private void init() {
        log.info("Declaring RabbitMQ queues and bindings");

        DirectExchange exchange = new DirectExchange(EXCHANGE_NAME);
        amqpAdmin.declareExchange(exchange);

        declareQueueWithBinding(RabbitMqConstants.ACCOUNT, exchange);
        declareQueueWithBinding(RabbitMqConstants.PAYMENT, exchange);
        declareQueueWithBinding(RabbitMqConstants.PURCHASE, exchange);
        declareQueueWithBinding(RabbitMqConstants.WITHDRAWAL, exchange);

        log.info("RabbitMQ resources declared successfully");
    }

    /**
     * Declares a single durable queue and binds it to the exchange using
     * the queue name as the routing key.
     *
     * @param queueName the name of the queue to declare and bind (non-null)
     * @param exchange  the exchange to bind the queue to (non-null)
     */
    private void declareQueueWithBinding(String queueName, DirectExchange exchange) {
        Queue queue = new Queue(queueName, true, false, false);
        Binding binding = new Binding(
                queueName, Binding.DestinationType.QUEUE,
                exchange.getName(), queueName, null);

        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareBinding(binding);
        log.debug("Queue declared and bound. queue={}", queueName);
    }
}
