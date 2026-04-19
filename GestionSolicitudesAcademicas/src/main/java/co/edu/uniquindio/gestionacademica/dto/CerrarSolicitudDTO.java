package co.edu.uniquindio.gestionacademica.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CerrarSolicitudDTO {

    //La observación es obligatoria cuando se desea cerrar una solicitud atendida, no puede ser nula ni vacía
    @NotBlank(message = "La observación de cierre es obligatoria")
    private String observacionCierre;
}
