package com.desafio.ubsfacil.controller;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.desafio.ubsfacil.models.MedicModel;
import com.desafio.ubsfacil.models.NurseModel;
import com.desafio.ubsfacil.repository.MedicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/medic")
public class MedicController {
    @Autowired
    private MedicRepository medicRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody MedicModel medicModel) {
        var user = this.medicRepository.findByUsername(medicModel.getUsername());

        if (user != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Medico já existe");
        }

        var passwordHashred = BCrypt.withDefaults()
                .hashToString(12, medicModel.getPassword().toCharArray());

        medicModel.setPassword(passwordHashred);
        var userCreated = this.medicRepository.save(medicModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }

    @GetMapping("/")
    public ResponseEntity<Page<MedicModel>> getAllMedic(Pageable pageable) {
        Page<MedicModel> medic = medicRepository.findAll(pageable);
        return ResponseEntity.ok(medic);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Optional<MedicModel>> getMedicById(@PathVariable UUID id) {
        Optional<MedicModel> medic = medicRepository.findById(id);
        if (medic.isPresent()) {
            return ResponseEntity.ok(medic);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
