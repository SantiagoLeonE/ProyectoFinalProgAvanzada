package co.edu.uniquindio.gestionacademica.dto;

import co.edu.uniquindio.gestionacademica.domain.enums.Prioridad;
import co.edu.uniquindio.gestionacademica.domain.enums.TipoSolicitud;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClasificarSolicitudDTO {

    private TipoSolicitud tipoSolicitud;
    private String impactoAcademico;
    private LocalDate fechaLimite;
    private Prioridad prioridadManual;
}
