package com.security.util;

import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.stereotype.Component;
@Component
public class KeycloakProvider {
    private  KeycloakProperties properties;

    public KeycloakProvider(KeycloakProperties properties) {
        this.properties = properties;
    }

    public  RealmResource getRealmResource(){//el realmResource nos permite acceder a la api de keycloak
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(properties.getServerUrl())
                .realm("master")
                .clientId(properties.getAdminCli())
                .username(properties.getUsername())
                .password(properties.getPassword())
                .clientSecret(properties.getClientSecret())
                .resteasyClient(new ResteasyClientBuilderImpl()
                        .connectionPoolSize(10)
                        .build())
                .build();
        return keycloak.realm(properties.getRealmName());
    }
    public UsersResource getUserResource(){//Permite el manejo de usuarios
        RealmResource realmResource = getRealmResource();
        return realmResource.users();
    }
    public String getClientSecret(){
        return properties.getClientSecret();
    }
    public String getClientId(){
        return properties.getClientId();
    }
    public String getRealm(){
        return properties.getRealmName();
    }
    public String getServerUrl(){
        return properties.getServerUrl();
    }
}
