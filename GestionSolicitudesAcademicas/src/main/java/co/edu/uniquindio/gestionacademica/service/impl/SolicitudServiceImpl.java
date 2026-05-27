package co.edu.uniquindio.gestionacademica.service.impl;

import co.edu.uniquindio.gestionacademica.domain.enums.*;
import co.edu.uniquindio.gestionacademica.domain.model.Solicitud;
import co.edu.uniquindio.gestionacademica.domain.model.Usuario;
import co.edu.uniquindio.gestionacademica.dto.*;
import co.edu.uniquindio.gestionacademica.dto.request.SolicitudRequestDTO;

import co.edu.uniquindio.gestionacademica.dto.response.IAClasificacionResponseDTO;
import co.edu.uniquindio.gestionacademica.dto.response.SolicitudResponseDTO;
import co.edu.uniquindio.gestionacademica.exception.*;
import co.edu.uniquindio.gestionacademica.mapper.SolicitudMapper;
import co.edu.uniquindio.gestionacademica.repository.SolicitudRepository;
import co.edu.uniquindio.gestionacademica.repository.UsuarioRepository;
import co.edu.uniquindio.gestionacademica.service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitudServiceImpl implements SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final SolicitudMapper solicitudMapper;
    private final HistorialSolicitudService historialSolicitudService;
    private final IAService iaService;


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
                .prioridad(null)
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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuarioActual = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        Page<Solicitud> solicitudesBase;

        //Filtrar por rol
        switch (usuarioActual.getRol()) {

            case ESTUDIANTE ->
                    solicitudesBase =
                            solicitudRepository.findBySolicitanteId(
                                    usuarioActual.getId(),
                                    Pageable.unpaged());

            case DOCENTE ->
                    solicitudesBase =
                            solicitudRepository.findByResponsableId(
                                    usuarioActual.getId(),
                                    Pageable.unpaged());

            case ADMINISTRATIVO ->
                    solicitudesBase =
                            solicitudRepository.findAll(
                                    Pageable.unpaged());

            default ->
                    throw new DatosInvalidosException(
                            "Rol no autorizado");
        }

        //Filtrar en memoria
        List<Solicitud> solicitudesFiltradas =
                solicitudesBase.getContent()
                        .stream()
                        .filter(s ->
                                estadoSolicitud == null
                                        || s.getEstadoSolicitud() == estadoSolicitud)
                        .filter(s ->
                                prioridad == null
                                        || s.getPrioridad() == prioridad)
                        .filter(s ->
                                tipoSolicitud == null
                                        || s.getTipoSolicitud() == tipoSolicitud)
                        .toList();

        //Paginación
        int inicio = (int) pageable.getOffset();

        int fin = Math.min(
                inicio + pageable.getPageSize(),
                solicitudesFiltradas.size());

        List<Solicitud> contenidoPagina =
                solicitudesFiltradas.subList(
                        inicio,
                        fin);

        Page<Solicitud> solicitudesPaginadas =
                new PageImpl<>(
                        contenidoPagina,
                        pageable,
                        solicitudesFiltradas.size());

        return solicitudesPaginadas.map(
                solicitudMapper::toDto);
    }

    @Override
    public SolicitudResponseDTO obtenerSolicitudPorId(Long id) {

        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud con id " + id + " no encontrada"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuarioActual = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        //Un DOCENTE solo puede ver solicitudes que le están asignadas
        if (usuarioActual.getRol() == Rol.DOCENTE) {
            if (solicitud.getResponsable() == null ||
                    !solicitud.getResponsable().getId().equals(usuarioActual.getId())) {
                throw new DatosInvalidosException("No tienes permiso para ver esta solicitud");
            }
        }

        //Un ESTUDIANTE solo puede ver sus propias solicitudes
        if (usuarioActual.getRol() == Rol.ESTUDIANTE) {
            if (!solicitud.getSolicitante().getId().equals(usuarioActual.getId())) {
                throw new DatosInvalidosException("No tienes permiso para ver esta solicitud");
            }
        }

        SolicitudResponseDTO response = solicitudMapper.toDto(solicitud);

        //Validación IA
        if (usuarioActual.getRol() == Rol.ADMINISTRATIVO) {
            IAClasificacionResponseDTO sugerencia = iaService.sugerirClasificacion(solicitud.getDescripcion());
            response.setSugerenciaIA(sugerencia);
        }

        return response;
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

        if(solicitud.getTipoSolicitud() != request.getTipoSolicitud()) {
            throw new DatosInvalidosException("El tipo de solicitud enviado " + request.getTipoSolicitud() + " debe ser igual al tipo de la solicitud que se va a clasificar " + solicitud.getTipoSolicitud());
        }

        if(request.getFechaLimite() != null) {

            LocalDate fechaSolicitud =
                    solicitud.getFechaRegistro()
                            .toLocalDate();

            if(request.getFechaLimite()
                    .isBefore(fechaSolicitud)) {

                throw new DatosInvalidosException("La fecha límite no puede ser anterior a la fecha de la solicitud");
            }

            if(request.getFechaLimite()
                    .isEqual(fechaSolicitud)) {

                throw new DatosInvalidosException("La fecha límite debe ser posterior a la fecha de la solicitud");
            }
        }

        //Si el ADMINISTRATIVO envió una prioridad manual se usa esa, si no se calcula automáticamente con la lógica de negocio
        Prioridad prioridad;
        if (request.getPrioridadManual() != null) {
            prioridad = request.getPrioridadManual();
            solicitud.setJustificacionPrioridad("Prioridad asignada manualmente por el administrador: " + prioridad);
        } else {
            prioridad = determinarPrioridad(
                    request.getTipoSolicitud(),
                    request.getImpactoAcademico(),
                    request.getFechaLimite());
            solicitud.setJustificacionPrioridad(
                    "Prioridad calculada automáticamente según tipo: "
                            + request.getTipoSolicitud()
                            + ", impacto: " + request.getImpactoAcademico()
                            + ", fecha límite: " + request.getFechaLimite());
        }

        solicitud.setPrioridad(prioridad);
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

        if(solicitud.getResponsable() != null) {
            throw new DatosInvalidosException("La solicitud ya tiene un responsable asignado");
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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuarioActual = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        if(usuarioActual.getRol() != Rol.DOCENTE) {
            throw new DatosInvalidosException("Solo un DOCENTE puede atender solicitudes");
        }

        if(!solicitud.getResponsable().getId().equals(usuarioActual.getId())) {
            throw new DatosInvalidosException("No eres el responsable asignado");
        }

        solicitud.setEstadoSolicitud(EstadoSolicitud.EN_ATENCION);

        Solicitud solicitudActualizada = solicitudRepository.save(solicitud);
        historialSolicitudService.registrarHistorial(solicitudActualizada, "Solicitud en Atención", solicitudActualizada.getResponsable());

        return solicitudMapper.toDto(solicitudActualizada);
    }

    @Override
    @Transactional
    public SolicitudResponseDTO resolverSolicitud(Long id) {

        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud con id " + id + " no encontrada"));

        if(solicitud.getEstadoSolicitud() == EstadoSolicitud.CERRADA) {
            throw new EstadoInvalidoException("No se puede modificar la solicitud con id " + id + " porque está cerrada");
        }

        if(solicitud.getEstadoSolicitud() != EstadoSolicitud.EN_ATENCION) {
            throw new EstadoInvalidoException("La solicitud con id " + id + " no puede pasar al estado ATENDIDA");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuarioActual = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        if(usuarioActual.getRol() != Rol.DOCENTE) {
            throw new DatosInvalidosException("Solo un DOCENTE puede atender solicitudes");
        }

        if(!solicitud.getResponsable()
                .getId()
                .equals(usuarioActual.getId())) {
            throw new DatosInvalidosException("No eres el responsable asignado");
        }

        solicitud.setEstadoSolicitud(EstadoSolicitud.ATENDIDA);

        Solicitud solicitudActualizada = solicitudRepository.save(solicitud);
        historialSolicitudService.registrarHistorial(solicitudActualizada, "Solicitud Atendida", solicitudActualizada.getResponsable());

        return solicitudMapper.toDto(solicitudActualizada);
    }

    @Override
    @Transactional
    public SolicitudResponseDTO cerrarSolicitud(Long id, CerrarSolicitudDTO request) {

        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud con id " + id + " no encontrada"));

        if(solicitud.getEstadoSolicitud() == EstadoSolicitud.CERRADA) {
            throw new EstadoInvalidoException("No se puede modificar la solicitud con id " + id + " porque está cerrada");
        }

        if(solicitud.getEstadoSolicitud() != EstadoSolicitud.ATENDIDA) {
            throw new EstadoInvalidoException("La solicitud con id " + id + " no puede pasar al estado CERRADA");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuarioActual = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        if(usuarioActual.getRol() != Rol.DOCENTE) {
            throw new DatosInvalidosException("Solo un DOCENTE puede atender solicitudes");
        }

        if(!solicitud.getResponsable()
                .getId()
                .equals(usuarioActual.getId())) {
            throw new DatosInvalidosException("No eres el responsable asignado");
        }

        solicitud.setEstadoSolicitud(EstadoSolicitud.CERRADA);
        solicitud.setObservacionCierre(request.getObservacionCierre());

        Solicitud solicitudActualizada = solicitudRepository.save(solicitud);
        historialSolicitudService.registrarHistorial(solicitudActualizada, "Solicitud Cerrada", solicitudActualizada.getResponsable());

        return solicitudMapper.toDto(solicitudActualizada);
    }

    private Prioridad determinarPrioridad(TipoSolicitud tipoSolicitud, String impactoAcademico, LocalDate fechaLimite) {

        //Puntos según el tipo de la solicitud
        int puntos = 0;

        switch (tipoSolicitud) {
            case REGISTRO_ASIGNATURA -> puntos += 3;
            case SOLICITUD_CUPO -> puntos += 3;
            case CANCELACION -> puntos += 2;
            case HOMOLOGACION -> puntos += 2;
            case CONSULTA_ACADEMICA -> puntos += 1;
        }

        //Impacto académico declarado por el solicitante
        if (impactoAcademico != null) {
            String impacto = impactoAcademico.toLowerCase();
            if (impacto.contains("grado") || impacto.contains("graduacion")
                    || impacto.contains("cancelacion") || impacto.contains("perder")) {
                puntos += 3;
            } else if (impacto.contains("semestre") || impacto.contains("creditos")) {
                puntos += 2;
            } else {
                puntos += 1;
            }
        }

        //Urgencia por fecha límite
        if (fechaLimite != null) {
            long diasRestantes = LocalDate.now().until(fechaLimite).getDays();
            if (diasRestantes <= 3) {
                puntos += 3;
            } else if (diasRestantes <= 7) {
                puntos += 2;
            } else if (diasRestantes <= 15) {
                puntos += 1;
            }
        }

        //Calcular la prioridad final según el total de puntos
        if (puntos >= 6) return Prioridad.ALTA;
        if (puntos >= 3) return Prioridad.MEDIA;
        return Prioridad.BAJA;
    }
}
