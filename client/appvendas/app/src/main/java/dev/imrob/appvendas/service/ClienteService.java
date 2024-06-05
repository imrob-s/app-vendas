
package dev.imrob.appvendas.service;

import dev.imrob.appvendas.request.ClienteRequest;
import dev.imrob.appvendas.config.FeignConfig;
import dev.imrob.appvendas.entity.Cliente;
import java.util.List;

/**
 *
 * @author Rob
 */
public class ClienteService {
    private ClienteRequest request;

    public ClienteService() {
        this.request = FeignConfig.iniciar(ClienteRequest.class);
    }
    
    public Cliente findById(Long id) {
        return request.findById(id);
    }

    public List<Cliente> findAll() {
        return request.findAll();
    }

    public Long save(Cliente cliente) {
        return request.save(cliente);
    }

    public String update(Cliente cliente) {
        return request.update(cliente);
    }

    public String delete(Long id) {
        return request.delete(id);
    }
}
