package co.edu.uniquindio.gestionacademica.repository;

import co.edu.uniquindio.gestionacademica.domain.enums.EstadoSolicitud;
import co.edu.uniquindio.gestionacademica.domain.enums.Prioridad;
import co.edu.uniquindio.gestionacademica.domain.enums.TipoSolicitud;
import co.edu.uniquindio.gestionacademica.domain.model.Solicitud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;



public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    Page<Solicitud> findByEstadoSolicitudAndTipoSolicitudAndPrioridadAndResponsableId(
            EstadoSolicitud estadoSolicitud,
            TipoSolicitud tipoSolicitud,
            Prioridad prioridad,
            Long responsableId,
            Pageable pageable);

    Page<Solicitud> findByEstadoSolicitud(EstadoSolicitud solicitud, Pageable pageable);
    Page<Solicitud> findByTipoSolicitud(TipoSolicitud tipoSolicitud, Pageable pageable);
    Page<Solicitud> findByPrioridad(Prioridad prioridad, Pageable pageable);
    Page<Solicitud> findByResponsableId(Long responsableId, Pageable pageable);
}
