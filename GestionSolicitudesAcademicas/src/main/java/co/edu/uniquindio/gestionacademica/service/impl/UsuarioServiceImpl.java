package co.edu.uniquindio.gestionacademica.service.impl;

import co.edu.uniquindio.gestionacademica.domain.enums.EstadoSolicitud;
import co.edu.uniquindio.gestionacademica.domain.enums.Rol;
import co.edu.uniquindio.gestionacademica.domain.model.Usuario;
import co.edu.uniquindio.gestionacademica.dto.request.UsuarioRequestDTO;
import co.edu.uniquindio.gestionacademica.dto.response.UsuarioResponseDTO;
import co.edu.uniquindio.gestionacademica.exception.DatosInvalidosException;
import co.edu.uniquindio.gestionacademica.exception.RecursoNoEncontradoException;
import co.edu.uniquindio.gestionacademica.mapper.UsuarioMapper;
import co.edu.uniquindio.gestionacademica.repository.SolicitudRepository;
import co.edu.uniquindio.gestionacademica.repository.UsuarioRepository;
import co.edu.uniquindio.gestionacademica.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final SolicitudRepository solicitudRepository;

    @Override
    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO request) {

        Usuario usuario = usuarioMapper.toEntity(request);
        usuario.setContrasena(passwordEncoder.encode(request.getContrasena()));

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return usuarioMapper.toDTO(usuarioGuardado);
    }

    @Override
    public List<UsuarioResponseDTO> listarUsuarios(Rol rol, Boolean activo) {

        List<Usuario> usuarios;

        if(rol != null && activo != null) {
            usuarios = usuarioRepository.findByRolAndActivo(rol, activo);
        }
        else if (rol != null) {
            usuarios = usuarioRepository.findByRol(rol);
        }
        else if (activo != null) {
            usuarios = usuarioRepository.findByActivo(activo);
        }
        else {
            usuarios = usuarioRepository.findAll();
        }

        return usuarios.stream()
                .map(usuarioMapper::toDTO)
                .toList();
    }

    @Override
    public UsuarioResponseDTO obtenerUsuarioPorId(Long id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario con id " + id + " no encontrado"));

        return usuarioMapper.toDTO(usuario);
    }

    @Override
    public UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO request) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario con id " + id + " no encontrado"));

        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setIdentificacion(request.getIdentificacion());
        usuario.setRol(request.getRol());
        usuario.setContrasena(passwordEncoder.encode(request.getContrasena()));

        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return usuarioMapper.toDTO(usuarioActualizado);
    }

    @Override
    public UsuarioResponseDTO activarUsuario(Long id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario con id " + id + " no encontrado"));

        if(usuario.isActivo()) {
            throw new DatosInvalidosException("El usuario con id " + id + " ya se encuentra activado");
        }

        usuario.setActivo(true);
        Usuario usuarioActualizado =  usuarioRepository.save(usuario);
        return usuarioMapper.toDTO(usuarioActualizado);
    }

    @Override
    public UsuarioResponseDTO desactivarUsuario(Long id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario con id " + id + " no encontrado"));

        if(!usuario.isActivo()) {
            throw new DatosInvalidosException("El usuario con id " + id + " ya se encuentra desactivado");
        }

        //No se puede desactivar un ADMINISTRATIVO
        if (usuario.getRol() == Rol.ADMINISTRATIVO) {
            throw new DatosInvalidosException("No se puede desactivar un usuario con rol ADMINISTRATIVO");
        }

        //Validación para DOCENTE
        if (usuario.getRol() == Rol.DOCENTE) {
            boolean tieneSolicitudesActivas = solicitudRepository
                    .existsByResponsableIdAndEstadoSolicitudIn(
                            id,
                            List.of(
                                    EstadoSolicitud.CLASIFICADA,
                                    EstadoSolicitud.EN_ATENCION,
                                    EstadoSolicitud.ATENDIDA));

            if (tieneSolicitudesActivas) {
                throw new DatosInvalidosException("No se puede desactivar el DOCENTE con id " + id + " porque tiene solicitudes activas asignadas");
            }
        }

        //Validación para ESTUDIANTE
        if (usuario.getRol() == Rol.ESTUDIANTE) {
            boolean tieneSolicitudesActivas = solicitudRepository
                    .existsBySolicitanteIdAndEstadoSolicitudIn(
                            id,
                            List.of(
                                    EstadoSolicitud.REGISTRADA,
                                    EstadoSolicitud.CLASIFICADA,
                                    EstadoSolicitud.EN_ATENCION,
                                    EstadoSolicitud.ATENDIDA));

            if (tieneSolicitudesActivas) {
                throw new DatosInvalidosException("No se puede desactivar el ESTUDIANTE con id " + id + " porque tiene solicitudes activas en el sistema");
            }
        }

        usuario.setActivo(false);
        Usuario usuarioActualizado =  usuarioRepository.save(usuario);
        return usuarioMapper.toDTO(usuarioActualizado);
    }
}
