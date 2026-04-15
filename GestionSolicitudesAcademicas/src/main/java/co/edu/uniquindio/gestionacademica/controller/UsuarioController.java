package co.edu.uniquindio.gestionacademica.controller;

import co.edu.uniquindio.gestionacademica.domain.enums.Rol;
import co.edu.uniquindio.gestionacademica.dto.request.UsuarioRequestDTO;
import co.edu.uniquindio.gestionacademica.dto.response.UsuarioResponseDTO;
import co.edu.uniquindio.gestionacademica.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crearUsuario(@RequestBody UsuarioRequestDTO request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crearUsuario(request));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios(@RequestParam (required = false) Rol rol, @RequestParam (required = false) Boolean activo) {

        return ResponseEntity.ok(usuarioService.listarUsuarios(rol, activo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerUsuarioPorId(@PathVariable Long id) {

        return ResponseEntity.ok(usuarioService.obtenerUsuarioPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(@PathVariable Long id, @RequestBody UsuarioRequestDTO request) {

        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, request));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<UsuarioResponseDTO> desactivarUsuario(@PathVariable Long id) {

        return ResponseEntity.ok(usuarioService.desactivarUsuario(id));
    }
}
