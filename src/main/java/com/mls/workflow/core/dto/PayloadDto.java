package com.mls.workflow.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data payload for the resource.")
public class PayloadDto {

    @Schema(description = "Name of the user.", example = "John Doe")
    private String nome;

    @Schema(description = "Email of the user.", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Age of the user.", example = "30")
    private int idade;

    public PayloadDto() {
    }

    public PayloadDto(String nome, String email, int idade) {
        this.nome = nome;
        this.email = email;
        this.idade = idade;
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