package co.edu.uniquindio.gestionacademica.dto.response;

import co.edu.uniquindio.gestionacademica.domain.enums.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudResponseDTO {

    private Long id;
    private String descripcion;
    private LocalDateTime fechaRegistro;
    private String justificacionPrioridad;
    private String observacionCierre;
    private TipoSolicitud tipoSolicitud;
    private EstadoSolicitud estadoSolicitud;
    private Prioridad prioridad;
    private CanalOrigen canalOrigen;
    private Long solicitanteId;
    private Long responsableId;
    private IAClasificacionResponseDTO sugerenciaIA;
}
