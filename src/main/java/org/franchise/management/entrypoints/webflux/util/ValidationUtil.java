package org.franchise.management.entrypoints.webflux.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utilidad para validación de DTOs usando Jakarta Validation
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ValidationUtil {

    private final Validator validator;

    /**
     * Valida un DTO usando Jakarta Validation
     * 
     * @param dto Objeto a validar
     * @return Mono con el objeto validado o error si hay violaciones
     */
    public <T> Mono<T> validate(T dto) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);

        if (violations.isEmpty()) {
            return Mono.just(dto);
        }

        String errorMessage = buildErrorMessage(violations);
        log.warn("Validation failed: {}", errorMessage);

        return Mono.error(new IllegalArgumentException(errorMessage));
    }

    /**
     * Construye un mensaje de error legible a partir de las violaciones
     */
    private <T> String buildErrorMessage(Set<ConstraintViolation<T>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
    }

    /**
     * Valida múltiples campos y retorna un mensaje detallado con el nombre del
     * campo
     */
    public <T> Mono<T> validateWithFieldNames(T dto) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);

        if (violations.isEmpty()) {
            return Mono.just(dto);
        }

        String errorMessage = buildDetailedErrorMessage(violations);
        log.warn("Validation failed: {}", errorMessage);

        return Mono.error(new IllegalArgumentException(errorMessage));
    }

    /**
     * Construye un mensaje de error detallado con nombres de campos
     */
    private <T> String buildDetailedErrorMessage(Set<ConstraintViolation<T>> violations) {
        return violations.stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
    }
}