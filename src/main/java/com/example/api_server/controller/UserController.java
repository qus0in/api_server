package com.example.api_server.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.api_server.model.User;
import com.example.api_server.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "User", description = "User management APIs")
public class UserController {
    private final UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Create new user", description = "Creates a new user and returns the created user with ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<User> createUser(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User object that needs to be created",
            required = true,
            content = @Content(
                schema = @Schema(implementation = User.class),
                examples = @ExampleObject(
                    value = "{\"name\": \"John Doe\", \"email\": \"john.doe@example.com\"}"
                )
            )
        ) @RequestBody User user) {
        log.info("* createUser");
        log.debug(user.toString());
        User savedUser = userRepository.save(user);
        log.debug(savedUser.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Returns a list of all users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of users",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    })
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("* getAllUsers");
        List<User> users = userRepository.findAll();
        log.debug(users.toString());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Returns a single user by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<User> getUserById(
        @PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user by ID", description = "Deletes a user by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<Void> deleteUserById(
        @PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user by ID", description = "Updates a user by ID and returns the updated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<User> updateUser(
        @PathVariable Long id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated user object",
            required = true,
            content = @Content(
                schema = @Schema(implementation = User.class),
                examples = @ExampleObject(
                    value = "{\"name\": \"Jane Doe\", \"email\": \"jane.doe@example.com\"}"
                )
            )
        ) @RequestBody User user) {
        Optional<User> oldUser = userRepository.findById(id);
        if (oldUser.isPresent()) {
            user.setId(id);
            User newUser = userRepository.save(user);
            return ResponseEntity.ok(newUser);
        }
        return ResponseEntity.notFound().build();
    }
}
