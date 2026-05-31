package io.github.emersondll.transactions.config.constants;

/**
 * Constants for RabbitMQ queue names used by the smart-card-authorizer service.
 *
 * <p>Each constant corresponds to a queue declared at startup by
 * {@link io.github.emersondll.transactions.config.RabbitMqConnections} and
 * consumed by downstream event processors.</p>
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
public final class RabbitMqConstants {

    /** Queue for account creation domain events. */
    public static final String ACCOUNT = "ACCOUNT";

    /** Queue for payment transaction domain events. */
    public static final String PAYMENT = "PAYMENT";

    /** Queue for purchase transaction domain events (cash and instalment). */
    public static final String PURCHASE = "PURCHASE";

    /** Queue for withdrawal transaction domain events. */
    public static final String WITHDRAWAL = "WITHDRAWAL";

    private RabbitMqConstants() {
    }
}
