package co.edu.uniquindio.gestionacademica.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumenIAResponseDTO {

    private Long solicitudId;
    private String resumen;
    private boolean iaDisponible;
}
