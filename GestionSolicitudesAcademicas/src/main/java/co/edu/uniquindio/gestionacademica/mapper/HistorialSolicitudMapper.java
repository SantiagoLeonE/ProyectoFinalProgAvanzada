package co.edu.uniquindio.gestionacademica.mapper;

import co.edu.uniquindio.gestionacademica.domain.model.HistorialSolicitud;
import co.edu.uniquindio.gestionacademica.dto.response.HistorialSolicitudResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class HistorialSolicitudMapper {

    public HistorialSolicitudResponseDTO toDTO(HistorialSolicitud historialSolicitud) {

        return HistorialSolicitudResponseDTO.builder()
                .id(historialSolicitud.getId())
                .fechaAccion(historialSolicitud.getFechaAccion())
                .accionRealizada(historialSolicitud.getAccionRealizada())
                .solicitudId(historialSolicitud.getSolicitud().getId())
                .responsableId(
                        historialSolicitud.getResponsable() != null
                                ? historialSolicitud.getResponsable().getId()
                                : null
                )
                .build();
    }
}
