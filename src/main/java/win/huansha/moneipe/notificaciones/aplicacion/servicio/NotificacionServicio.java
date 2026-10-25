package win.huansha.moneipe.notificaciones.aplicacion.servicio;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import win.huansha.moneipe.notificaciones.dominio.modelo.MensajeChat;
import win.huansha.moneipe.notificaciones.dominio.modelo.Notificacion;
import win.huansha.moneipe.notificaciones.dominio.puerto.entrada.NotificacionCasoUso;
import win.huansha.moneipe.notificaciones.infraestructura.adaptador.salida.persistencia.MensajeChatDocument;
import win.huansha.moneipe.notificaciones.infraestructura.adaptador.salida.persistencia.MensajeChatMongoRepository;
import win.huansha.moneipe.notificaciones.infraestructura.adaptador.salida.persistencia.NotificacionDocument;
import win.huansha.moneipe.notificaciones.infraestructura.adaptador.salida.persistencia.NotificacionMongoRepository;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificacionServicio implements NotificacionCasoUso {

    private final NotificacionMongoRepository notifRepo;
    private final MensajeChatMongoRepository mensajeRepo;

    // Sink para SSE: cada usuario tiene su propio sink de notificaciones
    private final Sinks.Many<Notificacion> notifSink = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public Mono<Notificacion> crear(UUID usuarioId, String tipo, String titulo, String mensaje, String grupoId) {
        NotificacionDocument doc = new NotificacionDocument();
        doc.setUsuarioId(usuarioId);
        doc.setTipo(tipo);
        doc.setTitulo(titulo);
        doc.setMensaje(mensaje);
        doc.setGrupoId(grupoId);
        doc.setLeida(false);
        doc.setFecha(Instant.now());

        return notifRepo.save(doc)
            .map(saved -> Notificacion.builder()
                .id(saved.getId())
                .usuarioId(saved.getUsuarioId())
                .tipo(saved.getTipo())
                .titulo(saved.getTitulo())
                .mensaje(saved.getMensaje())
                .grupoId(saved.getGrupoId())
                .leida(saved.isLeida())
                .fecha(saved.getFecha())
                .build())
            .doOnNext(notifSink::tryEmitNext);
    }

    @Override
    public Flux<Notificacion> stream(UUID usuarioId) {
        return notifSink.asFlux()
            .filter(n -> n.getUsuarioId().equals(usuarioId));
    }

    @Override
    public Flux<MensajeChat> mensajesDeGrupo(String grupoId) {
        return mensajeRepo.findAll()
            .filter(d -> grupoId.equals(d.getGrupoId()))
            .map(d -> MensajeChat.builder()
                .id(d.getId())
                .grupoId(d.getGrupoId())
                .usuarioId(d.getUsuarioId())
                .texto(d.getTexto())
                .fecha(d.getFecha())
                .build());
    }

    @Override
    public Mono<MensajeChat> enviarMensaje(String grupoId, UUID usuarioId, String texto) {
        MensajeChatDocument doc = new MensajeChatDocument();
        doc.setGrupoId(grupoId);
        doc.setUsuarioId(usuarioId);
        doc.setTexto(texto);
        doc.setFecha(Instant.now());

        return mensajeRepo.save(doc)
            .map(saved -> MensajeChat.builder()
                .id(saved.getId())
                .grupoId(saved.getGrupoId())
                .usuarioId(saved.getUsuarioId())
                .texto(saved.getTexto())
                .fecha(saved.getFecha())
                .build());
    }
}
