package dev.imrob.vendas.server.repository;

import dev.imrob.vendas.server.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
