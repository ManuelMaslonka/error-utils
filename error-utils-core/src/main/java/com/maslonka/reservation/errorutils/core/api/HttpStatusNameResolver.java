package com.maslonka.reservation.errorutils.core.api;

import java.util.Map;

final class HttpStatusNameResolver {

    public static final Map<Integer, String> STATUS_NAMES = Map.<Integer, String>ofEntries(Map.entry(100, "CONTINUE"),
                                                                                           Map.entry(101, "SWITCHING_PROTOCOLS"),
                                                                                           Map.entry(102, "PROCESSING"),
                                                                                           Map.entry(103, "EARLY_HINTS"),
                                                                                           Map.entry(200, "OK"),
                                                                                           Map.entry(201, "CREATED"),
                                                                                           Map.entry(202, "ACCEPTED"),
                                                                                           Map.entry(203, "NON_AUTHORITATIVE_INFORMATION"),
                                                                                           Map.entry(204, "NO_CONTENT"),
                                                                                           Map.entry(205, "RESET_CONTENT"),
                                                                                           Map.entry(206, "PARTIAL_CONTENT"),
                                                                                           Map.entry(207, "MULTI_STATUS"),
                                                                                           Map.entry(208, "ALREADY_REPORTED"),
                                                                                           Map.entry(226, "IM_USED"),
                                                                                           Map.entry(300, "MULTIPLE_CHOICES"),
                                                                                           Map.entry(301, "MOVED_PERMANENTLY"),
                                                                                           Map.entry(302, "FOUND"),
                                                                                           Map.entry(303, "SEE_OTHER"),
                                                                                           Map.entry(304, "NOT_MODIFIED"),
                                                                                           Map.entry(307, "TEMPORARY_REDIRECT"),
                                                                                           Map.entry(308, "PERMANENT_REDIRECT"),
                                                                                           Map.entry(400, "BAD_REQUEST"),
                                                                                           Map.entry(401, "UNAUTHORIZED"),
                                                                                           Map.entry(402, "PAYMENT_REQUIRED"),
                                                                                           Map.entry(403, "FORBIDDEN"),
                                                                                           Map.entry(404, "NOT_FOUND"),
                                                                                           Map.entry(405, "METHOD_NOT_ALLOWED"),
                                                                                           Map.entry(406, "NOT_ACCEPTABLE"),
                                                                                           Map.entry(407, "PROXY_AUTHENTICATION_REQUIRED"),
                                                                                           Map.entry(408, "REQUEST_TIMEOUT"),
                                                                                           Map.entry(409, "CONFLICT"),
                                                                                           Map.entry(410, "GONE"),
                                                                                           Map.entry(411, "LENGTH_REQUIRED"),
                                                                                           Map.entry(412, "PRECONDITION_FAILED"),
                                                                                           Map.entry(413, "PAYLOAD_TOO_LARGE"),
                                                                                           Map.entry(414, "URI_TOO_LONG"),
                                                                                           Map.entry(415, "UNSUPPORTED_MEDIA_TYPE"),
                                                                                           Map.entry(416, "REQUESTED_RANGE_NOT_SATISFIABLE"),
                                                                                           Map.entry(417, "EXPECTATION_FAILED"),
                                                                                           Map.entry(418, "I_AM_A_TEAPOT"),
                                                                                           Map.entry(421, "MISDIRECTED_REQUEST"),
                                                                                           Map.entry(422, "UNPROCESSABLE_ENTITY"),
                                                                                           Map.entry(423, "LOCKED"),
                                                                                           Map.entry(424, "FAILED_DEPENDENCY"),
                                                                                           Map.entry(425, "TOO_EARLY"),
                                                                                           Map.entry(426, "UPGRADE_REQUIRED"),
                                                                                           Map.entry(428, "PRECONDITION_REQUIRED"),
                                                                                           Map.entry(429, "TOO_MANY_REQUESTS"),
                                                                                           Map.entry(431, "REQUEST_HEADER_FIELDS_TOO_LARGE"),
                                                                                           Map.entry(451, "UNAVAILABLE_FOR_LEGAL_REASONS"),
                                                                                           Map.entry(500, "INTERNAL_SERVER_ERROR"),
                                                                                           Map.entry(501, "NOT_IMPLEMENTED"),
                                                                                           Map.entry(502, "BAD_GATEWAY"),
                                                                                           Map.entry(503, "SERVICE_UNAVAILABLE"),
                                                                                           Map.entry(504, "GATEWAY_TIMEOUT"),
                                                                                           Map.entry(505, "HTTP_VERSION_NOT_SUPPORTED"),
                                                                                           Map.entry(506, "VARIANT_ALSO_NEGOTIATES"),
                                                                                           Map.entry(507, "INSUFFICIENT_STORAGE"),
                                                                                           Map.entry(508, "LOOP_DETECTED"),
                                                                                           Map.entry(509, "BANDWIDTH_LIMIT_EXCEEDED"),
                                                                                           Map.entry(510, "NOT_EXTENDED"),
                                                                                           Map.entry(511, "NETWORK_AUTHENTICATION_REQUIRED"));

    private HttpStatusNameResolver() {
        /* This utility class should not be instantiated */
    }

    static String resolve(int httpStatus) {
        return STATUS_NAMES.getOrDefault(httpStatus, "UNKNOWN");
    }
}
