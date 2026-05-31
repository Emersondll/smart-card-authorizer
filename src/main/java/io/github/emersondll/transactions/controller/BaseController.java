package io.github.emersondll.transactions.controller;

import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Base interface that establishes the common API version prefix for all controllers.
 *
 * <p>All controllers implementing this interface automatically inherit the
 * {@code /digital/transactions/v1} request mapping, ensuring consistent
 * API versioning across the application.</p>
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
@RequestMapping("digital/transactions/v1")
public interface BaseController {
}
