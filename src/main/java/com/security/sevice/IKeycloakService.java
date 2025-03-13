package com.security.sevice;

import com.security.controller.dto.UserDTO;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public interface IKeycloakService {
    List<UserRepresentation> findAllUsers();
    List<UserRepresentation> searchUserByUsername(String username);
    String createUser(UserDTO userDTO);
    void deleteUser(String id);
    void updateUser(String id,UserDTO userDTO);
}
