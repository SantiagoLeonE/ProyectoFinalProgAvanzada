package co.edu.uniquindio.gestionacademica.service;

import co.edu.uniquindio.gestionacademica.domain.enums.Rol;
import co.edu.uniquindio.gestionacademica.dto.request.UsuarioRequestDTO;
import co.edu.uniquindio.gestionacademica.dto.response.UsuarioResponseDTO;

import java.util.List;

public interface UsuarioService {

    List<UsuarioResponseDTO> listarUsuarios(Rol rol, Boolean activo);

    UsuarioResponseDTO crearUsuario(UsuarioRequestDTO request);
    UsuarioResponseDTO obtenerUsuarioPorId(Long id);
    UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO request);
    UsuarioResponseDTO activarUsuario(Long id);
    UsuarioResponseDTO desactivarUsuario(Long id);
}
