package com.maslonka.reservation.errorutils.spring.web;

import com.maslonka.reservation.errorutils.core.api.ApiError;
import com.maslonka.reservation.errorutils.spring.web.advice.ErrorResponseContext;

@FunctionalInterface
public interface ErrorResponseCustomizer {

    ApiError customize(ApiError apiError, ErrorResponseContext context);
}
