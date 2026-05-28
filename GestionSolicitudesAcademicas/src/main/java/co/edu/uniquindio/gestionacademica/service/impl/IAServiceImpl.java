package co.edu.uniquindio.gestionacademica.service.impl;

import co.edu.uniquindio.gestionacademica.domain.model.HistorialSolicitud;
import co.edu.uniquindio.gestionacademica.domain.model.Solicitud;
import co.edu.uniquindio.gestionacademica.dto.response.ResumenIAResponseDTO;
import co.edu.uniquindio.gestionacademica.exception.RecursoNoEncontradoException;
import co.edu.uniquindio.gestionacademica.repository.HistorialSolicitudRepository;
import co.edu.uniquindio.gestionacademica.repository.SolicitudRepository;
import co.edu.uniquindio.gestionacademica.service.IAService;
import co.edu.uniquindio.gestionacademica.dto.response.IAClasificacionResponseDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.ia.habilitada", havingValue = "true")
public class IAServiceImpl implements IAService {

    private final HistorialSolicitudRepository historialRepository;
    private final SolicitudRepository solicitudRepository;
    private final ChatClient chatClient;

    @Value("${app.ia.habilitada}")
    private boolean iaHabilitada;

    //Sugerir tipo y prioridad a partir de la descripción
    @Override
    public IAClasificacionResponseDTO sugerirClasificacion(String descripcion) {

        if (!iaHabilitada) {
            return IAClasificacionResponseDTO.builder()
                    .iaDisponible(false)
                    .justificacion("IA deshabilitada. Clasifique manualmente.")
                    .build();
        }

        try {
            String prompt = """
                    Eres un asistente del sistema de gestión de solicitudes académicas.
                    Analiza la siguiente descripción y sugiere:
                    1. El tipo de solicitud más adecuado entre estas opciones:
                       REGISTRO_ASIGNATURA, HOMOLOGACION, CANCELACION,
                       SOLICITUD_CUPO, CONSULTA_ACADEMICA
                    2. La prioridad entre: ALTA, MEDIA, BAJA
                    3. Una justificación breve de tu sugerencia
 
                    Descripción: %s
 
                    Responde ÚNICAMENTE en este formato JSON sin texto adicional:
                    {
                        "tipoSolicitud": "TIPO_AQUI",
                        "prioridad": "PRIORIDAD_AQUI",
                        "justificacion": "Justificación breve aquí"
                    }
                    """.formatted(descripcion);

            String respuestaIA = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            return parsearRespuestaClasificacion(respuestaIA);

        } catch (Exception e) {
            //Fallback: si la IA falla el sistema sigue funcionando
            return IAClasificacionResponseDTO.builder()
                    .iaDisponible(false)
                    .justificacion("IA no disponible en este momento. Clasifique manualmente.")
                    .build();
        }
    }

    //Generar un resumen del historial de una solicitud
    @Override
    public ResumenIAResponseDTO generarResumen(Long solicitudId) {

        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Solicitud con id " + solicitudId + " no encontrada"));

        if (!iaHabilitada) {
            return ResumenIAResponseDTO.builder()
                    .solicitudId(solicitudId)
                    .resumen("IA deshabilitada. Consulte el historial directamente.")
                    .iaDisponible(false)
                    .build();
        }

        try {
            List<HistorialSolicitud> historial = historialRepository
                    .findBySolicitudIdOrderByFechaAccionAsc(solicitudId);

            StringBuilder contexto = new StringBuilder();
            contexto.append("Solicitud id: ").append(solicitudId).append("\n");
            contexto.append("Descripción: ").append(solicitud.getDescripcion()).append("\n");
            contexto.append("Estado actual: ").append(solicitud.getEstadoSolicitud()).append("\n");
            contexto.append("Tipo: ").append(solicitud.getTipoSolicitud()).append("\n");
            contexto.append("Prioridad: ").append(solicitud.getPrioridad()).append("\n");
            contexto.append("Historial de acciones:\n");

            for (HistorialSolicitud entrada : historial) {
                contexto.append("- ")
                        .append(entrada.getFechaAccion()).append(": ")
                        .append(entrada.getAccionRealizada());

                contexto.append("\n");
            }

            String prompt = """
                    Eres un asistente del sistema de gestión de solicitudes académicas.
                    Genera un resumen claro y conciso del siguiente caso para que
                    el responsable entienda rápidamente el estado actual y el historial.
                    El resumen debe tener máximo 3 oraciones.
 
                    %s
 
                    Responde ÚNICAMENTE con el texto del resumen, sin formato adicional.
                    """.formatted(contexto.toString());

            String resumen = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            return ResumenIAResponseDTO.builder()
                    .solicitudId(solicitudId)
                    .resumen(resumen)
                    .iaDisponible(true)
                    .build();

        } catch (Exception e) {
            return ResumenIAResponseDTO.builder()
                    .solicitudId(solicitudId)
                    .resumen("IA no disponible. Consulte el historial detallado de la solicitud.")
                    .iaDisponible(false)
                    .build();
        }
    }

    private IAClasificacionResponseDTO parsearRespuestaClasificacion(String respuestaJson) {
        try {
            //Limpiar la respuesta por si el modelo agrega texto extra
            String jsonLimpio = respuestaJson;
            int inicio = respuestaJson.indexOf("{");
            int fin = respuestaJson.lastIndexOf("}");
            if (inicio != -1 && fin != -1) {
                jsonLimpio = respuestaJson.substring(inicio, fin + 1);
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode nodo = mapper.readTree(jsonLimpio);

            return IAClasificacionResponseDTO.builder()
                    .tipoSolicitudSugerido(nodo.get("tipoSolicitud").asText())
                    .prioridadSugerida(nodo.get("prioridad").asText())
                    .justificacion(nodo.get("justificacion").asText())
                    .iaDisponible(true)
                    .build();

        } catch (Exception e) {
            return IAClasificacionResponseDTO.builder()
                    .iaDisponible(false)
                    .justificacion("No se pudo interpretar la respuesta de la IA.")
                    .build();
        }
    }
}
