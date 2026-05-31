package io.github.emersondll.transactions.config.constants;

/**
 * Constants for the predefined operation type descriptions stored in MongoDB.
 *
 * <p>These strings are used both as seed data (in {@link io.github.emersondll.transactions.config.MongoDbDDL})
 * and as match targets in the transaction service to determine routing and sign normalisation.</p>
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
public final class MongoDbOperationTypeConstants {

    /** Cash purchase operation description. Produces a debit (negative) amount. */
    public static final String BUY_AT_CASH = "COMPRA A VISTA";

    /** Instalment purchase operation description. Produces a debit (negative) amount. */
    public static final String INSTALLMENT_PURCHASE = "COMPRA PARCELADA";

    /** Withdrawal operation description. Produces a debit (negative) amount. */
    public static final String WITHDRAWAL = "SAQUE";

    /** Payment operation description. Produces a credit (positive) amount. */
    public static final String PAYMENT = "PAGAMENTO";

    private MongoDbOperationTypeConstants() {
    }
}
