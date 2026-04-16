package co.edu.uniquindio.gestionacademica.controller;


import co.edu.uniquindio.gestionacademica.dto.response.HistorialSolicitudResponseDTO;
import co.edu.uniquindio.gestionacademica.service.HistorialSolicitudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solicitudes")
@RequiredArgsConstructor
public class HistorialSolicitudController {

    private final HistorialSolicitudService historialSolicitudService;

    @GetMapping("/{id}/historial")
    public ResponseEntity<List<HistorialSolicitudResponseDTO>> obtenerHistorialSolicitud(@PathVariable Long id) {

        return ResponseEntity.ok(historialSolicitudService.obtenerHistorialPorSolicitud(id));
    }
}
