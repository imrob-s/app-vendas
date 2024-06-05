package dev.imrob.vendas.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class MetodoNaoPermitidoException extends AppVendasException {
    private final String message;
    public MetodoNaoPermitidoException(String message) {
        this.message = message;
    }

    @Override
    public ProblemDetail toProblemDetail() {
        var pd = ProblemDetail.forStatus(HttpStatus.METHOD_NOT_ALLOWED);
        pd.setTitle("Método não permitido");
        pd.setDetail(message);
        return pd;
    }
}
