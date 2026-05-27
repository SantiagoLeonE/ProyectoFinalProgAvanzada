package co.edu.uniquindio.gestionacademica.service;

import co.edu.uniquindio.gestionacademica.domain.enums.*;
import co.edu.uniquindio.gestionacademica.dto.AsignarResponsableDTO;
import co.edu.uniquindio.gestionacademica.dto.CerrarSolicitudDTO;
import co.edu.uniquindio.gestionacademica.dto.ClasificarSolicitudDTO;
import co.edu.uniquindio.gestionacademica.dto.request.SolicitudRequestDTO;
import co.edu.uniquindio.gestionacademica.dto.response.SolicitudResponseDTO;
import org.springframework.data.domain.Page;

public interface SolicitudService {

    Page<SolicitudResponseDTO> listarSolicitudes(
            EstadoSolicitud estadoSolicitud,
            TipoSolicitud tipoSolicitud,
            Prioridad prioridad,
            Long responsableId,
            int page, int size);

    SolicitudResponseDTO crearSolicitud(SolicitudRequestDTO request);
    SolicitudResponseDTO obtenerSolicitudPorId(Long id);
    SolicitudResponseDTO clasificarSolicitud(Long id, ClasificarSolicitudDTO request);
    SolicitudResponseDTO asignarResponsable(Long id, AsignarResponsableDTO request);
    SolicitudResponseDTO atenderSolicitud(Long id);
    SolicitudResponseDTO resolverSolicitud(Long id);
    SolicitudResponseDTO cerrarSolicitud(Long id, CerrarSolicitudDTO request);
}
