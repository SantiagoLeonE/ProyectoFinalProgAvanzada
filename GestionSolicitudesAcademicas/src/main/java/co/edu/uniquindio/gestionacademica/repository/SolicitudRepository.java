package co.edu.uniquindio.gestionacademica.repository;

import co.edu.uniquindio.gestionacademica.domain.enums.*;
import co.edu.uniquindio.gestionacademica.domain.model.Solicitud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    Page<Solicitud> findByResponsableId(Long responsableId, Pageable pageable);
    Page<Solicitud> findBySolicitanteId(Long solicitanteId, Pageable pageable);

    boolean existsByResponsableIdAndEstadoSolicitudIn(Long responsableId, List<EstadoSolicitud> estados);
    boolean existsBySolicitanteIdAndEstadoSolicitudIn(Long solicitanteId, List<EstadoSolicitud> estados);

}
