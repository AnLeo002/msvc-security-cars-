package com.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component//Este componente se encarga de convertir el rol que viene de keycloak a un rol entendible para spring security
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    //Constante que nos permite tomar las autorizaciones que provienen del token
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    @Value("${jwt.auth.converter.principalAttribute}")//Nombre del usuario que genero el token
    private String principalAttribute;
    @Value("${jwt.auth.converter.resource-id}")//Nombre del cliente
    private String resourceId;

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {

        //se encarga de convertir los roles de un JWT en autoridades de Spring Security (GrantedAuthority).
        Collection<GrantedAuthority> authorities = Stream
                .concat(jwtGrantedAuthoritiesConverter.convert(source).stream(), extractResourceRoles(source).stream())
                .toList();

        return new JwtAuthenticationToken(source,authorities, getPrincipalName(source));
    }
    //Metodo para acceder al los accesos del token, accedemos al cliente, despues a los roles y finalmente tomamos el rol
      /*"resource_access": {
        "cars-client": {
            "roles": [
            "user_client_role"]}*/
    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt){

        Map<String,Object> resourceAccess;
        Map<String,Object> resource;
        Collection<String> resourceRoles;

        if(jwt.getClaim("resource_access") == null){//Si este claim no existe significa que el joken no tiene roles asignado, por lo tanto es invalido
            return Set.of();
        }
        resourceAccess = jwt.getClaim("resource_access");

        if (resourceAccess.get(resourceId) == null ){//Si este cliente no existe en el token significa que el token es invalido
            return Set.of();
        }
        resource = (Map<String, Object>) resourceAccess.get(resourceId);//Si no existen los roles entonces el token es invalido
        if(resource.get("roles") == null){
            return Set.of();
        }
        resourceRoles = (Collection<String>) resource.get("roles");//recuperamos los roles

        return resourceRoles.stream()//agragamos ROLE_ para que springSecurity entienda que esto es un rol
                .map(role -> new SimpleGrantedAuthority("ROLE_".concat(role)))
                .collect(Collectors.toSet());

    }
    private String getPrincipalName(Jwt jwt){
        String claimName = JwtClaimNames.SUB;//Constante del token, es un identificador del token

        if(principalAttribute != null){//El nombre de usuario que genero el token no puede estar vacio
            claimName = principalAttribute;//sobreescribimos la variable y le asignamos el nombre de usuario
        }
        //si el nombre de usuario viene vacio retorna el identificador, si no retorna el nombre de usuario del token
        return jwt.getClaim(claimName);
    }
}
