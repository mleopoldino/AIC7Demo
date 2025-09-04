package com.mls.workflow.core.service;

import com.mls.workflow.core.dto.CadastroDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.validation.ConstraintViolationException;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class CadastroServiceTest {

    @Autowired
    private CadastroService cadastroService;

    @Test
    void testCreate_ValidCadastro_ShouldReturnCreatedCadastro() {
        CadastroDto newCadastro = new CadastroDto(null, "Teste Novo", "teste@example.com", 28);
        
        CadastroDto result = cadastroService.create(newCadastro);
        
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getNome()).isEqualTo("Teste Novo");
        assertThat(result.getEmail()).isEqualTo("teste@example.com");
        assertThat(result.getIdade()).isEqualTo(28);
    }

    @Test
    void testRead_ExistingId_ShouldReturnCadastro() {
        Optional<CadastroDto> result = cadastroService.read(1L);
        
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getNome()).isEqualTo("João Silva");
        assertThat(result.get().getEmail()).isEqualTo("joao@example.com");
        assertThat(result.get().getIdade()).isEqualTo(30);
    }

    @Test
    void testRead_NonExistingId_ShouldReturnEmpty() {
        Optional<CadastroDto> result = cadastroService.read(999L);
        
        assertThat(result).isEmpty();
    }

    @Test
    void testUpdate_ExistingId_ShouldUpdateAndReturnCadastro() {
        CadastroDto updatedCadastro = new CadastroDto(null, "João Silva Updated", "joao.updated@example.com", 31);
        
        CadastroDto result = cadastroService.update(1L, updatedCadastro);
        
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNome()).isEqualTo("João Silva Updated");
        assertThat(result.getEmail()).isEqualTo("joao.updated@example.com");
        assertThat(result.getIdade()).isEqualTo(31);
        
        // Verify the update persisted
        Optional<CadastroDto> readResult = cadastroService.read(1L);
        assertThat(readResult).isPresent();
        assertThat(readResult.get().getNome()).isEqualTo("João Silva Updated");
    }

    @Test
    void testUpdate_NonExistingId_ShouldReturnNull() {
        CadastroDto updatedCadastro = new CadastroDto(null, "Non Existing", "nonexisting@example.com", 25);
        
        CadastroDto result = cadastroService.update(999L, updatedCadastro);
        
        assertThat(result).isNull();
    }

    @Test
    void testDelete_ExistingId_ShouldReturnTrue() {
        boolean result = cadastroService.delete(2L);
        
        assertThat(result).isTrue();
        
        // Verify the record was deleted
        Optional<CadastroDto> readResult = cadastroService.read(2L);
        assertThat(readResult).isEmpty();
    }

    @Test
    void testDelete_NonExistingId_ShouldReturnFalse() {
        boolean result = cadastroService.delete(999L);
        
        assertThat(result).isFalse();
    }

    @Test
    void testCreateAndRead_CompleteCycle_ShouldWork() {
        // Create
        CadastroDto newCadastro = new CadastroDto(null, "Ciclo Completo", "ciclo@example.com", 40);
        CadastroDto created = cadastroService.create(newCadastro);
        
        assertThat(created.getId()).isNotNull();
        
        // Read
        Optional<CadastroDto> read = cadastroService.read(created.getId());
        assertThat(read).isPresent();
        assertThat(read.get().getNome()).isEqualTo("Ciclo Completo");
        
        // Update
        CadastroDto updated = cadastroService.update(created.getId(), 
            new CadastroDto(null, "Ciclo Atualizado", "ciclo.updated@example.com", 41));
        assertThat(updated.getNome()).isEqualTo("Ciclo Atualizado");
        
        // Delete
        boolean deleted = cadastroService.delete(created.getId());
        assertThat(deleted).isTrue();
        
        // Verify deletion
        Optional<CadastroDto> afterDelete = cadastroService.read(created.getId());
        assertThat(afterDelete).isEmpty();
    }

    @Test
    void testCreate_InvalidCadastro_ShouldThrowConstraintViolationException() {
        // Test with blank name
        CadastroDto invalidCadastro = new CadastroDto(null, "", "valid@example.com", 25);
        
        assertThrows(ConstraintViolationException.class, () -> {
            cadastroService.create(invalidCadastro);
        });
    }

    @Test
    void testCreate_InvalidEmail_ShouldThrowConstraintViolationException() {
        // Test with invalid email
        CadastroDto invalidCadastro = new CadastroDto(null, "Valid Name", "invalid-email", 25);
        
        assertThrows(ConstraintViolationException.class, () -> {
            cadastroService.create(invalidCadastro);
        });
    }

    @Test
    void testCreate_NegativeAge_ShouldThrowConstraintViolationException() {
        // Test with negative age
        CadastroDto invalidCadastro = new CadastroDto(null, "Valid Name", "valid@example.com", -5);
        
        assertThrows(ConstraintViolationException.class, () -> {
            cadastroService.create(invalidCadastro);
        });
    }

    @Test
    void testCreate_NullCadastro_ShouldThrowConstraintViolationException() {
        // Test with null DTO
        assertThrows(ConstraintViolationException.class, () -> {
            cadastroService.create(null);
        });
    }

    @Test
    void testUpdate_InvalidCadastro_ShouldThrowConstraintViolationException() {
        // Test update with invalid data
        CadastroDto invalidCadastro = new CadastroDto(null, "", "invalid-email", -1);
        
        assertThrows(ConstraintViolationException.class, () -> {
            cadastroService.update(1L, invalidCadastro);
        });
    }

    @Test
    void testUpdate_NullId_ShouldThrowConstraintViolationException() {
        CadastroDto validCadastro = new CadastroDto(null, "Valid Name", "valid@example.com", 25);
        
        assertThrows(ConstraintViolationException.class, () -> {
            cadastroService.update(null, validCadastro);
        });
    }
}