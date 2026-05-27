package co.edu.uniquindio.gestionacademica.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IAClasificacionResponseDTO {

    private String tipoSolicitudSugerido;
    private String prioridadSugerida;
    private String justificacion;
    private boolean iaDisponible;
}
