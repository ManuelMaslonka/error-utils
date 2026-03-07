package com.maslonka.reservation.errorutils.core.api;

final class HttpStatusNameResolver {

    private HttpStatusNameResolver() {
    }

    static String resolve(int httpStatus) {
        return switch (httpStatus) {
            case 100 -> "CONTINUE";
            case 101 -> "SWITCHING_PROTOCOLS";
            case 102 -> "PROCESSING";
            case 103 -> "EARLY_HINTS";
            case 200 -> "OK";
            case 201 -> "CREATED";
            case 202 -> "ACCEPTED";
            case 203 -> "NON_AUTHORITATIVE_INFORMATION";
            case 204 -> "NO_CONTENT";
            case 205 -> "RESET_CONTENT";
            case 206 -> "PARTIAL_CONTENT";
            case 207 -> "MULTI_STATUS";
            case 208 -> "ALREADY_REPORTED";
            case 226 -> "IM_USED";
            case 300 -> "MULTIPLE_CHOICES";
            case 301 -> "MOVED_PERMANENTLY";
            case 302 -> "FOUND";
            case 303 -> "SEE_OTHER";
            case 304 -> "NOT_MODIFIED";
            case 307 -> "TEMPORARY_REDIRECT";
            case 308 -> "PERMANENT_REDIRECT";
            case 400 -> "BAD_REQUEST";
            case 401 -> "UNAUTHORIZED";
            case 402 -> "PAYMENT_REQUIRED";
            case 403 -> "FORBIDDEN";
            case 404 -> "NOT_FOUND";
            case 405 -> "METHOD_NOT_ALLOWED";
            case 406 -> "NOT_ACCEPTABLE";
            case 407 -> "PROXY_AUTHENTICATION_REQUIRED";
            case 408 -> "REQUEST_TIMEOUT";
            case 409 -> "CONFLICT";
            case 410 -> "GONE";
            case 411 -> "LENGTH_REQUIRED";
            case 412 -> "PRECONDITION_FAILED";
            case 413 -> "PAYLOAD_TOO_LARGE";
            case 414 -> "URI_TOO_LONG";
            case 415 -> "UNSUPPORTED_MEDIA_TYPE";
            case 416 -> "REQUESTED_RANGE_NOT_SATISFIABLE";
            case 417 -> "EXPECTATION_FAILED";
            case 418 -> "I_AM_A_TEAPOT";
            case 421 -> "MISDIRECTED_REQUEST";
            case 422 -> "UNPROCESSABLE_ENTITY";
            case 423 -> "LOCKED";
            case 424 -> "FAILED_DEPENDENCY";
            case 425 -> "TOO_EARLY";
            case 426 -> "UPGRADE_REQUIRED";
            case 428 -> "PRECONDITION_REQUIRED";
            case 429 -> "TOO_MANY_REQUESTS";
            case 431 -> "REQUEST_HEADER_FIELDS_TOO_LARGE";
            case 451 -> "UNAVAILABLE_FOR_LEGAL_REASONS";
            case 500 -> "INTERNAL_SERVER_ERROR";
            case 501 -> "NOT_IMPLEMENTED";
            case 502 -> "BAD_GATEWAY";
            case 503 -> "SERVICE_UNAVAILABLE";
            case 504 -> "GATEWAY_TIMEOUT";
            case 505 -> "HTTP_VERSION_NOT_SUPPORTED";
            case 506 -> "VARIANT_ALSO_NEGOTIATES";
            case 507 -> "INSUFFICIENT_STORAGE";
            case 508 -> "LOOP_DETECTED";
            case 509 -> "BANDWIDTH_LIMIT_EXCEEDED";
            case 510 -> "NOT_EXTENDED";
            case 511 -> "NETWORK_AUTHENTICATION_REQUIRED";
            default -> "UNKNOWN";
        };
    }
}
