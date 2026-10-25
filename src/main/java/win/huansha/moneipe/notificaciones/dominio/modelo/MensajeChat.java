package win.huansha.moneipe.notificaciones.dominio.modelo;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MensajeChat {
    String id;
    String grupoId;
    UUID usuarioId;
    String texto;
    Instant fecha;
}
