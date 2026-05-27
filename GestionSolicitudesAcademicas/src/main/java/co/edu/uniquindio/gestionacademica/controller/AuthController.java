package co.edu.uniquindio.gestionacademica.controller;

import co.edu.uniquindio.gestionacademica.domain.model.Usuario;
import co.edu.uniquindio.gestionacademica.dto.request.LoginRequestDTO;
import co.edu.uniquindio.gestionacademica.dto.request.UsuarioRequestDTO;
import co.edu.uniquindio.gestionacademica.dto.response.LoginResponseDTO;
import co.edu.uniquindio.gestionacademica.exception.DatosInvalidosException;
import co.edu.uniquindio.gestionacademica.exception.RecursoNoEncontradoException;
import co.edu.uniquindio.gestionacademica.mapper.UsuarioMapper;
import co.edu.uniquindio.gestionacademica.repository.UsuarioRepository;
import co.edu.uniquindio.gestionacademica.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UsuarioMapper usuarioMapper;

    //Endpoint para registrar un usuario y obtener su token
    @PostMapping("/registro")
    public ResponseEntity<LoginResponseDTO> registro(@Valid @RequestBody UsuarioRequestDTO request) {

        //Verificar que el email no esté ya registrado
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DatosInvalidosException("Ya existe un usuario con el email " + request.getEmail());
        }

        //Verificar que la identificación no esté ya registrada
        if (usuarioRepository.findByIdentificacion(request.getIdentificacion()).isPresent()) {
            throw new DatosInvalidosException("Ya existe un usuario con la identificación " + request.getIdentificacion());
        }

        Usuario usuario = usuarioMapper.toEntity(request);
        usuario.setContrasena(passwordEncoder.encode(request.getContrasena()));

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        String token = jwtService.generarToken(usuarioGuardado);

        return ResponseEntity.status(HttpStatus.CREATED).body(LoginResponseDTO.builder()
                .id(usuario.getId())
                .token(token)
                .nombre(usuarioGuardado.getNombre())
                .email(usuarioGuardado.getEmail())
                .rol(usuarioGuardado.getRol())
                .build());
    }

    //Endpoint para iniciar sesión y obtener token
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request) {

        //Buscar el usuario por email
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario con email " + request.getEmail() + " no encontrado"));

        //Verificar que la contraseña sea correcta
        if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
            throw new DatosInvalidosException("Contraseña incorrecta");
        }

        //Verificar que el usuario esté activo
        if (!usuario.isActivo()) {
            throw new DatosInvalidosException("El usuario está desactivado y no puede iniciar sesión");
        }

        String token = jwtService.generarToken(usuario);

        return ResponseEntity.ok(LoginResponseDTO.builder()
                .id(usuario.getId())
                .token(token)
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .build());
    }
}
