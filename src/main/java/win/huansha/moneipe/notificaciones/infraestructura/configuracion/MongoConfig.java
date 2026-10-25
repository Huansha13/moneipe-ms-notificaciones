package win.huansha.moneipe.notificaciones.infraestructura.configuracion;

import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.bson.UuidRepresentation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;

@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Bean
    public MongoClient mongoClient() {
        // Parse URI manually to add uuidRepresentation
        MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(new com.mongodb.ConnectionString(mongoUri))
            .uuidRepresentation(org.bson.UuidRepresentation.STANDARD)
            .build();
        return MongoClients.create(settings);
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate(MongoClient mongoClient) {
        // Extract database name from URI
        com.mongodb.ConnectionString cs = new com.mongodb.ConnectionString(mongoUri);
        String dbName = cs.getDatabase();
        return new ReactiveMongoTemplate(new SimpleReactiveMongoDatabaseFactory(mongoClient, dbName));
    }
}
