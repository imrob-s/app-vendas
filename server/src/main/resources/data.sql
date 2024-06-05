-- Inserção de Clientes
INSERT INTO tb_cliente (dia_fechamento_fatura, limite_compra, nome) VALUES
(15, 2000.00, 'Ana Silva'),
(10, 1250.00, 'Bruno Pereira'),
(20, 4500.00, 'Carlos Oliveira'),
(5, 6500.00, 'Daniela Souza'),
(25, 1500.00, 'Eduardo Santos'),
(30, 3500.00, 'Fernanda Costa'),
(18, 6000.00, 'Gabriel Lima'),
(12, 1700.00, 'Helena Ribeiro'),
(22, 5200.00, 'Isabel Martins'),
(8, 9999.00, 'João Alves'),
(5, 10.00, 'Robson');

-- Inserção de Produtos
INSERT INTO tb_produto (descricao, preco) VALUES
('Arroz 5kg', 20.00),
('Feijão 1kg', 8.00),
('Macarrão 500g', 4.50),
('Óleo de Soja 900ml', 7.00),
('Açúcar 1kg', 3.50),
('Sal 1kg', 2.00),
('Café 500g', 12.00),
('Leite 1L', 4.00),
('Biscoito 200g', 3.00),
('Farinha de Trigo 1kg', 5.50),
('Detergente 500ml', 2.50),
('Sabão em Pó 1kg', 10.00),
('Amaciante 2L', 8.50),
('Papel Higiênico 4 unidades', 6.00),
('Creme Dental 90g', 3.50),
('Shampoo 300ml', 9.00),
('Condicionador 300ml', 9.00),
('Sabonete 90g', 1.50),
('Desodorante 150ml', 12.00),
('Escova de Dentes', 5.00);

-- Inserção de Pedidos e Itens de Pedido
-- Cliente 1 - Ana Silva (3 pedidos)
INSERT INTO tb_pedido (data, status, cliente_id, valor_total) VALUES
('2023-05-01', 'ATIVO', 1, 58.00);  -- Pedido 1: 2*20 + 1*4.5 + 4*3.5 = 58.00

INSERT INTO tb_item_pedido (quantidade, pedido_id, produto_id) VALUES
(2, 1, 1), (1, 1, 3), (4, 1, 5);

INSERT INTO tb_pedido (data, status, cliente_id, valor_total) VALUES
('2023-05-05', 'ATIVO', 1, 17.00);  -- Pedido 2: 1*8 + 2*2 + 1*3 = 17.00

INSERT INTO tb_item_pedido (quantidade, pedido_id, produto_id) VALUES
(1, 2, 2), (2, 2, 6), (1, 2, 9);

INSERT INTO tb_pedido (data, status, cliente_id, valor_total) VALUES
('2023-05-10', 'ATIVO', 1, 53.00);  -- Pedido 3: 3*12 + 2*4 + 1*5.5 = 53.00

INSERT INTO tb_item_pedido (quantidade, pedido_id, produto_id) VALUES
(3, 3, 7), (2, 3, 8), (1, 3, 10);

-- Cliente 2 - Bruno Pereira (1 pedido)
INSERT INTO tb_pedido (data, status, cliente_id, valor_total) VALUES
('2023-05-02', 'ATIVO', 2, 55.00);  -- Pedido 4: 3*8 + 1*7 + 2*4 + 5*5.5 = 55.00

INSERT INTO tb_item_pedido (quantidade, pedido_id, produto_id) VALUES
(3, 4, 2), (1, 4, 4), (2, 4, 8), (5, 4, 10);

-- Cliente 3 - Carlos Oliveira (2 pedidos)
INSERT INTO tb_pedido (data, status, cliente_id, valor_total) VALUES
('2023-05-03', 'ATIVO', 3, 65.00);  -- Pedido 5: 2*20 + 1*3.5 + 3*12 = 65.00

INSERT INTO tb_item_pedido (quantidade, pedido_id, produto_id) VALUES
(2, 5, 1), (1, 5, 5), (3, 5, 7);

