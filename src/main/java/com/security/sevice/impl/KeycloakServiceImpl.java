package com.security.sevice.impl;

import com.security.controller.dto.UserDTO;
import com.security.sevice.IKeycloakService;
import com.security.util.KeycloakProvider;
import jakarta.ws.rs.core.Response;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
@Service
@Slf4j
public class KeycloakServiceImpl implements IKeycloakService {
    public KeycloakProvider provider;

    public KeycloakServiceImpl(KeycloakProvider provider) {
        this.provider = provider;
    }

    @Override
    public List<UserRepresentation> findAllUsers() {
        return provider.getRealmResource()
                .users()
                .list();
    }

    @Override
    public List<UserRepresentation> searchUserByUsername(String username) {
        return provider.getRealmResource()
                .users().searchByUsername(username,true);//TRUE:el nombre de usuario es exacto
    }

    @Override
    public String createUser(@NonNull UserDTO userDTO) {

        int status = 0;
        UsersResource userResource = provider.getUserResource();

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setFirstName(userDTO.firstName());
        userRepresentation.setLastName(userDTO.lastName());
        userRepresentation.setEmail(userDTO.email());
        userRepresentation.setUsername(userDTO.username());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setEnabled(true);

        Response response = userResource.create(userRepresentation);
        status = response.getStatus();
        //Después de crear el usuario le asignamos la contraseña(si el estatus es 201)
        if(status == 201){
            String path = response.getLocation().getPath();
            String userId = path.substring(path.lastIndexOf("/")+1);

            CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
            credentialRepresentation.setTemporary(false);
            credentialRepresentation.setType(OAuth2Constants.PASSWORD);
            credentialRepresentation.setValue(userDTO.password());

            userResource.get(userId).resetPassword(credentialRepresentation);

            RealmResource realmResource = provider.getRealmResource();

            List<RoleRepresentation> roleRepresentations;

            if (userDTO.roles() == null || userDTO.roles().isEmpty()){
                roleRepresentations = List.of(realmResource.roles().get("user").toRepresentation());
            }else{
                roleRepresentations = realmResource.roles()
                        .list()
                        .stream()
                        .filter(role -> userDTO.roles()
                                .stream()
                                .anyMatch(roleName -> roleName.equalsIgnoreCase(role.getName())))
                        .toList();
            }
            realmResource.users()
                    .get(userId)
                    .roles()
                    .realmLevel()
                    .add(roleRepresentations);

            return "User created successfully";
        }else if(status == 409){
            log.error("User exist already");
            return "User exist already";
        }
        log.error("Error creating user, please contact with the admin");
        return "Error creating user, please contact with the admin";
    }

    @Override
    public void deleteUser(String id) {
        provider.getUserResource()
                .get(id)
                .remove();
    }

    @Override
    public void updateUser(String id,@NonNull UserDTO userDTO) {

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(OAuth2Constants.PASSWORD);
        credentialRepresentation.setValue(userDTO.password());

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setFirstName(userDTO.firstName());
        userRepresentation.setLastName(userDTO.lastName());
        userRepresentation.setEmail(userDTO.email());
        userRepresentation.setUsername(userDTO.username());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setEnabled(true);

        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));

        UserResource userResource = provider.getUserResource().get(id);
        userResource.update(userRepresentation);

    }

    @Override
    public String login(String username, String password) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String,String> form = new LinkedMultiValueMap<>();
        form.add("grant_type","password");
        form.add("client_id",provider.getClientId());
        form.add("client_secret", provider.getClientSecret());
        form.add("username",username);
        form.add("password",password);

        HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(form,headers);
        //http://localhost:9090/realms/cars-realm-dev/protocol/openid-connect/token
        ResponseEntity<String> response = restTemplate.postForEntity(
                provider.getServerUrl()+"/realms/"+provider.getRealm()+"/protocol/openid-connect/token",
                entity,
                String.class
        );
        if (response.getStatusCode().is2xxSuccessful()){
            return response.getBody();
        }else{
            throw new ResponseStatusException(HttpStatus.FAILED_DEPENDENCY, "No fue posible iniciar sesión");
        }
    }
}
