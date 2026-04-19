package co.edu.uniquindio.gestionacademica.domain.model;

import co.edu.uniquindio.gestionacademica.domain.enums.CanalOrigen;
import co.edu.uniquindio.gestionacademica.domain.enums.EstadoSolicitud;
import co.edu.uniquindio.gestionacademica.domain.enums.Prioridad;
import co.edu.uniquindio.gestionacademica.domain.enums.TipoSolicitud;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitud")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(nullable = false, length = 1000)
    private String justificacionPrioridad;

    @Column(length = 1000)
    private String observacionCierre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoSolicitud tipoSolicitud;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSolicitud estadoSolicitud;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Prioridad prioridad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CanalOrigen canalOrigen;

    @ManyToOne
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Usuario solicitante;

    @ManyToOne
    @JoinColumn(name = "responsable_id")
    private Usuario responsable;
}
