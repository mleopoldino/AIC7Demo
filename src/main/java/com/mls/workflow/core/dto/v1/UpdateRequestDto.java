package com.mls.workflow.core.dto.v1;

import com.mls.workflow.core.validation.v1.AtLeastOneField;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@AtLeastOneField(fields = {"nome", "email", "idade"}, message = "Pelo menos um campo deve ser fornecido para atualização.")
public class UpdateRequestDto {

    private String nome;

    @Email(message = "O e-mail deve ser válido.")
    private String email;

    @Min(value = 0, message = "A idade não pode ser negativa.")
    private Integer idade;
}