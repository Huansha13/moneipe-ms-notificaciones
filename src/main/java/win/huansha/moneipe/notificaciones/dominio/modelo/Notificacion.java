package win.huansha.moneipe.notificaciones.dominio.modelo;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Notificacion {
    String id;
    UUID usuarioId;
    String tipo;        // INVITACION, NUEVO_GASTO, NUEVO_MENSAJE
    String titulo;
    String mensaje;
    String grupoId;
    boolean leida;
    Instant fecha;
}