INSERT INTO tb_pedido (data, status, cliente_id, valor_total) VALUES
('2023-05-08', 'ATIVO', 3, 37.00);  -- Pedido 6: 1*7 + 4*2 + 2*3 = 37.00

INSERT INTO tb_item_pedido (quantidade, pedido_id, produto_id) VALUES
(1, 6, 4), (4, 6, 6), (2, 6, 9);

-- Cliente 4 - Daniela Souza (3 pedidos)
INSERT INTO tb_pedido (data, status, cliente_id, valor_total) VALUES
('2023-05-04', 'ATIVO', 4, 12.50);  -- Pedido 7: 1*4.5 + 2*2 = 12.50

INSERT INTO tb_item_pedido (quantidade, pedido_id, produto_id) VALUES
(1, 7, 3), (2, 7, 6);

INSERT INTO tb_pedido (data, status, cliente_id, valor_total) VALUES
('2023-05-12', 'ATIVO', 4, 30.50);  -- Pedido 8: 2*8 + 3*3.5 + 1*4 = 30.50

INSERT INTO tb_item_pedido (quantidade, pedido_id, produto_id) VALUES
(2, 8, 2), (3, 8, 5), (1, 8, 8);

INSERT INTO tb_pedido (data, status, cliente_id, valor_total) VALUES
('2023-05-18', 'ATIVO', 4, 112.00);  -- Pedido 9: 4*20 + 1*7 + 3*12 = 112.00

INSERT INTO tb_item_pedido (quantidade, pedido_id, produto_id) VALUES
(4, 9, 1), (1, 9, 4), (3, 9, 7);

-- Cliente 5 - Eduardo Santos (4 pedidos)
INSERT INTO tb_pedido (data, status, cliente_id, valor_total) VALUES
('2023-05-05', 'ATIVO', 5, 40.00);  -- Pedido 10: 2*7 + 1*12 = 40.00

INSERT INTO tb_item_pedido (quantidade, pedido_id, produto_id) VALUES
(2, 10, 4), (1, 10, 7);

INSERT INTO tb_pedido (data, status, cliente_id, valor_total) VALUES
('2023-05-09', 'ATIVO', 5, 25.00);  -- Pedido 11: 3*4.5 + 2*3 + 1*5.5 = 25.00

INSERT INTO tb_item_pedido (quantidade, pedido_id, produto_id) VALUES
(3, 11, 3), (2, 11, 9), (1, 11, 10);

INSERT INTO tb_pedido (data, status, cliente_id, valor_total) VALUES
('2023-05-14', 'ATIVO', 5, 45.00);  -- Pedido 12: 1*20 + 4*2 + 2*4 = 45.00

INSERT INTO tb_item_pedido (quantidade, pedido_id, produto_id) VALUES
(1, 12, 1), (4, 12, 6), (2, 12, 8);

INSERT INTO tb_pedido (data, status, cliente_id, valor_total) VALUES
('2023-05-20', 'ATIVO', 5, 70.50);  -- Pedido 13: 5*8 + 1*3.5 + 3*5.5 = 70.50

INSERT INTO tb_item_pedido (quantidade, pedido_id, produto_id) VALUES
(5, 13, 2), (1, 13, 5), (3, 13, 10);

-- Cliente 6 - Fernanda Costa (2 pedidos)
INSERT INTO tb_pedido (data, status, cliente_id, valor_total) VALUES
('2023-05-06', 'ATIVO', 6, 33.00);  -- Pedido 14: 1*7 + 2*12 + 1*3 = 33.00

INSERT INTO tb_item_pedido (quantidade, pedido_id, produto_id) VALUES
(1, 14, 4), (2, 14, 7), (1, 14, 9);

INSERT INTO tb_pedido (data, status, cliente_id, valor_total) VALUES
('2023-05-11', 'ATIVO', 6, 40.00);  -- Pedido 15: 3*8 + 2*2 + 4*4 = 40.00

