package co.edu.uniquindio.gestionacademica.controller;

import co.edu.uniquindio.gestionacademica.domain.enums.*;
import co.edu.uniquindio.gestionacademica.dto.*;
import co.edu.uniquindio.gestionacademica.dto.request.SolicitudRequestDTO;
import co.edu.uniquindio.gestionacademica.dto.response.SolicitudResponseDTO;
import co.edu.uniquindio.gestionacademica.service.SolicitudService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

    private final SolicitudService solicitudService;

    @PostMapping
    public ResponseEntity<SolicitudResponseDTO> crearSolicitud(@RequestBody SolicitudRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(solicitudService.crearSolicitud(request));
    }

    @GetMapping
    public ResponseEntity<Page<SolicitudResponseDTO>> listarSolicitudes(
            @RequestParam(required = false) EstadoSolicitud estadoSolicitud,
            @RequestParam(required = false)TipoSolicitud tipoSolicitud,
            @RequestParam(required = false) Prioridad prioridad,
            @RequestParam(required = false) Long responsableId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size

    ) {
        return ResponseEntity.ok(solicitudService.listarSolicitudes(
                estadoSolicitud, tipoSolicitud, prioridad, responsableId, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudResponseDTO> obtenerSolicitudPorId(@PathVariable Long id) {
        return ResponseEntity.ok(solicitudService.obtenerSolicitudPorId(id));
    }

    @PatchMapping("/{id}/clasificar")
    public ResponseEntity<SolicitudResponseDTO> clasificarSolicitud(@PathVariable Long id,  @RequestBody ClasificarSolicitudDTO request) {
        return ResponseEntity.ok(solicitudService.clasificarSolicitud(id, request));
    }

    @PatchMapping("/{id}/asignar")
    public ResponseEntity<SolicitudResponseDTO> asignarResponsable(@PathVariable Long id,  @RequestBody AsignarResponsableDTO request) {
        return ResponseEntity.ok(solicitudService.asignarResponsable(id, request));
    }

    @PatchMapping("/{id}/atender")
    public ResponseEntity<SolicitudResponseDTO> atenderSolicitud(@PathVariable Long id) {
        return ResponseEntity.ok(solicitudService.atenderSolicitud(id));
    }

    @PatchMapping("/{id}/resolver")
    public ResponseEntity<SolicitudResponseDTO> resolverSolicitud(@PathVariable Long id) {
        return ResponseEntity.ok(solicitudService.resolverSolicitud(id));
    }

    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<SolicitudResponseDTO> cerrarSolicitud(@PathVariable Long id, @RequestBody CerrarSolicitudDTO request) {
        return ResponseEntity.ok(solicitudService.cerrarSolicitud(id, request));
    }
}
