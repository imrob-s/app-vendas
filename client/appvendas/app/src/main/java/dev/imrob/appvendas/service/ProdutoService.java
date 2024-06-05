/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dev.imrob.appvendas.service;

import dev.imrob.appvendas.request.ProdutoRequest;
import dev.imrob.appvendas.config.FeignConfig;
import dev.imrob.appvendas.entity.Produto;
import java.util.List;

/**
 *
 * @author Rob
 */
public class ProdutoService {
    
    private ProdutoRequest request;

    public ProdutoService() {
        this.request = FeignConfig.iniciar(ProdutoRequest.class);
    }
    
    public Produto findById(Long id) {
        return request.findById(id);
    }

    public List<Produto> findAll() {
        return request.findAll();
    }

    public Long save(Produto produto) {
        return request.save(produto);
    }

    public String update(Produto produto) {
        return request.update(produto);
    }

    public String delete(Long id) {
        return request.delete(id);
    }
}
