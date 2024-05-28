package dev.imrob.vendas.server.controller;

import dev.imrob.vendas.server.dto.PedidoDTO;
import dev.imrob.vendas.server.service.PedidoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController extends CrudController<PedidoDTO, PedidoService> {
}
