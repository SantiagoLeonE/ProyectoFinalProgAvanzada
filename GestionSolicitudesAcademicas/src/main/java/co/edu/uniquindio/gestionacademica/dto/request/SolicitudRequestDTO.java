package co.edu.uniquindio.gestionacademica.dto.request;

import co.edu.uniquindio.gestionacademica.domain.enums.CanalOrigen;
import co.edu.uniquindio.gestionacademica.domain.enums.TipoSolicitud;
import jakarta.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudRequestDTO {

    //No puede estar vacío ni ser null porque es el texto principal de la solicitud
    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 1000, message = "La descripción debe tener entre 10 y 1000 caracteres")
    private String descripcion;

    //El tipo es obligatorio para saber qué clase de solicitud se desea realizar
    @NotNull(message = "El tipo de la solicitud es obligatorio")
    private TipoSolicitud tipoSolicitud;

    //La justificación no es obligatoria porque la decide la IA o un rol ADMINISTRATIVO
    @Size(max = 500)
    private String justificacionPrioridad;

    //El canal es obligatorio para saber por dónde se realiza la solicitud
    @NotNull(message = "El canal de origen es obligatorio")
    private CanalOrigen canalOrigen;

    //Id del solicitante es obligatorio para saber quién realiza la solicitud
    @NotNull(message = "El id del solicitante es obligatorio")
    private Long solicitanteId;
}
