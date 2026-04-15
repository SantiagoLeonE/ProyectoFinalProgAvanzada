package co.edu.uniquindio.gestionacademica.service.impl;

import co.edu.uniquindio.gestionacademica.domain.enums.Rol;
import co.edu.uniquindio.gestionacademica.domain.model.Usuario;
import co.edu.uniquindio.gestionacademica.dto.request.UsuarioRequestDTO;
import co.edu.uniquindio.gestionacademica.dto.response.UsuarioResponseDTO;
import co.edu.uniquindio.gestionacademica.mapper.UsuarioMapper;
import co.edu.uniquindio.gestionacademica.repository.UsuarioRepository;
import co.edu.uniquindio.gestionacademica.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO request) {

        Usuario usuario = usuarioMapper.toEntity(request);

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
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return usuarioMapper.toDTO(usuario);
    }

    @Override
    public UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO request) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setIdentificacion(request.getIdentificacion());
        usuario.setRol(request.getRol());
        usuario.setContrasena(request.getContrasena());

        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        return usuarioMapper.toDTO(usuarioActualizado);
    }

    @Override
    public UsuarioResponseDTO desactivarUsuario(Long id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if(!usuario.isActivo()) {
            throw new RuntimeException("El usuario ya se encuentra desactivado");
        }

        usuario.setActivo(false);

        Usuario usuarioActualizado =  usuarioRepository.save(usuario);

        return usuarioMapper.toDTO(usuarioActualizado);
    }
}
