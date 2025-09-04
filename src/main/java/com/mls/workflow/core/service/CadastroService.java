package com.mls.workflow.core.service;

import com.mls.workflow.core.dto.CadastroDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class CadastroService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CadastroService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<CadastroDto> rowMapper = new RowMapper<CadastroDto>() {
        @Override
        public CadastroDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CadastroDto(
                    rs.getLong("ID"),
                    rs.getString("NOME"),
                    rs.getString("EMAIL"),
                    rs.getInt("IDADE")
            );
        }
    };

    public CadastroDto create(@Valid @NotNull CadastroDto cadastroDto) {
        String sql = "INSERT INTO AIC_CADASTRO (NOME, EMAIL, IDADE) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, cadastroDto.getNome());
            ps.setString(2, cadastroDto.getEmail());
            ps.setInt(3, cadastroDto.getIdade());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            cadastroDto.setId(keyHolder.getKey().longValue());
        }
        return cadastroDto;
    }

    public Optional<CadastroDto> read(Long id) {
        String sql = "SELECT ID, NOME, EMAIL, IDADE FROM AIC_CADASTRO WHERE ID = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public CadastroDto update(@NotNull Long id, @Valid @NotNull CadastroDto cadastroDto) {
        String sql = "UPDATE AIC_CADASTRO SET NOME = ?, EMAIL = ?, IDADE = ? WHERE ID = ?";
        int affectedRows = jdbcTemplate.update(sql, cadastroDto.getNome(), cadastroDto.getEmail(), cadastroDto.getIdade(), id);
        if (affectedRows == 0) {
            // If no rows were affected, it means the record with the given ID was not found.
            // In a real application, you might throw a specific exception here.
            return null; // Or throw an exception indicating not found
        }
        cadastroDto.setId(id); // Ensure the ID is set in the returned DTO
        return cadastroDto;
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM AIC_CADASTRO WHERE ID = ?";
        int affectedRows = jdbcTemplate.update(sql, id);
        return affectedRows > 0;
    }
}
