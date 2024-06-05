package dev.imrob.vendas.server.controller.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidacaoError {
    private Map<String, String> errors = new HashMap<>();

    public ValidacaoError() {
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void addError(String field, String message) {
        errors.put(field, message);
    }
}
