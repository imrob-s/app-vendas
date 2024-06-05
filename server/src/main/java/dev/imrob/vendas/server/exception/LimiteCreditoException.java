package dev.imrob.vendas.server.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;

@Getter
public class LimiteCreditoException extends AppVendasException {
    private final String message;
    public LimiteCreditoException(String message) {
        this.message = message;
    }

    @Override
    public ProblemDetail toProblemDetail() {
        var pd = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        pd.setType(URI.create("https://github.com/imrob-s/app-vendas/issues"));
        pd.setTitle("Limite de crédito excedido.");
        pd.setDetail(message);

        return pd;
    }
}
