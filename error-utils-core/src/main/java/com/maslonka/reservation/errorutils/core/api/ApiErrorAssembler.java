package com.maslonka.reservation.errorutils.core.api;

import java.util.List;
import java.util.Map;

/**
 * Assembles normalized error input into the public {@link ApiError} payload.
 */
public class ApiErrorAssembler {

    /**
     * Creates a stateless assembler instance.
     */
    public ApiErrorAssembler() {
    }

    /**
     * Builds an immutable {@link ApiError} from the provided normalized input.
     *
     * @param input normalized error payload input
     * @return assembled API error payload
     */
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
