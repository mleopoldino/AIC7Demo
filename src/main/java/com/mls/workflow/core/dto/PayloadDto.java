package com.mls.workflow.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Data payload for the resource.")
public class PayloadDto {

    @NotBlank(message = "Name cannot be blank")
    @Schema(description = "Name of the user.", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("nome")
    private String nome;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Schema(description = "Email of the user.", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("email")
    private String email;

    @Min(value = 0, message = "Age cannot be negative")
    @Schema(description = "Age of the user.", example = "30")
    @JsonProperty("idade")
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
