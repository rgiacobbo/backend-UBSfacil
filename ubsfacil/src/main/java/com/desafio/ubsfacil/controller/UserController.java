package com.desafio.ubsfacil.controller;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.desafio.ubsfacil.models.UserModel;
import com.desafio.ubsfacil.repository.UserRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody UserModel userModel) {
        var user = this.userRepository.findByUsername(userModel.getUsername());

        if (user != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário já existe");
        }

        var passwordHashred = BCrypt.withDefaults()
                .hashToString(12, userModel.getPassword().toCharArray());

        userModel.setPassword(passwordHashred);
        var userCreated = this.userRepository.save(userModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }

    @GetMapping("/")
    public ResponseEntity<Page<UserModel>> getAllPersons(Pageable pageable) {
        Page<UserModel> user = userRepository.findAll(pageable);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<UserModel>> getUserById(@PathVariable UUID id) {
        Optional<UserModel> user = userRepository.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserModel> updateUser(@PathVariable UUID id, @RequestBody UserModel updatedUser) {
        Optional<UserModel> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            UserModel user = userOptional.get();

            if (updatedUser.getUsername() != null) {
                user.setUsername(updatedUser.getUsername());
            }
            if (updatedUser.getName() != null) {
                user.setName(updatedUser.getName());
            }
            if (updatedUser.getPassword() != null) {
                // Lembre-se de criptografar a senha novamente se necessário
                var passwordHashred = BCrypt.withDefaults()
                        .hashToString(12, updatedUser.getPassword().toCharArray());
                user.setPassword(passwordHashred);
            }
            if (updatedUser.getNumberCard() != null) {
                user.setNumberCard(updatedUser.getNumberCard());
            }
            if (updatedUser.getValidade() != null) {
                user.setValidade(updatedUser.getValidade());
            }

            UserModel updatedUserModel = userRepository.save(user);
            return ResponseEntity.ok(updatedUserModel);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        Optional<UserModel> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
