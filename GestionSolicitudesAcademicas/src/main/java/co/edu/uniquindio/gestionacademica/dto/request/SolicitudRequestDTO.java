package co.edu.uniquindio.gestionacademica.dto.request;

import co.edu.uniquindio.gestionacademica.domain.enums.CanalOrigen;
import co.edu.uniquindio.gestionacademica.domain.enums.Prioridad;
import co.edu.uniquindio.gestionacademica.domain.enums.TipoSolicitud;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudRequestDTO {

    private String descripcion;
    private TipoSolicitud tipoSolicitud;
    private String justificacionPrioridad;
    private Prioridad prioridad;
    private CanalOrigen canalOrigen;
    private Long solicitanteId;

}
