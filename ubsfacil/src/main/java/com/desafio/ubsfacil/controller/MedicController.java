package com.desafio.ubsfacil.controller;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.desafio.ubsfacil.models.MedicModel;
import com.desafio.ubsfacil.models.NurseModel;
import com.desafio.ubsfacil.models.UserModel;
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

    @PutMapping("/{id}")
    public ResponseEntity<MedicModel> updateMedic(@PathVariable UUID id, @RequestBody MedicModel updatedMedic) {
        Optional<MedicModel> medicOptional = medicRepository.findById(id);

        if (medicOptional.isPresent()) {
            MedicModel medic = medicOptional.get();

            if (updatedMedic.getUsername() != null) {
                medic.setUsername(updatedMedic.getUsername());
            }
            if (updatedMedic.getName() != null) {
                medic.setName(updatedMedic.getName());
            }
            if (updatedMedic.getPassword() != null) {
                // Lembre-se de criptografar a senha novamente se necessário
                var passwordHashred = BCrypt.withDefaults()
                        .hashToString(12, updatedMedic.getPassword().toCharArray());
                MedicModel updatedMedicModel = medicRepository.save(medic);
                medic.setPassword(passwordHashred);
            }
            if (updatedMedic.getNumberCard() != null) {
                medic.setNumberCard(updatedMedic.getNumberCard());
            }
            if (updatedMedic.getCrm() != null ) {
                medic.setCrm(updatedMedic.getCrm());
            }
            if (updatedMedic.getSpecialised() != null ) {
                medic.setSpecialised(updatedMedic.getSpecialised());
            }

            MedicModel updatedMedicModel = medicRepository.save(medic);

            return ResponseEntity.ok(updatedMedicModel);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedic(@PathVariable UUID id) {
        Optional<MedicModel> userOptional = medicRepository.findById(id);

        if (userOptional.isPresent()) {
            medicRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
