package win.huansha.moneipe.notificaciones.infraestructura.adaptador.salida.persistencia;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface NotificacionMongoRepository extends ReactiveMongoRepository<NotificacionDocument, String> {
}
