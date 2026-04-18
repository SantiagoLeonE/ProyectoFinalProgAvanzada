package co.edu.uniquindio.gestionacademica.exception;

import co.edu.uniquindio.gestionacademica.dto.response.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //Está excepción captura cuando no se encuentra una solicitud, usuario o historial. Retorna HTTP 404 NOT FOUND
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> handleRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponseDTO.builder()
                        .codigo("Recurso no encontrado")
                        .mensaje(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    //Está excepción captura cuando se intenta realizar una transición de estados inválida. Retorna HTTP 409 CONFLICT
    @ExceptionHandler(EstadoInvalidoException.class)
    public ResponseEntity<ErrorResponseDTO> handleEstadoInvalido(EstadoInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponseDTO.builder()
                        .codigo("Estado inválido")
                        .mensaje(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    //Está excepción captura cuando se incumple con una regla de negocio. Retorna HTTP 400 BAD REQUEST
    @ExceptionHandler(DatosInvalidosException.class)
    public ResponseEntity<ErrorResponseDTO> handleDatosInvalidos(DatosInvalidosException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDTO.builder()
                        .codigo("Datos inválidos")
                        .mensaje(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    //Está excepción captura cualquier otro error que no se controla. Retorna HTTP 500 INTERNAL SERVER ERROR
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleExcepcionGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponseDTO.builder()
                        .codigo("Error interno")
                        .mensaje("Ocurrió un error inesperado en el servidor")
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}
