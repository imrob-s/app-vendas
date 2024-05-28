package dev.imrob.vendas.server.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

/**
 * Interface que define as operações CRUD (Create, Read, Update, Delete) para entidades.
 *
 * @param <TYPE> O tipo de entidade gerenciada por este serviço.
 */
public interface CrudService<TYPE> {
    /**
     * Encontra uma entidade pelo seu identificador único.
     *
     * @param id O identificador único da entidade a ser recuperada.
     * @return A entidade correspondente ao identificador especificado.
     * @throws EntityNotFoundException se a entidade não for encontrada.
     */
    TYPE findById(Long id);

    /**
     * Recupera todas as entidades.
     *
     * @return Uma lista contendo todas as entidades encontradas.
     */
    List<TYPE> findAll();

    /**
     * Salva uma nova entidade.
     *
     * @param dto A entidade a ser salva.
     * @return O identificador da entidade salva.
     */
    Long save(TYPE dto);

    /**
     * Atualiza uma entidade existente.
     *
     * @param dto A entidade a ser atualizada.
     */
    void update(TYPE dto);

    /**
     * Exclui uma entidade pelo seu identificador único.
     *
     * @param id O identificador único da entidade a ser excluída.
     */
    void delete(Long id);

    /**
     * Obtém o repositório JPA usado para executar operações no banco de dados.
     *
     * @return O repositório JPA usado pelo serviço.
     */
    JpaRepository<?, Long> getRepository();

    /**
     * Verifica se uma entidade existe, lançando uma exceção se não existir.
     *
     * @param id O identificador da entidade.
     * @param entityClass A classe da entidade.
     * @param <E> O tipo da entidade.
     * @return A entidade correspondente ao identificador especificado.
     * @throws EntityNotFoundException se a entidade não for encontrada.
     */
    @SuppressWarnings("unchecked")
    default <E> E idExisteOuException(Long id, Class<E> entityClass) {
        var repository = getRepository();
        String entityName = entityClass.getSimpleName();

        return (E) repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("%s com id %d não foi encontrado".formatted(entityName, id))
        );
    }

    /**
     * Obtém o validador para validar entidades.
     *
     * @return O validador para validar entidades.
     */
    private Validator getValidator(){
        return Validation.buildDefaultValidatorFactory().getValidator();
    }

    /**
     * Valida os campos de uma entidade.
     *
     * @param dto A entidade a ser validada.
     * @throws ConstraintViolationException se houver violações de validação.
     */
    default void validarCampos(TYPE dto) {
        Set<ConstraintViolation<TYPE>> violations = getValidator().validate(dto);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<TYPE> constraintViolation : violations) {
                sb.append("[").append(constraintViolation.getPropertyPath().toString())
                .append(" : ").append(constraintViolation.getMessage()).append("] ");
            }
            throw new ConstraintViolationException("Erro de validação: " + sb, violations);
        }
    }
}
