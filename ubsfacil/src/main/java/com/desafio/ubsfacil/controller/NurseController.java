package com.desafio.ubsfacil.controller;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.desafio.ubsfacil.models.NurseModel;
import com.desafio.ubsfacil.models.UserModel;
import com.desafio.ubsfacil.repository.NurseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/nurse")
public class NurseController {
    @Autowired
    private NurseRepository nurseRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody NurseModel nurseModel) {
        var user = this.nurseRepository.findByUsername(nurseModel.getUsername());

        if (user != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Enfermeiro já existe");
        }

        var passwordHashred = BCrypt.withDefaults()
                .hashToString(12, nurseModel.getPassword().toCharArray());

        nurseModel.setPassword(passwordHashred);
        var userCreated = this.nurseRepository.save(nurseModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }

    @GetMapping("/")
    public ResponseEntity<Page<NurseModel>> getAllNurse(Pageable pageable) {
        Page<NurseModel> nurse = nurseRepository.findAll(pageable);
        return ResponseEntity.ok(nurse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<NurseModel>> getNurseById(@PathVariable UUID id) {
        Optional<NurseModel> nurse = nurseRepository.findById(id);
        if (nurse.isPresent()) {
            return ResponseEntity.ok(nurse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<NurseModel> updateNurse(@PathVariable UUID id, @RequestBody NurseModel updatedNurse) {
        Optional<NurseModel> nurseOptional = nurseRepository.findById(id);

        if (nurseOptional.isPresent()) {
            NurseModel nurse = nurseOptional.get();

            if (updatedNurse.getUsername() != null) {
                nurse.setUsername(updatedNurse.getUsername());
            }
            if (updatedNurse.getName() != null) {
                nurse.setName(updatedNurse.getName());
            }
            if (updatedNurse.getPassword() != null) {
                // Lembre-se de criptografar a senha novamente se necessário
                var passwordHashred = BCrypt.withDefaults()
                        .hashToString(12, updatedNurse.getPassword().toCharArray());
                nurse.setPassword(passwordHashred);
            }
            if (updatedNurse.getNumberCard() != null) {
                nurse.setNumberCard(updatedNurse.getNumberCard());
            }
            NurseModel updatedNurseModel = nurseRepository.save(nurse);
            return ResponseEntity.ok(updatedNurseModel);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNurse(@PathVariable UUID id) {
        Optional<NurseModel> userOptional = nurseRepository.findById(id);

        if (userOptional.isPresent()) {
            nurseRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
