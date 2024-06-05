/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dev.imrob.appvendas.request;

import dev.imrob.appvendas.entity.Cliente;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.List;

/**
 *
 * @author Rob
 */
public interface ClienteRequest {
    @RequestLine("GET /clientes/{id}")
    @Headers("Content-Type: application/json")
    Cliente findById(@Param("id") Long id);

    @RequestLine("GET /clientes")
    @Headers("Content-Type: application/json")
    List<Cliente> findAll();

    @RequestLine("POST /clientes")
    @Headers("Content-Type: application/json")
    Long save(Cliente cliente);

    @RequestLine("PUT /clientes")
    @Headers("Content-Type: application/json")
    String update(Cliente cliente);

    @RequestLine("DELETE /clientes/{id}")
    @Headers("Content-Type: application/json")
    String delete(@Param("id") Long id);
}
