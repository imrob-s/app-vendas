package dev.imrob.vendas.server.controller;

import dev.imrob.vendas.server.service.CrudService;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Classe abstrata que fornece operações CRUD básicas para entidades.
 *
 * @param <TYPE>   O tipo da entidade gerenciada por este controlador.
 * @param <SERVICE> O tipo do serviço que implementa as operações CRUD para a entidade.
 */
@Getter
public abstract class CrudController<TYPE, SERVICE extends CrudService<TYPE>> {
    @Autowired
    private SERVICE service;

    /**
     * Recupera uma entidade pelo seu ID.
     *
     * @param id O ID da entidade a ser recuperada.
     * @return ResponseEntity contendo a entidade encontrada ou um erro 404 se não for encontrada.
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<TYPE> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    /**
     * Recupera todas as entidades.
     *
     * @return ResponseEntity contendo uma lista de todas as entidades.
     */
    @GetMapping
    public ResponseEntity<List<TYPE>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    /**
     * Salva uma nova entidade.
     *
     * @param dto O DTO da entidade a ser salva.
     * @return ResponseEntity contendo o ID da entidade salva.
     */
    @PostMapping
    public ResponseEntity<Long> save(@Valid @RequestBody TYPE dto) {
        Long id = service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    /**
     * Atualiza uma entidade existente.
     *
     * @param dto O DTO da entidade a ser atualizada.
     * @return ResponseEntity com uma mensagem de sucesso ou códigos de erro apropriados.
     */
    @PutMapping
    public ResponseEntity<Void> update(@Valid @RequestBody TYPE dto) {
        service.update(dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * Exclui uma entidade pelo seu ID.
     *
     * @param id O ID da entidade a ser excluída.
     * @return ResponseEntity com uma mensagem de sucesso ou códigos de erro apropriados.
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
