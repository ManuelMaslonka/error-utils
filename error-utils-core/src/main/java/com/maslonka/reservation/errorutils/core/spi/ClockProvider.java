package com.maslonka.reservation.errorutils.core.spi;

import java.time.Clock;

@FunctionalInterface
public interface ClockProvider {

    Clock clock();
}
