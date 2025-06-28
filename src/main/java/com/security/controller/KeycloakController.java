package com.security.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.controller.dto.UserDTO;
import com.security.controller.dto.UserLoginDTO;
import com.security.sevice.IKeycloakService;
import jakarta.validation.Valid;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/keycloak/user")
public class KeycloakController {
    @Autowired
    private IKeycloakService keycloakService;

    @GetMapping("/me")
    public ResponseEntity<String> me(@AuthenticationPrincipal Jwt principal) {
        return ResponseEntity.ok("Usuario autenticado: " + principal.getSubject());
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>> login(@RequestBody @Valid UserLoginDTO userLoginDTO) {
        String tokenJson = keycloakService.login(
                userLoginDTO.username(),
                userLoginDTO.password()
        );

        // Convertir el String JSON a un Map para que el frontend reciba un objeto JSON válido
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> tokenMap = mapper.readValue(tokenJson, new TypeReference<>() {});
            return ResponseEntity.ok(tokenMap);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Error al procesar el token");
        }
    }
    @GetMapping("/search")
    @PreAuthorize("hasRole('admin_client_role')")
    public ResponseEntity<List<UserRepresentation>> findAllUsers(){
        return ResponseEntity.ok(keycloakService.findAllUsers());
    }
    @GetMapping("/search/{username}")
    public ResponseEntity<List<UserRepresentation>> findUserByUsername(@PathVariable String username){
        return ResponseEntity.ok(keycloakService.searchUserByUsername(username));
    }
    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody @Valid UserDTO userDTO) throws URISyntaxException {
        String response = keycloakService.createUser(userDTO);
        return ResponseEntity.created(new URI("/keycloak/user/create")).body(response);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateUser(@RequestBody UserDTO userDTO, @PathVariable String id) throws URISyntaxException {
        keycloakService.updateUser(id,userDTO);
        return ResponseEntity.ok("User updated successfully");
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id){
        keycloakService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
