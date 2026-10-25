package win.huansha.moneipe.notificaciones.infraestructura.adaptador.salida.persistencia;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MensajeChatMongoRepository extends ReactiveMongoRepository<MensajeChatDocument, String> {
}
