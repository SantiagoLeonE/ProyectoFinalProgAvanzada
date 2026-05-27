package co.edu.uniquindio.gestionacademica.controller;

import co.edu.uniquindio.gestionacademica.dto.request.IAClasificacionRequestDTO;
import co.edu.uniquindio.gestionacademica.dto.response.IAClasificacionResponseDTO;
import co.edu.uniquindio.gestionacademica.dto.response.ResumenIAResponseDTO;
import co.edu.uniquindio.gestionacademica.service.IAService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ia")
@RequiredArgsConstructor
public class IAController {

    private final IAService iaService;

    //Sugerir tipo y prioridad a partir de la descripción. Cualquier usuario autenticado puede usar este endpoint
    @PostMapping("/sugerir-clasificacion")
    public ResponseEntity<IAClasificacionResponseDTO> sugerirClasificacion(
            @Valid @RequestBody IAClasificacionRequestDTO request) {

        return ResponseEntity.ok(iaService.sugerirClasificacion(request.getDescripcion()));
    }

    //Generar resumen del historial de una solicitud. Solo DOCENTE y ADMINISTRATIVO pueden usar este endpoint
    @GetMapping("/resumir/{id}")
    public ResponseEntity<ResumenIAResponseDTO> generarResumen(@PathVariable Long id) {

        return ResponseEntity.ok(iaService.generarResumen(id));
    }
}
