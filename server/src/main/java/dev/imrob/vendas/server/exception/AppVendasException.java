package dev.imrob.vendas.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;

public class AppVendasException extends RuntimeException {
    public ProblemDetail toProblemDetail() {
        var pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setType(URI.create("https://github.com/imrob-s/app-vendas/issues"));
        pd.setTitle("App Vendas Internal Server Error");
        return pd;
    }
}
