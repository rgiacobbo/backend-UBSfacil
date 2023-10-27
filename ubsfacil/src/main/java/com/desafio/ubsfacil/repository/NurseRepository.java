package com.desafio.ubsfacil.repository;

import com.desafio.ubsfacil.models.NurseModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NurseRepository extends JpaRepository<NurseModel, UUID> {
    NurseModel findByUsername(String username);
}
