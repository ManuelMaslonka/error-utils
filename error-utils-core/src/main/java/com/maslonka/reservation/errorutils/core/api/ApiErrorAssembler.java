package com.maslonka.reservation.errorutils.core.api;

import java.util.List;
import java.util.Map;

public class ApiErrorAssembler {

    public ApiError assemble(ApiErrorInput input) {
        ErrorCode errorCode = input.errorCode();
        List<FieldViolation> violations = input.violations() == null ?
                                          List.of() :
                                          List.copyOf(input.violations());
        Map<String, Object> metadata = input.metadata() == null ?
                                       Map.of() :
                                       Map.copyOf(input.metadata());

        return new ApiError(input.timestamp(),
                            errorCode.httpStatus(),
                            HttpStatusNameResolver.resolve(errorCode.httpStatus()),
                            errorCode.code(),
                            input.message(),
                            input.path(),
                            input.correlationId(),
                            input.traceId(),
                            violations,
                            metadata);
    }
}
