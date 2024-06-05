package dev.imrob.vendas.server.repository;

import dev.imrob.vendas.server.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findAllByOrderByDescricaoAsc();
}
