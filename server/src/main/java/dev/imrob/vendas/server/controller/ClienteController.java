package dev.imrob.vendas.server.controller;

import dev.imrob.vendas.server.dto.ClienteDTO;
import dev.imrob.vendas.server.service.ClienteService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController extends CrudController<ClienteDTO, ClienteService> {
}