INSERT INTO tb_item_pedido (quantidade, pedido_id, produto_id) VALUES
(3, 15, 2), (2, 15, 6), (4, 15, 8);

-- Cliente 7 - Gabriel Lima (1 pedido)
INSERT INTO tb_pedido (data, status, cliente_id, valor_total) VALUES
('2023-05-07', 'ATIVO', 7, 35.50);  -- Pedido 16: 2*3.5 + 1*4 + 3*5.5 = 35.50

INSERT INTO tb_item_pedido (quantidade, pedido_id, produto_id) VALUES
(2, 16, 5), (1, 16, 8), (3, 16, 10);

-- Cliente 8 - Helena Ribeiro (2 pedidos)
INSERT INTO tb_pedido (data, status, cliente_id, valor_total) VALUES
('2023-05-08', 'ATIVO', 8, 44.50);  -- Pedido 17: 1*20 + 3*4.5 + 2*7 = 44.50

INSERT INTO tb_item_pedido (quantidade, pedido_id, produto_id) VALUES
(1, 17, 1), (3, 17, 3), (2, 17, 4);

INSERT INTO tb_pedido (data, status, cliente_id, valor_total) VALUES
('2023-05-15', 'ATIVO', 8, 44.00);  -- Pedido 18: 4*8 + 1*2 + 2*12 = 44.00

INSERT INTO tb_item_pedido (quantidade, pedido_id, produto_id) VALUES
(4, 18, 2), (1, 18, 6), (2, 18, 7);

-- Cliente 9 - Isabel Martins (4 pedidos)
INSERT INTO tb_pedido (data, status, cliente_id, valor_total) VALUES
('2023-05-09', 'ATIVO', 9, 43.00);  -- Pedido 19: 2*8 + 1*4.5 + 4*3.5 = 43.00

INSERT INTO tb_item_pedido (quantidade, pedido_id, produto_id) VALUES
(2, 19, 2), (1, 19, 3), (4, 19, 5);

INSERT INTO tb_pedido (data, status, cliente_id, valor_total) VALUES
('2023-05-13', 'ATIVO', 9, 73.00);  -- Pedido 20: 3*20 + 2*7 + 1*2 = 73.00

INSERT INTO tb_item_pedido (quantidade, pedido_id, produto_id) VALUES
(3, 20, 1), (2, 20, 4), (1, 20, 6);

INSERT INTO tb_pedido (data, status, cliente_id, valor_total) VALUES
('2023-05-17', 'ATIVO', 9, 44.00);  -- Pedido 21: 1*12 + 3*4 + 2*3 = 44.00

INSERT INTO tb_item_pedido (quantidade, pedido_id, produto_id) VALUES
(1, 21, 7), (3, 21, 8), (2, 21, 9);

INSERT INTO tb_pedido (data, status, cliente_id, valor_total) VALUES
('2023-05-19', 'ATIVO', 9, 78.00);  -- Pedido 22: 4*5.5 + 1*20 + 3*8 = 78.00

INSERT INTO tb_item_pedido (quantidade, pedido_id, produto_id) VALUES
(4, 22, 10), (1, 22, 1), (3, 22, 2);

-- Cliente 10 - João Alves (2 pedidos)
INSERT INTO tb_pedido (data, status, cliente_id, valor_total) VALUES
('2023-05-10', 'ATIVO', 10, 29.50);  -- Pedido 23: 3*4.5 + 2*3.5 + 1*12 = 29.50

INSERT INTO tb_item_pedido (quantidade, pedido_id, produto_id) VALUES
(3, 23, 3), (2, 23, 5), (1, 23, 7);

INSERT INTO tb_pedido (data, status, cliente_id, valor_total) VALUES
('2023-05-16', 'ATIVO', 10, 34.00);  -- Pedido 24: 1*8 + 3*7 + 2*2 = 34.00

INSERT INTO tb_item_pedido (quantidade, pedido_id, produto_id) VALUES
(1, 24, 2), (3, 24, 4), (2, 24, 6);