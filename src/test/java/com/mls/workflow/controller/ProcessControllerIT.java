package com.mls.workflow.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProcessControllerIT {

    @Autowired
    MockMvc mvc;

    @Test
    void postCreate_shouldReturn202_whenValid() throws Exception {
        String json = """
{"tarefa":"CREATE","payload":{"nome":"Novo Registro","email":"novo@example.com","idade":30}}
""";
        mvc.perform(post("/api/cadastro/process")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isAccepted())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void postCreate_shouldReturn400_whenTarefaMissing() throws Exception {
        String json = """
{"payload":{"nome":"N","email":"n@example.com","idade":1}}
""";
        mvc.perform(post("/api/cadastro/process")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation error"))
                .andExpect(jsonPath("$.errors.tarefa").exists());
    }

    @Test
    void postCreate_shouldReturn400_whenTarefaInvalid() throws Exception {
        String json = """
{"tarefa":"UPSERT","payload":{"nome":"N","email":"n@example.com","idade":1}}
""";
        mvc.perform(post("/api/cadastro/process")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.tarefa")
                        .value("tarefa must be one of CREATE, READ, UPDATE, DELETE"));
    }
}
