package io.github.emersondll.transactions.service.impl;

import java.util.Objects;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import io.github.emersondll.transactions.service.RabbitMqService;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link RabbitMqService} that delegates message publishing
 * to Spring AMQP's {@link RabbitTemplate}.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Publishing domain event messages to pre-configured RabbitMQ queues.</li>
 * </ul>
 *
 * <p>Thread Safety: Stateless service; thread-safe as a Spring singleton.
 * {@link RabbitTemplate} is also thread-safe.</p>
 *
 * @author Emerson Lima
 * @version 1.0
 * @since 1.0.0
 * @see io.github.emersondll.transactions.config.RabbitMqConnections for queue declarations
 */
@Service
@Slf4j
public class RabbitMqServiceImpl implements RabbitMqService {

    private final RabbitTemplate template;

    /**
     * Constructor-based dependency injection.
     *
     * @param template Spring AMQP template for message publishing (non-null)
     * @throws NullPointerException if {@code template} is null
     */
    public RabbitMqServiceImpl(RabbitTemplate template) {
        this.template = Objects.requireNonNull(template, "RabbitTemplate cannot be null");
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to {@link RabbitTemplate#convertAndSend(String, Object)}
     * which handles message conversion and routing.</p>
     */
    @Override
    public void send(String queue, Object message) {
        log.debug("Sending message to queue. queue={}", queue);
        template.convertAndSend(queue, message);
    }
}
