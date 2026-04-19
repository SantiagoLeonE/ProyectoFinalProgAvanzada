package co.edu.uniquindio.gestionacademica.service.impl;

import co.edu.uniquindio.gestionacademica.domain.enums.*;
import co.edu.uniquindio.gestionacademica.domain.model.Solicitud;
import co.edu.uniquindio.gestionacademica.domain.model.Usuario;
import co.edu.uniquindio.gestionacademica.dto.*;
import co.edu.uniquindio.gestionacademica.dto.request.SolicitudRequestDTO;
import co.edu.uniquindio.gestionacademica.dto.response.SolicitudResponseDTO;
import co.edu.uniquindio.gestionacademica.exception.DatosInvalidosException;
import co.edu.uniquindio.gestionacademica.exception.EstadoInvalidoException;
import co.edu.uniquindio.gestionacademica.exception.RecursoNoEncontradoException;
import co.edu.uniquindio.gestionacademica.mapper.SolicitudMapper;
import co.edu.uniquindio.gestionacademica.repository.SolicitudRepository;
import co.edu.uniquindio.gestionacademica.repository.UsuarioRepository;
import co.edu.uniquindio.gestionacademica.service.HistorialSolicitudService;
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
    private final HistorialSolicitudService historialSolicitudService;

    @Override
    @Transactional
    public SolicitudResponseDTO crearSolicitud(SolicitudRequestDTO request) {

        Usuario solicitante = usuarioRepository.findById(request.getSolicitanteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario con id " + request.getSolicitanteId() + " no encontrado"));

        if(solicitante.getRol() != Rol.ESTUDIANTE) {
            throw new DatosInvalidosException("Solo un ESTUDIANTE puede realizar solicitudes");
        }

        if(!solicitante.isActivo()) {
            throw new DatosInvalidosException("El solicitante con id " + request.getSolicitanteId() + " no está activo y no puede realizar solicitudes");
        }

        Solicitud solicitud = Solicitud.builder()
                .descripcion(request.getDescripcion())
                .tipoSolicitud(request.getTipoSolicitud())
                .prioridad(request.getPrioridad())
                .justificacionPrioridad(request.getJustificacionPrioridad())
                .canalOrigen(request.getCanalOrigen())
                .estadoSolicitud(EstadoSolicitud.REGISTRADA)
                .fechaRegistro(LocalDateTime.now())
                .solicitante(solicitante)
                .build();

        Solicitud solicitudGuardada = solicitudRepository.save(solicitud);
        historialSolicitudService.registrarHistorial(solicitudGuardada, "Solicitud Registrada", null);
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
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud con id " + id + " no encontrada"));

        return solicitudMapper.toDto(solicitud);
    }

    @Override
    @Transactional
    public SolicitudResponseDTO clasificarSolicitud(Long id,  ClasificarSolicitudDTO request) {

        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud con id " + id + " no encontrada"));

        if(solicitud.getEstadoSolicitud() == EstadoSolicitud.CERRADA) {
            throw new EstadoInvalidoException("No se puede modificar la solicitud con id " + id + " porque está cerrada");
        }

        if(solicitud.getEstadoSolicitud() != EstadoSolicitud.REGISTRADA) {
            throw new EstadoInvalidoException("La solicitud con id " + id + " no puede pasar al estado CLASIFICADA");
        }

        solicitud.setTipoSolicitud(request.getTipoSolicitud());
        solicitud.setEstadoSolicitud(EstadoSolicitud.CLASIFICADA);

        Solicitud solicitudActualizada = solicitudRepository.save(solicitud);
        historialSolicitudService.registrarHistorial(solicitudActualizada, "Solicitud Clasificada", solicitudActualizada.getResponsable());
        return solicitudMapper.toDto(solicitudActualizada);
    }

    @Override
    @Transactional
    public SolicitudResponseDTO asignarResponsable(Long id, AsignarResponsableDTO request) {

        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud con id " + id + " no encontrada"));

        if(solicitud.getEstadoSolicitud() == EstadoSolicitud.CERRADA) {
            throw new EstadoInvalidoException("No se puede modificar la solicitud con id " + id + " porque está cerrada");
        }

        Usuario responsable = usuarioRepository.findById(request.getResponsableId())
                .orElseThrow(() -> new DatosInvalidosException("Responsable con id " + request.getResponsableId() + " no encontrado"));

        if(responsable.getRol() != Rol.DOCENTE && responsable.getRol() != Rol.ADMINISTRATIVO) {
            throw new DatosInvalidosException("Solo un DOCENTE o un ADMINISTRATIVO puede ser asignado como responsable");
        }

        if(!responsable.isActivo()) {
            throw new DatosInvalidosException("El responsable con id " + responsable.getId() + " no está activo y no puede recibir solicitudes");
        }

        solicitud.setResponsable(responsable);

        Solicitud solicitudActualizada = solicitudRepository.save(solicitud);
        historialSolicitudService.registrarHistorial(solicitudActualizada, "Responsable Asignado", responsable);
        return solicitudMapper.toDto(solicitudActualizada);
    }

    @Override
    @Transactional
    public SolicitudResponseDTO atenderSolicitud(Long id) {

        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud con id " + id + " no encontrada"));

        if(solicitud.getEstadoSolicitud() == EstadoSolicitud.CERRADA) {
            throw new EstadoInvalidoException("No se puede modificar la solicitud con id " + id + " porque está cerrada");
        }

        if(solicitud.getEstadoSolicitud() != EstadoSolicitud.CLASIFICADA) {
            throw new EstadoInvalidoException("La solicitud con id " + id + " no puede pasar al estado EN_ATENCIÓN");
        }

        if(solicitud.getResponsable() == null) {
            throw new DatosInvalidosException("No se puede atender una solicitud sin un responsable asignado");
        }

        solicitud.setEstadoSolicitud(EstadoSolicitud.EN_ATENCION);

        Solicitud solicitudActualizada = solicitudRepository.save(solicitud);
        historialSolicitudService.registrarHistorial(solicitudActualizada, "Solicitud en Atención", solicitudActualizada.getResponsable());
        return solicitudMapper.toDto(solicitudActualizada);
    }

    @Override
    public SolicitudResponseDTO resolverSolicitud(Long id) {

        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud con id " + id + " no encontrada"));

        if(solicitud.getEstadoSolicitud() == EstadoSolicitud.CERRADA) {
            throw new EstadoInvalidoException("No se puede modificar la solicitud con id " + id + " porque está cerrada");
        }

        if(solicitud.getEstadoSolicitud() != EstadoSolicitud.EN_ATENCION) {
            throw new EstadoInvalidoException("La solicitud con id " + id + " no puede pasar al estado ATENDIDA");
        }

        solicitud.setEstadoSolicitud(EstadoSolicitud.ATENDIDA);

        Solicitud solicitudActualizada = solicitudRepository.save(solicitud);
        historialSolicitudService.registrarHistorial(solicitudActualizada, "Solicitud Atendida", solicitudActualizada.getResponsable());
        return solicitudMapper.toDto(solicitudActualizada);
    }

    @Override
    public SolicitudResponseDTO cerrarSolicitud(Long id, CerrarSolicitudDTO request) {

        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud con id " + id + " no encontrada"));

        if(solicitud.getEstadoSolicitud() == EstadoSolicitud.CERRADA) {
            throw new EstadoInvalidoException("No se puede modificar la solicitud con id " + id + " porque está cerrada");
        }

        if(solicitud.getEstadoSolicitud() != EstadoSolicitud.ATENDIDA) {
            throw new EstadoInvalidoException("La solicitud con id " + id + " no puede pasar al estado CERRADA");
        }

        solicitud.setEstadoSolicitud(EstadoSolicitud.CERRADA);
        solicitud.setObservacionCierre(request.getObservacionCierre());

        Solicitud solicitudActualizada = solicitudRepository.save(solicitud);
        historialSolicitudService.registrarHistorial(solicitudActualizada, "Solicitud Cerrada", solicitudActualizada.getResponsable());
        return solicitudMapper.toDto(solicitudActualizada);
    }

}
