package co.edu.uniquindio.gestionacademica.service;

import co.edu.uniquindio.gestionacademica.dto.response.IAClasificacionResponseDTO;
import co.edu.uniquindio.gestionacademica.dto.response.ResumenIAResponseDTO;

public interface IAService {

    //Sugerir tipo y prioridad a partir de la descripción
    IAClasificacionResponseDTO sugerirClasificacion(String descripcion);

    //Generar un resumen del historial de una solicitud
    ResumenIAResponseDTO generarResumen(Long solicitudId);
}

