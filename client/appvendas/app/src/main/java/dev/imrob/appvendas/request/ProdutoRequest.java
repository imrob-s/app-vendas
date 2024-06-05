
package dev.imrob.appvendas.request;

import dev.imrob.appvendas.entity.Produto;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.List;

/**
 *
 * @author Rob
 */
public interface ProdutoRequest {
    @RequestLine("GET /produtos/{id}")
    @Headers("Content-Type: application/json")
    Produto findById(@Param("id") Long id);

    @RequestLine("GET /produtos")
    @Headers("Content-Type: application/json")
    List<Produto> findAll();

    @RequestLine("POST /produtos")
    @Headers("Content-Type: application/json")
    Long save(Produto produto);

    @RequestLine("PUT /produtos")
    @Headers("Content-Type: application/json")
    String update(Produto produto);

    @RequestLine("DELETE /produtos/{id}")
    @Headers("Content-Type: application/json")
    String delete(@Param("id") Long id);
    
}
