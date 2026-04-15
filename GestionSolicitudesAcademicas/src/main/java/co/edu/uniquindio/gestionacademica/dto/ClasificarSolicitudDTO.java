package co.edu.uniquindio.gestionacademica.dto;

import co.edu.uniquindio.gestionacademica.domain.enums.TipoSolicitud;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClasificarSolicitudDTO {

    private TipoSolicitud tipoSolicitud;
}
