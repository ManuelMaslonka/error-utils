package com.maslonka.reservation.errorutils.core.spi;

import java.time.Clock;

/**
 * Supplies the clock used by error handling components.
 */
@FunctionalInterface
public interface ClockProvider {

    /**
     * Returns the clock to use when creating timestamps.
     *
     * @return active clock
     */
    Clock clock();
}
