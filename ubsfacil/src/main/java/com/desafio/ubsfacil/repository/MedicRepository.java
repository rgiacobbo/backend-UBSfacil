package com.desafio.ubsfacil.repository;

import com.desafio.ubsfacil.models.MedicModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MedicRepository extends JpaRepository<MedicModel, UUID> {
    MedicModel findByUsername(String username);
}
