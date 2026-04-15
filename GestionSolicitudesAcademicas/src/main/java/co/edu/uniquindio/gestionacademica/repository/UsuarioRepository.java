package co.edu.uniquindio.gestionacademica.repository;

import co.edu.uniquindio.gestionacademica.domain.enums.Rol;
import co.edu.uniquindio.gestionacademica.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findByRolAndActivo(Rol rol, Boolean activo);

    List<Usuario> findByRol(Rol rol);

    List<Usuario> findByActivo(Boolean activo);
}
