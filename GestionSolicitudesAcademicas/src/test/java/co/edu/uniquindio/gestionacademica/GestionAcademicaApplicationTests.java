package co.edu.uniquindio.gestionacademica;

import co.edu.uniquindio.gestionacademica.domain.enums.*;
import co.edu.uniquindio.gestionacademica.domain.model.Usuario;
import co.edu.uniquindio.gestionacademica.dto.*;
import co.edu.uniquindio.gestionacademica.dto.request.SolicitudRequestDTO;
import co.edu.uniquindio.gestionacademica.dto.response.HistorialSolicitudResponseDTO;
import co.edu.uniquindio.gestionacademica.dto.response.SolicitudResponseDTO;
import co.edu.uniquindio.gestionacademica.exception.DatosInvalidosException;
import co.edu.uniquindio.gestionacademica.exception.EstadoInvalidoException;
import co.edu.uniquindio.gestionacademica.repository.UsuarioRepository;
import co.edu.uniquindio.gestionacademica.service.HistorialSolicitudService;
import co.edu.uniquindio.gestionacademica.service.SolicitudService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GestionAcademicaApplicationTests {

    @Autowired
    private SolicitudService solicitudService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private HistorialSolicitudService historialSolicitudService;

    private Usuario solicitante;
    private Usuario responsable;

    //Se ejecuta antes de cada prueba para tener datos listos
    @BeforeEach
    void setUp() {
        solicitante = usuarioRepository.save(Usuario.builder()
                .nombre("Estudiante Test")
                .email("estudiante@test.com")
                .identificacion("111")
                .contrasena("pass")
                .rol(Rol.ESTUDIANTE)
                .activo(true)
                .build());

        responsable = usuarioRepository.save(Usuario.builder()
                .nombre("Docente Test")
                .email("docente@test.com")
                .identificacion("222")
                .contrasena("pass")
                .rol(Rol.DOCENTE)
                .activo(true)
                .build());
    }

    /*
     * Prueba que una solicitud se crea correctamente con sus respectivos atributos
     */
    @Test
    void crearSolicitud() {
        SolicitudRequestDTO request = SolicitudRequestDTO.builder()
                .descripcion("Registrar la asignatura de Bases de Datos")
                .tipoSolicitud(TipoSolicitud.REGISTRO_ASIGNATURA)
                .justificacionPrioridad("Necesito realizar el registro lo más pronto posible para continuar con mis estudios")
                .canalOrigen(CanalOrigen.CSU)
                .solicitanteId(solicitante.getId())
                .build();

        SolicitudResponseDTO response = solicitudService.crearSolicitud(request);

        assertEquals("Registrar la asignatura de Bases de Datos", response.getDescripcion());
        assertEquals("Necesito realizar el registro lo más pronto posible para continuar con mis estudios",  response.getJustificacionPrioridad());
        assertEquals(TipoSolicitud.REGISTRO_ASIGNATURA, response.getTipoSolicitud());
        assertEquals(EstadoSolicitud.REGISTRADA, response.getEstadoSolicitud());
        assertNull(response.getPrioridad());
        assertEquals(CanalOrigen.CSU, response.getCanalOrigen());
        assertEquals(solicitante.getId(), response.getSolicitanteId());
    }

    /*
     * Prueba que no se puede asignar un usuario con rol ESTUDIANTE como responsable de una solicitud
     */
    @Test
    void asignarResponsable() {
        SolicitudRequestDTO request = SolicitudRequestDTO.builder()
                .descripcion("Registrar la asignatura de Bases de Datos")
                .tipoSolicitud(TipoSolicitud.REGISTRO_ASIGNATURA)
                .justificacionPrioridad("Necesito realizar el registro lo más pronto posible para continuar con mis estudios")
                .canalOrigen(CanalOrigen.CSU)
                .solicitanteId(solicitante.getId())
                .build();

        SolicitudResponseDTO response = solicitudService.crearSolicitud(request);

        AsignarResponsableDTO asignar = new AsignarResponsableDTO(solicitante.getId());

        assertThrows(DatosInvalidosException.class, () ->
                solicitudService.asignarResponsable(response.getId(), asignar));
    }

    /*
     * Prueba que una solicitud ya cerrada no puede modificarse de ninguna manera
     */
    @Test
    @WithMockUser(username = "docente@test.com")
    void cerrarSolicitud() {
        SolicitudRequestDTO request = SolicitudRequestDTO.builder()
                .descripcion("Registrar la asignatura de Bases de Datos")
                .tipoSolicitud(TipoSolicitud.REGISTRO_ASIGNATURA)
                .justificacionPrioridad("Necesito realizar el registro lo más pronto posible para continuar con mis estudios")
                .canalOrigen(CanalOrigen.CSU)
                .solicitanteId(solicitante.getId())
                .build();

        SolicitudResponseDTO response = solicitudService.crearSolicitud(request);

        solicitudService.clasificarSolicitud(
                response.getId(),
                ClasificarSolicitudDTO.builder()
                        .tipoSolicitud(TipoSolicitud.REGISTRO_ASIGNATURA)
                        .impactoAcademico("")
                        .fechaLimite(LocalDate.now().plusDays(1))
                        .prioridadManual(null)
                        .build()
        );
        solicitudService.asignarResponsable(response.getId(), new AsignarResponsableDTO(responsable.getId()));
        solicitudService.atenderSolicitud((response.getId()));
        solicitudService.resolverSolicitud(response.getId());
        solicitudService.cerrarSolicitud(response.getId(), new CerrarSolicitudDTO("Solicitud cerrada sin ninguna novedad"));

        assertThrows(EstadoInvalidoException.class, () ->
                solicitudService.cerrarSolicitud(response.getId(), new CerrarSolicitudDTO("Intento de cierre cuando la solicitud ya está cerrada")));
    }

    /*
     * Prueba que el historial de una solicitud se registra al crear una solicitud
     */
    @Test
    void registrarHistorial() {
        SolicitudRequestDTO request = SolicitudRequestDTO.builder()
                .descripcion("Registrar la asignatura de Bases de Datos")
                .tipoSolicitud(TipoSolicitud.REGISTRO_ASIGNATURA)
                .justificacionPrioridad("Necesito realizar el registro lo más pronto posible para continuar con mis estudios")
                .canalOrigen(CanalOrigen.CSU)
                .solicitanteId(solicitante.getId())
                .build();

        SolicitudResponseDTO response = solicitudService.crearSolicitud(request);

        List<HistorialSolicitudResponseDTO> historial = historialSolicitudService.obtenerHistorialPorSolicitud(response.getId());

        assertFalse(historial.isEmpty());
    }

    /*
     * Prueba para comprobar el funcionamiento correcto de la asignación de la prioridad por el tipo
     */
    @Test
    @WithMockUser(username = "docente@test.com")
    void clasificarSolicitud() {
        SolicitudRequestDTO request = SolicitudRequestDTO.builder()
                .descripcion("Prueba válida de longitud")
                .tipoSolicitud(TipoSolicitud.REGISTRO_ASIGNATURA)
                .justificacionPrioridad("Prueba importante para funcionamiento")
                .canalOrigen(CanalOrigen.CSU)
                .solicitanteId(solicitante.getId())
                .build();

        // Crear
        SolicitudResponseDTO creada = solicitudService.crearSolicitud(request);
        assertNull(creada.getPrioridad());

        // Clasificar
        SolicitudResponseDTO clasificada = solicitudService.clasificarSolicitud(creada.getId(),
                ClasificarSolicitudDTO.builder()
                        .tipoSolicitud(TipoSolicitud.REGISTRO_ASIGNATURA)
                        .impactoAcademico("")
                        .fechaLimite(LocalDate.now().plusDays(1))
                        .prioridadManual(null)
                        .build()
        );

        assertEquals(Prioridad.ALTA, clasificada.getPrioridad());
    }
}
