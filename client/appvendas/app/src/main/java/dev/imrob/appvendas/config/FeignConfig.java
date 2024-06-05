
package dev.imrob.appvendas.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import java.util.List;

/**
 * Classe de configuração para a biblioteca Feign.
 *
 * <p>Esta classe fornece métodos para configurar e inicializar 
 * clientes Feign, além de um método utilitário para extrair 
 * mensagens de erro de respostas JSON.</p>
 * 
 * @author Rob
 */
public class FeignConfig {
    private static final String URL_API_APPVENDAS = "http://localhost:8080/api/v1";
    
    /**
     * Inicializa um cliente Feign para uma interface de API.
     *
     * <p>Este método configura um cliente Feign com codificador e decodificador Jackson,
     * incluindo suporte para tipos Java 8 Time (JavaTimeModule). O cliente é configurado
     * para se comunicar com a URL base da API definida em URL_API_APPVENDAS.</p>
     *
     * @param clazz A interface da API para a qual o cliente Feign deve ser criado.
     * @param <TYPE> O tipo da interface da API.
     * @return Um cliente Feign configurado para a interface da API especificada.
     */
    public static <TYPE> TYPE iniciar(Class<TYPE> clazz) {
        return Feign.builder()
                .decoder(new JacksonDecoder(List.of(new JavaTimeModule())))
                .encoder(new JacksonEncoder(List.of(new JavaTimeModule())))
                .target(clazz, URL_API_APPVENDAS);
    }
    
    /**
     * Extrai a mensagem de erro de uma resposta JSON de erro.
     *
     * <p>Este método analisa uma string JSON contendo uma resposta de erro 
     * e extrai as informações relevantes da mensagem de erro. Ele lida com 
     * diferentes formatos de erro, incluindo erros de validação 
     * ("campo-invalido") e erros gerais ("detail").</p>
     *
     * @param jsonErro A string JSON contendo a resposta de erro.
     * @return Uma string formatada contendo a mensagem de erro extraída.
     */
    public static String extrairMensagemDeErro(String jsonErro) {
    try {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonErro);

        StringBuilder mensagem = new StringBuilder();

        String titulo = rootNode.path("title").asText();
        mensagem.append(titulo).append("\n\n");
        if (rootNode.has("campo-invalido")) {
            JsonNode campoInvalidoNode = rootNode.path("campo-invalido");
            campoInvalidoNode.fieldNames().forEachRemaining(campo -> {
                String erroCampo = campoInvalidoNode.path(campo).asText();
                mensagem.append(erroCampo).append("\n");
            });
        } else {
            String detail = rootNode.path("detail").asText();
            mensagem.append(detail);
        }

        return mensagem.toString();

    } catch (JsonProcessingException e) {
        e.printStackTrace();
        return "Erro desconhecido ao processar a requisição.";
    }
}
}
