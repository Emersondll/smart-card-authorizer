package io.github.emersondll.transactions.service;

/**
 * Service contract for publishing messages to RabbitMQ queues.
 *
 * <p>Used as a domain event bus: whenever a significant business event occurs
 * (account creation, transaction registration), a message is sent to the
 * appropriate queue for downstream consumers.</p>
 *
 * @author Emerson Lima
 * @version 1.0
 * @since 1.0.0
 * @see io.github.emersondll.transactions.service.impl.RabbitMqServiceImpl
 */
public interface RabbitMqService {

    /**
     * Sends a message to the specified RabbitMQ queue.
     *
     * <p>The message is converted to the wire format by the underlying
     * {@link org.springframework.amqp.rabbit.core.RabbitTemplate}.</p>
     *
     * @param queue   the target queue name (non-null, must exist in the broker)
     * @param message the payload to send (non-null)
     */
    void send(String queue, Object message);
}
