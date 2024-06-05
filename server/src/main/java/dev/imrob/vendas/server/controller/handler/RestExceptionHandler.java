package dev.imrob.vendas.server.controller.handler;

import dev.imrob.vendas.server.exception.AppVendasException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(AppVendasException.class)
    public ProblemDetail handleAppVendasException(AppVendasException e) {
        return e.toProblemDetail();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ValidacaoError error = new ValidacaoError();
        var pd = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        pd.setType(URI.create("https://github.com/imrob-s/app-vendas/issues"));
        pd.setTitle("Erro de validação dos campos");

        for (FieldError f : ex.getBindingResult().getFieldErrors()){
            error.addError(f.getField(), f.getDefaultMessage());
        }

        log.error("Erro de validação: " + ex.getMessage());
        pd.setProperty("campo-invalido", error.getErrors());
        return pd;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        var pd = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        pd.setType(URI.create("https://github.com/imrob-s/app-vendas/issues"));
        pd.setTitle("Parâmetro não encontrado");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        var pd = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        pd.setType(URI.create("https://github.com/imrob-s/app-vendas/issues"));
        pd.setTitle("Tipo de parâmetro inválido");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException ex) {
        ValidacaoError error = new ValidacaoError();
        var pd = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        pd.setType(URI.create("https://github.com/imrob-s/app-vendas/issues"));
        pd.setTitle("Erro de validação dos campos");
        for (ConstraintViolation<?> f : ex.getConstraintViolations()){
            error.addError(f.getPropertyPath().toString(), f.getMessage());
        }

        log.error("Erro de validação: " + ex.getMessage());
        pd.setProperty("campo-invalido", error.getErrors());
        return pd;
    }


    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFoundException(EntityNotFoundException ex) {
        var pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setType(URI.create("https://github.com/imrob-s/app-vendas/issues"));
        pd.setTitle("Entidade não encontrada");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        var pd = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        pd.setType(URI.create("https://github.com/imrob-s/app-vendas/issues"));
        pd.setTitle("Erro de validação no banco de dados");
        pd.setDetail(ex.getMessage());
        return pd;
    }

}
