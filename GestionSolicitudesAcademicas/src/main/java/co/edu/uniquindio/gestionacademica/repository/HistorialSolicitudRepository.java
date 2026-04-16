package co.edu.uniquindio.gestionacademica.repository;

import co.edu.uniquindio.gestionacademica.domain.model.HistorialSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialSolicitudRepository extends JpaRepository<HistorialSolicitud,Long> {

    List<HistorialSolicitud> findBySolicitudIdOrderByFechaAccionAsc(Long solicitudId);
}
