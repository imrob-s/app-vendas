package dev.imrob.vendas.server.controller;

import dev.imrob.vendas.server.dto.ProdutoDTO;
import dev.imrob.vendas.server.entity.Produto;
import dev.imrob.vendas.server.service.ProdutoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/produtos")
public class ProdutoController extends CrudController<ProdutoDTO, ProdutoService> {
}
