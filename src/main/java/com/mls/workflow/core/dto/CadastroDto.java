package com.mls.workflow.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer Object for Cadastro (Registration) entity.")
public class CadastroDto {

    @Schema(description = "Unique identifier of the registration.", example = "1")
    private Long id;

    @Schema(description = "Name of the person.", example = "Jane Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;

    @Schema(description = "Email of the person.", example = "jane.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

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
