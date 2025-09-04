package com.mls.workflow.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

@Schema(description = "Data Transfer Object for Cadastro (Registration) entity.")
public class CadastroDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Unique identifier of the registration.", example = "1")
    private Long id;

    @NotBlank(message = "Nome cannot be blank")
    @Schema(description = "Name of the person.", example = "Jane Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Schema(description = "Email of the person.", example = "jane.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Min(value = 0, message = "Age cannot be negative")
    @Schema(description = "Age of the person.", example = "25")
    private int idade;

    public CadastroDto() {
    }

    public CadastroDto(Long id, String nome, String email, int idade) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.idade = idade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }
}
