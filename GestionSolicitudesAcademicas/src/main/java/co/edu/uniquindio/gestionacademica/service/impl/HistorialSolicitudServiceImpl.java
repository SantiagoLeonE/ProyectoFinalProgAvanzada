package co.edu.uniquindio.gestionacademica.service.impl;

import co.edu.uniquindio.gestionacademica.domain.model.HistorialSolicitud;
import co.edu.uniquindio.gestionacademica.domain.model.Solicitud;
import co.edu.uniquindio.gestionacademica.domain.model.Usuario;
import co.edu.uniquindio.gestionacademica.dto.response.HistorialSolicitudResponseDTO;
import co.edu.uniquindio.gestionacademica.exception.RecursoNoEncontradoException;
import co.edu.uniquindio.gestionacademica.mapper.HistorialSolicitudMapper;
import co.edu.uniquindio.gestionacademica.repository.HistorialSolicitudRepository;
import co.edu.uniquindio.gestionacademica.repository.SolicitudRepository;
import co.edu.uniquindio.gestionacademica.service.HistorialSolicitudService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistorialSolicitudServiceImpl implements HistorialSolicitudService {

    private final HistorialSolicitudRepository historialSolicitudRepository;
    private final HistorialSolicitudMapper historialSolicitudMapper;
    private final SolicitudRepository solicitudRepository;

    @Override
    public List<HistorialSolicitudResponseDTO> obtenerHistorialPorSolicitud(Long solicitudId) {
        solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud con id " + solicitudId + " no encontrada"));

        return historialSolicitudRepository.findBySolicitudIdOrderByFechaAccionAsc(solicitudId)
                .stream()
                .map(historialSolicitudMapper::toDTO)
                .toList();
    }


    /*
     *Método para integrar historial con solicitud
     */
    @Override
    public void registrarHistorial(Solicitud solicitud, String accionRealizada, Usuario responsable) {

        HistorialSolicitud historialSolicitud = HistorialSolicitud.builder()
                .accionRealizada(accionRealizada)
                .fechaAccion(LocalDateTime.now())
                .solicitud(solicitud)
                .responsable(responsable)
                .build();

        historialSolicitudRepository.save(historialSolicitud);
    }
}
