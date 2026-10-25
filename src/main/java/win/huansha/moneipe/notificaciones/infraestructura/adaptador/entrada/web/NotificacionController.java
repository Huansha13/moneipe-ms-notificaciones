package win.huansha.moneipe.notificaciones.infraestructura.adaptador.entrada.web;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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
    public Flux<Notificacion> stream() {
        return usuarioActual()
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
    public Mono<MensajeChat> enviarMensaje(@PathVariable String grupoId, @RequestBody Map<String, String> body) {
        return usuarioActual()
            .flatMap(uid -> servicio.enviarMensaje(grupoId, uid, body.get("texto")));
    }

    private Mono<UUID> usuarioActual() {
        return ReactiveSecurityContextHolder.getContext()
            .map(ctx -> ctx.getAuthentication())
            .cast(JwtAuthenticationToken.class)
            .map(auth -> UUID.fromString(auth.getToken().getSubject()));
    }
}
