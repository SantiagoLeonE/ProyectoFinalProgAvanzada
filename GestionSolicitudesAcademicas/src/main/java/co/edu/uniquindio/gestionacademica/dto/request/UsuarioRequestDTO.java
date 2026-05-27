package co.edu.uniquindio.gestionacademica.dto.request;

import co.edu.uniquindio.gestionacademica.domain.enums.Rol;
import jakarta.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioRequestDTO {

    //La identificación no puede estar vacía ni nula porque es la referencia única de un usuario
    @NotBlank(message = "La identificación es obligatoria")
    @Size(max = 12, message = "La identificación no puede tener más de 12 caracteres")
    private String identificacion;

    //El nombre no puede estar vacío ni nulo
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 200, message = "El nombre no puede tener más de 200 caracteres")
    private String nombre;

    //El @Email válida que el email tenga un formato correcto como nombre_cuenta@correo.com
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    //La contraseña es obligatoria, por lo tanto, no puede ser vacía ni nula
    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;

    //El rol de un usuario es obligatorio, ya que define las acciones que se pueden realizar
    @NotNull(message = "El rol es obligatorio")
    private Rol rol;
}
