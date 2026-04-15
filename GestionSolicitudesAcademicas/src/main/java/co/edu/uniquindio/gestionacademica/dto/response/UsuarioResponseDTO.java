package co.edu.uniquindio.gestionacademica.dto.response;

import co.edu.uniquindio.gestionacademica.domain.enums.Rol;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {

    private Long id;
    private String identificacion;
    private String nombre;
    private String email;
    private Rol rol;
    private boolean activo;
}
