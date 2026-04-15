package co.edu.uniquindio.gestionacademica.service.impl;

import co.edu.uniquindio.gestionacademica.domain.enums.*;
import co.edu.uniquindio.gestionacademica.domain.model.Solicitud;
import co.edu.uniquindio.gestionacademica.domain.model.Usuario;
import co.edu.uniquindio.gestionacademica.dto.*;
import co.edu.uniquindio.gestionacademica.dto.request.SolicitudRequestDTO;
import co.edu.uniquindio.gestionacademica.dto.response.SolicitudResponseDTO;
import co.edu.uniquindio.gestionacademica.mapper.SolicitudMapper;
import co.edu.uniquindio.gestionacademica.repository.SolicitudRepository;
import co.edu.uniquindio.gestionacademica.repository.UsuarioRepository;
import co.edu.uniquindio.gestionacademica.service.SolicitudService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SolicitudServiceImpl implements SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final SolicitudMapper solicitudMapper;

    @Override
    @Transactional
    public SolicitudResponseDTO crearSolicitud(SolicitudRequestDTO request) {

        Usuario solicitante = usuarioRepository.findById(request.getSolicitanteId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Solicitud solicitud = Solicitud.builder()
                .descripcion(request.getDescripcion())
                .tipoSolicitud(request.getTipoSolicitud())
                .prioridad(request.getPrioridad())
                .justificacion(request.getJustificacionPrioridad())
                .canalOrigen(request.getCanalOrigen())
                .estadoSolicitud(EstadoSolicitud.REGISTRADA)
                .fechaRegistro(LocalDateTime.now())
                .solicitante(solicitante)
                .build();

        Solicitud solicitudGuardada = solicitudRepository.save(solicitud);
        return solicitudMapper.toDto(solicitudGuardada);
    }

    @Override
    public Page<SolicitudResponseDTO> listarSolicitudes(EstadoSolicitud estadoSolicitud, TipoSolicitud tipoSolicitud, Prioridad prioridad, Long responsableId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Solicitud> solicitudes;

        if(estadoSolicitud != null && tipoSolicitud != null && prioridad != null && responsableId != null) {
            solicitudes = solicitudRepository.findByEstadoSolicitudAndTipoSolicitudAndPrioridadAndResponsableId(estadoSolicitud, tipoSolicitud, prioridad, responsableId, pageable);
        }
        else if (estadoSolicitud != null) {
            solicitudes = solicitudRepository.findByEstadoSolicitud(estadoSolicitud, pageable);
        }
        else if (tipoSolicitud != null) {
            solicitudes = solicitudRepository.findByTipoSolicitud(tipoSolicitud, pageable);
        }
        else if (prioridad != null) {
            solicitudes = solicitudRepository.findByPrioridad(prioridad, pageable);
        }
        else if (responsableId != null) {
            solicitudes = solicitudRepository.findByResponsableId(responsableId, pageable);
        }
        else {
            solicitudes = solicitudRepository.findAll(pageable);
        }

        return solicitudes.map(solicitudMapper::toDto);
    }

    @Override
    public SolicitudResponseDTO obtenerSolicitudPorId(Long id) {

        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        return solicitudMapper.toDto(solicitud);
    }

    @Override
    @Transactional
    public SolicitudResponseDTO clasificarSolicitud(Long id,  ClasificarSolicitudDTO request) {

        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if(solicitud.getEstadoSolicitud() == EstadoSolicitud.CERRADA) {
            throw new RuntimeException("No se puede modificar una solicitud cerrada");
        }

        if(solicitud.getEstadoSolicitud() != EstadoSolicitud.REGISTRADA) {
            throw new RuntimeException("La solicitud no puede pasar al estado CLASIFICADA");
        }

        solicitud.setTipoSolicitud(request.getTipoSolicitud());
        solicitud.setEstadoSolicitud(EstadoSolicitud.CLASIFICADA);

        Solicitud solicitudActualizada = solicitudRepository.save(solicitud);
        return solicitudMapper.toDto(solicitudActualizada);
    }

    @Override
    @Transactional
    public SolicitudResponseDTO asignarResponsable(Long id, AsignarResponsableDTO request) {

        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if(solicitud.getEstadoSolicitud() == EstadoSolicitud.CERRADA) {
            throw new RuntimeException("No se puede modificar una solicitud cerrada");
        }

        Usuario responsable = usuarioRepository.findById(request.getResponsableId())
                .orElseThrow(() -> new RuntimeException("Responsable no encontrado"));

        if(responsable.getRol() != Rol.DOCENTE && responsable.getRol() != Rol.ADMINISTRATIVO) {
            throw new RuntimeException("Solo un DOCENTE o un ADMINISTRATIVO puede ser asignado como responsable");
        }

        if(!responsable.isActivo()) {
            throw new RuntimeException("El responsable no está activo y no puede recibir solicitudes");
        }

        solicitud.setResponsable(responsable);

        Solicitud solicitudActualizada = solicitudRepository.save(solicitud);
        return solicitudMapper.toDto(solicitudActualizada);
    }

    @Override
    @Transactional
    public SolicitudResponseDTO atenderSolicitud(Long id) {

        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if(solicitud.getEstadoSolicitud() == EstadoSolicitud.CERRADA) {
            throw new RuntimeException("No se puede modificar una solicitud cerrada");
        }

        if(solicitud.getEstadoSolicitud() != EstadoSolicitud.CLASIFICADA) {
            throw new RuntimeException("La solicitud no puede pasar al estado EN_ATENCIÓN");
        }

        if(solicitud.getResponsable() == null) {
            throw new RuntimeException("No se puede atender una solicitud sin un responsable asignado");
        }

        solicitud.setEstadoSolicitud(EstadoSolicitud.EN_ATENCION);

        Solicitud solicitudActualizada = solicitudRepository.save(solicitud);
        return solicitudMapper.toDto(solicitudActualizada);
    }

    @Override
    public SolicitudResponseDTO resolverSolicitud(Long id) {

        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if(solicitud.getEstadoSolicitud() == EstadoSolicitud.CERRADA) {
            throw new RuntimeException("No se puede modificar una solicitud cerrada");
        }

        if(solicitud.getEstadoSolicitud() != EstadoSolicitud.EN_ATENCION) {
            throw new RuntimeException("La solicitud no puede pasar al estado ATENDIDA");
        }

        solicitud.setEstadoSolicitud(EstadoSolicitud.ATENDIDA);

        Solicitud solicitudActualizada = solicitudRepository.save(solicitud);
        return solicitudMapper.toDto(solicitudActualizada);
    }

    @Override
    public SolicitudResponseDTO cerrarSolicitud(Long id, CerrarSolicitudDTO request) {

        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if(solicitud.getEstadoSolicitud() == EstadoSolicitud.CERRADA) {
            throw new RuntimeException("No se puede modificar una solicitud cerrada");
        }

        if(solicitud.getEstadoSolicitud() != EstadoSolicitud.ATENDIDA) {
            throw new RuntimeException("La solicitud no puede pasar al estado CERRADA");
        }

        solicitud.setEstadoSolicitud(EstadoSolicitud.CERRADA);
        solicitud.setObservacionCierre(request.getObservacionCierre());

        Solicitud solicitudActualizada = solicitudRepository.save(solicitud);
        return solicitudMapper.toDto(solicitudActualizada);
    }

}
