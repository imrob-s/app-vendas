package dev.imrob.vendas.server.controller;

import dev.imrob.vendas.server.service.CrudService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class CrudController<TYPE, SERVICE extends CrudService<TYPE>> {
    @Autowired
    private SERVICE service;

    @GetMapping(value = "/{id}")
    public ResponseEntity<TYPE> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
    @GetMapping
    public ResponseEntity<List<TYPE>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
    @PostMapping
    public ResponseEntity<Long> save(@Valid @RequestBody TYPE dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @PatchMapping
    public ResponseEntity<String> update(@Valid @RequestBody TYPE dto) {
        service.update(dto);
        return ResponseEntity.ok("Atualizado com sucesso!");
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.ok("Deletado com sucesso!");
    }
}
