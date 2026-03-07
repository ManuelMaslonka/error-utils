/**
 * MVC advice classes responsible for mapping exceptions to standardized responses.
 *
 * <p>The default HTTP flow typically ends here after a service throws a
 * {@link com.maslonka.reservation.errorutils.core.exception.BusinessException} or an
 * infrastructure/framework exception bubbles up from Spring MVC.</p>
 */
package com.maslonka.reservation.errorutils.spring.web.advice;
