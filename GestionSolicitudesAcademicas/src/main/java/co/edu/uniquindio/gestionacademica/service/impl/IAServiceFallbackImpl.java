package co.edu.uniquindio.gestionacademica.service.impl;

import co.edu.uniquindio.gestionacademica.dto.response.IAClasificacionResponseDTO;
import co.edu.uniquindio.gestionacademica.dto.response.ResumenIAResponseDTO;
import co.edu.uniquindio.gestionacademica.service.IAService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@ConditionalOnProperty(name = "app.ia.habilitada", havingValue = "false", matchIfMissing = true)
public class IAServiceFallbackImpl implements IAService {

    @Override
    public IAClasificacionResponseDTO sugerirClasificacion(String descripcion) {
        return IAClasificacionResponseDTO.builder()
                .iaDisponible(false)
                .justificacion("IA deshabilitada. Clasifique manualmente.")
                .build();
    }

    @Override
    public ResumenIAResponseDTO generarResumen(Long solicitudId) {
        return ResumenIAResponseDTO.builder()
                .solicitudId(solicitudId)
                .resumen("IA deshabilitada. Consulte el historial directamente.")
                .iaDisponible(false)
                .build();
    }
}
