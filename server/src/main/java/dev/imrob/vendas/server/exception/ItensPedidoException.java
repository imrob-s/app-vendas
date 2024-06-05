package dev.imrob.vendas.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class ItensPedidoException extends AppVendasException {
    private final String message;
    public ItensPedidoException(String message) {
        this.message = message;
    }

    @Override
    public ProblemDetail toProblemDetail() {
        var pd = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        pd.setTitle("Itens do pedido inválidos.");
        pd.setDetail(message);

        return pd;
    }
}
