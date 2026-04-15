package co.edu.uniquindio.gestionacademica.mapper;

import co.edu.uniquindio.gestionacademica.domain.model.Usuario;
import co.edu.uniquindio.gestionacademica.dto.request.UsuarioRequestDTO;
import co.edu.uniquindio.gestionacademica.dto.response.UsuarioResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioRequestDTO dto) {

        return Usuario.builder()
                .identificacion(dto.getIdentificacion())
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .contrasena(dto.getContrasena())
                .rol(dto.getRol())
                .activo(true)
                .build();
    }

    public UsuarioResponseDTO toDTO(Usuario usuario) {

        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .identificacion(usuario.getIdentificacion())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .activo(usuario.isActivo())
                .build();
    }
}
