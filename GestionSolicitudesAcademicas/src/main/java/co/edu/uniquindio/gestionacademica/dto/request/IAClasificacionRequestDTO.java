package co.edu.uniquindio.gestionacademica.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IAClasificacionRequestDTO {

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;
}
