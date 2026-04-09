package co.edu.uniquindio.gestionacademica.repository;

import co.edu.uniquindio.gestionacademica.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByIdentificacion(String email);
}
