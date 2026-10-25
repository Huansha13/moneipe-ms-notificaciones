package win.huansha.moneipe.notificaciones.dominio.puerto.entrada;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import win.huansha.moneipe.notificaciones.dominio.modelo.MensajeChat;
import win.huansha.moneipe.notificaciones.dominio.modelo.Notificacion;

import java.util.UUID;

public interface NotificacionCasoUso {
    Flux<Notificacion> stream(UUID usuarioId);
    Mono<Notificacion> crear(UUID usuarioId, String tipo, String titulo, String mensaje, String grupoId);
    Flux<MensajeChat> mensajesDeGrupo(String grupoId);
    Mono<MensajeChat> enviarMensaje(String grupoId, UUID usuarioId, String texto);
}
