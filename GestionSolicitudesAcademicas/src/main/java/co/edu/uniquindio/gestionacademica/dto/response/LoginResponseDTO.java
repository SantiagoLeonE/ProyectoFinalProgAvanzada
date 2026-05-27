package co.edu.uniquindio.gestionacademica.dto.response;

import co.edu.uniquindio.gestionacademica.domain.enums.Rol;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {

    private Long id;
    private String token;
    private String nombre;
    private String email;
    private Rol rol;
}
