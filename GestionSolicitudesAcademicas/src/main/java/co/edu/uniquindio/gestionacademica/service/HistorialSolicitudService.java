package co.edu.uniquindio.gestionacademica.service;

import co.edu.uniquindio.gestionacademica.domain.model.Solicitud;
import co.edu.uniquindio.gestionacademica.domain.model.Usuario;
import co.edu.uniquindio.gestionacademica.dto.response.HistorialSolicitudResponseDTO;

import java.util.List;

public interface HistorialSolicitudService{

    List<HistorialSolicitudResponseDTO> obtenerHistorialPorSolicitud(Long solicitudId);

    void registrarHistorial(Solicitud solicitud, String accionRealizada, Usuario responsable);
}
