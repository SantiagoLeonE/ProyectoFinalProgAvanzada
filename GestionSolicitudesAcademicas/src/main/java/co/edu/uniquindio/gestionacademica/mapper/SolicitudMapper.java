package co.edu.uniquindio.gestionacademica.mapper;

import co.edu.uniquindio.gestionacademica.domain.model.Solicitud;
import co.edu.uniquindio.gestionacademica.dto.response.SolicitudResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class SolicitudMapper {

    public SolicitudResponseDTO toDto(Solicitud solicitud) {

        return SolicitudResponseDTO.builder()
                .id(solicitud.getId())
                .descripcion(solicitud.getDescripcion())
                .fechaRegistro(solicitud.getFechaRegistro())
                .justificacionPrioridad(solicitud.getJustificacion())
                .observacionCierre(solicitud.getObservacionCierre())
                .tipoSolicitud(solicitud.getTipoSolicitud())
                .estadoSolicitud(solicitud.getEstadoSolicitud())
                .prioridad(solicitud.getPrioridad())
                .canalOrigen(solicitud.getCanalOrigen())
                .solicitanteId(solicitud.getSolicitante().getId())
                .responsableId(
                        solicitud.getResponsable() != null
                                ? solicitud.getResponsable().getId()
                                : null
                )
                .build();
    }
}
