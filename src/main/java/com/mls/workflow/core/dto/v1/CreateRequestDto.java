package com.mls.workflow.core.dto.v1;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class CreateRequestDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "O e-mail deve ser válido.")
    private String email;

    @NotNull(message = "A idade é obrigatória.")
    @Min(value = 0, message = "A idade não pode ser negativa.")
    private Integer idade;
}