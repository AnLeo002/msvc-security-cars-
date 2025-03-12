package com.security.util;

import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;

public class KeycloakProvider {
    private static final String SERVER_URL = "http://localhost:9090";
    private static final String REALM_NAME = "cars-realm-dev";
    private static final String REALM_MASTER =  "master";
    private static final String ADMIN_CLI = "admin-cli";
    private static final String USER_CONSOLE = "superadmin";
    private static final String PASSWORD_CONSOLE = "admin";
    private static final String CLIENT_SECRET = "WYdY8MzRbIpwUEuVeloh0FreuQdMurTz";
    public static RealmResource getRealmResource(){//el realmResource nos permite acceder a la api de keycloak
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(SERVER_URL)
                .realm(REALM_MASTER)
                .clientId(ADMIN_CLI)
                .username(USER_CONSOLE)
                .password(PASSWORD_CONSOLE)
                .clientSecret(CLIENT_SECRET)
                .resteasyClient(new ResteasyClientBuilderImpl()
                        .connectionPoolSize(10)
                        .build())
                .build();
        return keycloak.realm(REALM_NAME);
    }
    public static UsersResource getUserResource(){//Permite el manejo de usuarios
        RealmResource realmResource = getRealmResource();
        return realmResource.users();

    }
}
