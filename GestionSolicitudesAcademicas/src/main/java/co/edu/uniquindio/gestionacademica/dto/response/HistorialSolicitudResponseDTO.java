package co.edu.uniquindio.gestionacademica.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialSolicitudResponseDTO {

    private Long id;
    private LocalDateTime fechaAccion;
    private String accionRealizada;
    private Long solicitudId;
    private Long responsableId;
}
