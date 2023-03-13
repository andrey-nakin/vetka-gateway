package io.vetka.gateway.persistence.mongo.document;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
public class GraphQlEndpointDocument {

    @Id
    private String id;
    @Indexed(unique = true)
    @NonNull
    private String name;
    @NonNull
    private String address;
    @NonNull
    private String schema;
    private String httpVersion;
    private Integer connectTimeout;
    private Integer readTimeout;

    @Version
    private Long version;

    @Override
    public String toString() {
        return "GraphQlEndpointDocument{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", address='" + address + '\'' + ", schema='...', httpVersion='" + httpVersion + '\'' + ", connectTimeout=" + connectTimeout + ", readTimeout=" + readTimeout + ", version=" + version + '}';
    }
}
