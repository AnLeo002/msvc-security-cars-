package com.security.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "keycloak")
@Getter
@Setter
public class KeycloakProperties {
    private String serverUrl;
    private String realmName;
    private String username;
    private String password;
    private String adminCli;
    private String clientSecret;
    private String clientId;
}
