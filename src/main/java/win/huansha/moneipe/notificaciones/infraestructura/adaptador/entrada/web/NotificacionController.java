package win.huansha.moneipe.notificaciones.infraestructura.adaptador.entrada.web;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import win.huansha.moneipe.notificaciones.aplicacion.servicio.NotificacionServicio;
import win.huansha.moneipe.notificaciones.dominio.modelo.MensajeChat;
import win.huansha.moneipe.notificaciones.dominio.modelo.Notificacion;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionServicio servicio;

    // SSE stream de notificaciones para el usuario autenticado
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Notificacion> stream(@RequestHeader("X-User-Id") String userId) {
        return usuarioActual(userId)
            .flatMapMany(servicio::stream);
    }

    // Enviar notificación (usado por otros MS o admin)
    @PostMapping
    public Mono<Notificacion> crear(@RequestBody Map<String, String> body) {
        UUID usuarioId = UUID.fromString(body.get("usuarioId"));
        return servicio.crear(usuarioId, body.get("tipo"), body.get("titulo"),
            body.get("mensaje"), body.get("grupoId"));
    }

    // Listar mensajes de un grupo (chat history)
    @GetMapping("/grupos/{grupoId}/mensajes")
    public Flux<MensajeChat> mensajes(@PathVariable String grupoId) {
        return servicio.mensajesDeGrupo(grupoId);
    }

    // Enviar mensaje de chat
    @PostMapping("/grupos/{grupoId}/mensajes")
    public Mono<MensajeChat> enviarMensaje(@PathVariable String grupoId, @RequestHeader("X-User-Id") String userId, @RequestBody Map<String, String> body) {
        return usuarioActual(userId)
            .flatMap(uid -> servicio.enviarMensaje(grupoId, uid, body.get("texto")));
    }

    private Mono<UUID> usuarioActual(String userId) {
        return Mono.just(UUID.fromString(userId));
    }
}