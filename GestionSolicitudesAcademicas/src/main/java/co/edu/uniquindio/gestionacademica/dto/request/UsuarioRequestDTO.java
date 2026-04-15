package co.edu.uniquindio.gestionacademica.dto.request;

import co.edu.uniquindio.gestionacademica.domain.enums.Rol;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioRequestDTO {

    private String identificacion;
    private String nombre;
    private String email;
    private String contrasena;
    private Rol rol;
}
