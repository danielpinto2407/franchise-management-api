package org.franchise.management.entrypoints.webflux.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Log4j2
public class ResponseUtil {

    private ResponseUtil() {
        // Clase utilitaria, no se instancia
    }

    /** Respuesta 200 OK con cuerpo JSON */
    public static Mono<ServerResponse> ok(Object body) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body);
    }

    /** Respuesta 400 Bad Request con mensaje JSON */
    public static Mono<ServerResponse> badRequest(String message) {
        return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("error", message));
    }

    /** Manejo de cuerpo vacío */
    public static Mono<ServerResponse> emptyBody() {
        return badRequest("El cuerpo de la solicitud está vacío");
    }

    /** Manejo de errores genérico */
    public static Mono<ServerResponse> handleError(String action, Throwable e) {
        log.error("Error al {}: {}", action, e.getMessage(), e);
        return badRequest(e.getMessage());
    }
}
