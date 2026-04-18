package co.edu.uniquindio.gestionacademica.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponseDTO {

    private String codigo;
    private String mensaje;
    private LocalDateTime timestamp;
}
