
package dev.imrob.appvendas.gui.pedido;

import com.formdev.flatlaf.FlatClientProperties;
import dev.imrob.appvendas.entity.Pedido;
import dev.imrob.appvendas.entity.StatusPedido;
import dev.imrob.appvendas.gui.componentes.CheckBoxTableHeaderRenderer;
import dev.imrob.appvendas.gui.componentes.TableHeaderAlignment;
import dev.imrob.appvendas.service.PedidoService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import raven.datetime.component.date.DateEvent;
import raven.datetime.component.date.DatePicker;
import raven.popup.DefaultOption;
import raven.popup.GlassPanePopup;
import raven.popup.component.SimplePopupBorder;
import raven.swing.AvatarIcon;
import raven.toast.Notifications;

/**
 * Tela de gerenciamento de pedidos.
 *
 * <p>Esta classe representa a interface gráfica para gerenciar pedidos, 
 * fornecendo funcionalidades para:</p>
 * 
 * <ul>
 *  <li>Visualizar a lista de pedidos.</li>
 *  <li>Filtrar pedidos por período, cliente, produto e status.</li>
 *  <li>Cancelar pedidos.</li>
 *  <li>Visualizar o total de vendas por cliente.</li>
 *  <li>Visualizar o total de vendas por produto.</li>
 * </ul>
 * 
 * <p>A tela exibe diferentes tabelas de acordo com a opção 
 * selecionada: "Pedidos", "Total por Cliente" ou "Total por Produto". 
 * A tabela de pedidos permite a seleção de múltiplos pedidos 
 * para cancelamento. As tabelas de total por cliente e por 
 * produto exibem informações agregadas sobre as vendas.</p>
 * 
 * <p>A classe utiliza componentes da biblioteca Swing para a 
 * construção da interface gráfica e faz uso de notificações 
 * para informar o usuário sobre o andamento das operações.</p>
 * 
 * @author Robson Silva
 */
public class PedidoManager extends javax.swing.JFrame {
    private PedidoService service = new PedidoService();
    private LocalDate dataInicio;
    private LocalDate dataTermino;

     /**
     * Construtor da classe PedidoManager.
     * 
     * <p>Inicializa a tela, define configurações personalizadas, 
     * carrega os componentes do calendário, define a ação de 
     * fechamento da janela e configura os componentes visuais 
     * da tela.</p>
     */
    public PedidoManager() {
        initComponents();
        init();
        carregarCalendario();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getRootPane().putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, true);
    }
    
    /**
     * Inicializa componentes e configurações da tela.
     * 
     * <p>Esta função realiza as seguintes ações:</p>
     * 
     * <ul>
     *  <li>Define o estado inicial dos componentes de filtro.</li>
     *  <li>Configura o GlassPanePopup para exibir pop-ups na tela.</li>
     *  <li>Configura o sistema de notificações para a tela.</li>
     *  <li>Aplica estilos visuais personalizados aos componentes da tela, 
     *  como painéis, tabelas e campos de texto.</li>
     *  <li>Define ícones e placeholders para campos de texto.</li>
     *  <li>Configura a renderização das tabelas, incluindo a adição de 
     *  caixas de seleção para a tabela de pedidos e o alinhamento 
     *  do cabeçalho das tabelas.</li>
     *  <li>Carrega os dados dos pedidos na tabela.</li>
     * </ul>
     */
    private void init() {
        rdoPedido.setSelected(true);
        GlassPanePopup.install(this);
        Notifications.getInstance().setJFrame(this);
        painel.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:25;"
                + "background:$Table.background");
        pnlFiltro.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:25;");
        
        estilizarTabela(tabelaPedidos, scroll);
        estilizarTabela(tabelaClientes, scrollClientes);
        estilizarTabela(tabelaProdutos, scrollProdutos);
        
        lbTitle.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:bold +5;");
        
        txtPesquisar.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new AvatarIcon(getClass().getResource("/icon/pesquisar.png"), 24, 24, 0));
        txtPesquisar.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Pesquisar...");
        txtPesquisar.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:15;"
                + "borderWidth:0;"
                + "focusWidth:0;"
                + "innerFocusWidth:0;"
                + "margin:5,20,5,20;"
                + "background:$Panel.background");
        
        tabelaPedidos.getColumnModel().getColumn(0).setHeaderRenderer(new CheckBoxTableHeaderRenderer(tabelaPedidos, 0));
        
        carregarPedidos();
    }
    
    /**
     * Aplica estilos visuais personalizados aos componentes da tabela e da barra de rolagem.
     * 
     * @param tabela A tabela a ser estilizada.
     * @param scroll A barra de rolagem associada à tabela.
     */
    private void estilizarTabela(JTable tabela, JScrollPane scroll) {
        tabela.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "height:30;"
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background;"
                + "font:bold;");

        tabela.putClientProperty(FlatClientProperties.STYLE, ""
                + "rowHeight:30;"
                + "showHorizontalLines:true;"
                + "intercellSpacing:0,1;"
                + "cellFocusColor:$TableHeader.hoverBackground;"
                + "selectionBackground:$TableHeader.hoverBackground;"
                + "selectionForeground:$Table.foreground;");
        
        tabela.getTableHeader().setDefaultRenderer(new TableHeaderAlignment(tabela));

        scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, ""
                + "trackArc:999;"
                + "trackInsets:3,3,3,3;"
                + "thumbInsets:3,3,3,3;"
                + "background:$Table.background;");
    }
    
    /**
     * Carrega os dados dos pedidos na tabela de pedidos.
     * 
     * <p>Obtém a lista de pedidos do serviço PedidoService e 
     * adiciona cada pedido como uma nova linha na tabela.</p>
     */
    private void carregarPedidos() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabelaPedidos.getModel();
            if (tabelaPedidos.isEditing()) {
                tabelaPedidos.getCellEditor().stopCellEditing();
            }
            model.setRowCount(0);
            List<Pedido> list = service.getPedidosFiltrados(null, null, null, null, StatusPedido.ATIVO);
            for (Pedido p : list) {
                model.addRow(p.toTableRow(tabelaPedidos.getRowCount() + 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Atualiza o filtro da tabela de pedidos de acordo com o texto digitado no campo de pesquisa.
     * 
     * <p>Utiliza um RowFilter para filtrar as linhas da tabela 
     * com base em uma expressão regular. A expressão regular 
     * é construída a partir do texto digitado no campo de pesquisa.</p>
     */
    private void atualizarFiltro() {
        DefaultTableModel model = (DefaultTableModel) tabelaPedidos.getModel();
        var sorter = new TableRowSorter<>(model);
        tabelaPedidos.setRowSorter(sorter);
        String texto = txtPesquisar.getText();
        if (texto.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            try {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto)));
            } catch (PatternSyntaxException e) {
                sorter.setRowFilter(null);
            }
        }
    }
    
    /**
     * Carrega e configura o componente de calendário para seleção de datas.
     * 
     * <p>O calendário permite a seleção de um período de datas, 
     * com data de início e data de término. As datas selecionadas 
     * são armazenadas nos atributos dataInicio e dataTermino.</p>
     */
    private void carregarCalendario() {
        DatePicker datePicker = new DatePicker();
        datePicker.setDateSelectionMode(DatePicker.DateSelectionMode.BETWEEN_DATE_SELECTED);
        datePicker.setSeparator(" até ");
        datePicker.setDateSelectionAble(localDate -> localDate.isBefore(LocalDate.now()));
        datePicker.setCloseAfterSelected(true);
        datePicker.setEditor(txtfPeriodo); 
        datePicker.addDateSelectionListener((DateEvent de) -> {
            LocalDate[] datas = datePicker.getSelectedDateRange();
            dataInicio = datas[0];
            dataTermino = datas[1];
        });
    }
    
    /**
     * Realiza a filtragem dos pedidos de acordo com os filtros selecionados.
     * 
     * <p>Obtém os valores dos filtros de período, cliente, produto e status 
     * e utiliza o serviço PedidoService para obter a lista de pedidos 
     * filtrados. Os pedidos filtrados são então exibidos na tabela de pedidos.</p>
     */
    private void filtrarPesquisa() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabelaPedidos.getModel();
            if (tabelaPedidos.isEditing()) {
                tabelaPedidos.getCellEditor().stopCellEditing();
            }
            model.setRowCount(0);

            Long idCliente = null;
            if (!txtIdCliente.getText().trim().isEmpty()) {
                try {
                    idCliente = Long.valueOf(txtIdCliente.getText().trim());
                } catch (NumberFormatException e) {
                    System.err.println("ID do Cliente inválido: " + e.getMessage());
                }
            }

            Long idProduto = null;
            if (!txtIdProduto.getText().trim().isEmpty()) {
                try {
                    idProduto = Long.valueOf(txtIdProduto.getText().trim());
                } catch (NumberFormatException e) {
                    System.err.println("ID do Produto inválido: " + e.getMessage());
                }
            }

            StatusPedido status = StatusPedido.valueOf(cboStatus.getSelectedItem().toString());
            List<Pedido> list = service.getPedidosFiltrados(dataInicio, dataTermino, 
                    idCliente, idProduto, status);

            for (Pedido p : list) {
                model.addRow(p.toTableRow(tabelaPedidos.getRowCount() + 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
//    public Object[] objetoParaLinhaTabela(Object obj) {
//        NumberFormat nf = new DecimalFormat("R$ #,##0.00");
//        return Object[]{obj[0], obj[1], nf.format(obj[2])};
//    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        radioGroup = new javax.swing.ButtonGroup();
        painel = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        lbTitle = new javax.swing.JLabel();
        rdoPedido = new javax.swing.JRadioButton();
        rdoCliente = new javax.swing.JRadioButton();
        rdoProduto = new javax.swing.JRadioButton();
        pnlFiltro = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtfPeriodo = new javax.swing.JFormattedTextField();
        txtIdProduto = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cboStatus = new javax.swing.JComboBox<>();
        txtIdCliente = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        pnlTabelas = new javax.swing.JTabbedPane();
        scroll = new javax.swing.JScrollPane();
        tabelaPedidos = new javax.swing.JTable();
        scrollClientes = new javax.swing.JScrollPane();
        tabelaClientes = new javax.swing.JTable();
        scrollProdutos = new javax.swing.JScrollPane();
        tabelaProdutos = new javax.swing.JTable();
        btnCancelarPedido = new dev.imrob.appvendas.gui.componentes.ButtonAction();
        txtPesquisar = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lbTitle.setText("PEDIDOS");

        radioGroup.add(rdoPedido);
        rdoPedido.setText("Pedidos");
        rdoPedido.addActionListener(this::rdoPedidoActionPerformed);

        radioGroup.add(rdoCliente);
        rdoCliente.setText("Total por Cliente");
        rdoCliente.addActionListener(this::rdoClienteActionPerformed);

        radioGroup.add(rdoProduto);
        rdoProduto.setText("Total por Produto");
        rdoProduto.addActionListener(this::rdoProdutoActionPerformed);

        jLabel1.setText("ID do Produto");

        jLabel2.setText("Periodo");

        jLabel3.setText("ID do Cliente");

        txtIdProduto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtIdProdutoKeyTyped(evt);
            }
        });

        jLabel4.setText("Status");

        cboStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ATIVO", "EXCLUIDO" }));

        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(this::btnBuscarActionPerformed);

        org.jdesktop.layout.GroupLayout pnlFiltroLayout = new org.jdesktop.layout.GroupLayout(pnlFiltro);
        pnlFiltro.setLayout(pnlFiltroLayout);
        pnlFiltroLayout.setHorizontalGroup(
            pnlFiltroLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlFiltroLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(pnlFiltroLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(txtfPeriodo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 234, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .add(20, 20, 20)
                .add(pnlFiltroLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(txtIdCliente))
                .add(20, 20, 20)
                .add(pnlFiltroLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(txtIdProduto))
                .add(20, 20, 20)
                .add(pnlFiltroLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlFiltroLayout.createSequentialGroup()
                        .add(cboStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 105, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 98, Short.MAX_VALUE)
                        .add(btnBuscar))
                    .add(jLabel4))
                .add(10, 10, 10))
        );
        pnlFiltroLayout.setVerticalGroup(
            pnlFiltroLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlFiltroLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(pnlFiltroLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jLabel3)
                    .add(jLabel1)
                    .add(jLabel4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlFiltroLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtfPeriodo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtIdCliente, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtIdProduto, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cboStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnBuscar))
                .add(10, 10, 10))
        );

        scroll.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        tabelaPedidos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "X", "#", "CLIENTE", "DATA", "ITENS", "TOTAL", "STATUS"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabelaPedidos.getTableHeader().setReorderingAllowed(false);
        scroll.setViewportView(tabelaPedidos);
        if (tabelaPedidos.getColumnModel().getColumnCount() > 0) {
            tabelaPedidos.getColumnModel().getColumn(0).setPreferredWidth(40);
            tabelaPedidos.getColumnModel().getColumn(0).setMaxWidth(40);
            tabelaPedidos.getColumnModel().getColumn(0).setHeaderValue("X");
            tabelaPedidos.getColumnModel().getColumn(1).setMaxWidth(40);
            tabelaPedidos.getColumnModel().getColumn(3).setMinWidth(100);
            tabelaPedidos.getColumnModel().getColumn(3).setMaxWidth(100);
            tabelaPedidos.getColumnModel().getColumn(4).setMinWidth(100);
            tabelaPedidos.getColumnModel().getColumn(4).setPreferredWidth(100);
            tabelaPedidos.getColumnModel().getColumn(4).setMaxWidth(100);
            tabelaPedidos.getColumnModel().getColumn(5).setMinWidth(150);
            tabelaPedidos.getColumnModel().getColumn(5).setPreferredWidth(150);
            tabelaPedidos.getColumnModel().getColumn(5).setMaxWidth(150);
            tabelaPedidos.getColumnModel().getColumn(6).setMinWidth(80);
            tabelaPedidos.getColumnModel().getColumn(6).setMaxWidth(80);
        }

        pnlTabelas.addTab("Pedidos", scroll);

        scrollClientes.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        tabelaClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "CLIENTE", "TOTAL EM COMPRAS"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabelaClientes.getTableHeader().setReorderingAllowed(false);
        scrollClientes.setViewportView(tabelaClientes);
        if (tabelaClientes.getColumnModel().getColumnCount() > 0) {
            tabelaClientes.getColumnModel().getColumn(0).setMaxWidth(40);
            tabelaClientes.getColumnModel().getColumn(2).setMinWidth(150);
            tabelaClientes.getColumnModel().getColumn(2).setPreferredWidth(150);
            tabelaClientes.getColumnModel().getColumn(2).setMaxWidth(150);
        }

        pnlTabelas.addTab("Por Cliente", scrollClientes);

        scrollProdutos.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        tabelaProdutos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "PRODUTO", "QUANTIDADE", "VALOR TOTAL"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabelaProdutos.getTableHeader().setReorderingAllowed(false);
        scrollProdutos.setViewportView(tabelaProdutos);
        if (tabelaProdutos.getColumnModel().getColumnCount() > 0) {
            tabelaProdutos.getColumnModel().getColumn(0).setMaxWidth(40);
            tabelaProdutos.getColumnModel().getColumn(2).setMinWidth(100);
            tabelaProdutos.getColumnModel().getColumn(2).setMaxWidth(100);
            tabelaProdutos.getColumnModel().getColumn(3).setMinWidth(100);
            tabelaProdutos.getColumnModel().getColumn(3).setPreferredWidth(100);
            tabelaProdutos.getColumnModel().getColumn(3).setMaxWidth(100);
        }

        pnlTabelas.addTab("Por Produto", scrollProdutos);

        btnCancelarPedido.setText("Cancelar Pedido");
        btnCancelarPedido.addActionListener(this::btnCancelarPedidoActionPerformed);

        txtPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPesquisarKeyTyped(evt);
            }
        });

        org.jdesktop.layout.GroupLayout painelLayout = new org.jdesktop.layout.GroupLayout(painel);
        painel.setLayout(painelLayout);
        painelLayout.setHorizontalGroup(
            painelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(painelLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(painelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(painelLayout.createSequentialGroup()
                        .add(painelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(pnlTabelas)
                            .add(painelLayout.createSequentialGroup()
                                .add(6, 6, 6)
                                .add(txtPesquisar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 148, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(rdoPedido)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(rdoCliente)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(rdoProduto)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(btnCancelarPedido, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 153, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlFiltro, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jSeparator1))
                        .add(10, 10, 10))
                    .add(painelLayout.createSequentialGroup()
                        .add(lbTitle)
                        .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        painelLayout.setVerticalGroup(
            painelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, painelLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(lbTitle)
                .add(10, 10, 10)
                .add(painelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(rdoPedido)
                    .add(rdoCliente)
                    .add(rdoProduto)
                    .add(btnCancelarPedido, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtPesquisar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(10, 10, 10)
                .add(pnlFiltro, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(5, 5, 5)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(pnlTabelas)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(painel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(35, 35, 35)
                .add(painel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtPesquisarKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarKeyTyped
        atualizarFiltro();
    }//GEN-LAST:event_txtPesquisarKeyTyped

    private void txtIdProdutoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtIdProdutoKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdProdutoKeyTyped
    /**
     * Ação executada quando o RadioButton "Pedidos" é selecionado.
     * 
     * <p>Exibe o painel de filtros e seleciona a aba "Pedidos" na interface.</p>
     * 
     * @param evt O evento de ação do RadioButton.
     */
    private void rdoPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoPedidoActionPerformed
        pnlFiltro.setVisible(true);
        pnlTabelas.setSelectedIndex(0);
    }//GEN-LAST:event_rdoPedidoActionPerformed
    /**
     * Ação executada quando o RadioButton "Total por Cliente" é selecionado.
     * 
     * <p>Oculta o painel de filtros, seleciona a aba "Por Cliente" 
     * na interface e carrega os dados do total de vendas por cliente 
     * na tabela correspondente.</p>
     * 
     * @param evt O evento de ação do RadioButton.
     */
    private void rdoClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoClienteActionPerformed
        pnlFiltro.setVisible(false);
        pnlTabelas.setSelectedIndex(1);
        try {
            DefaultTableModel model = (DefaultTableModel) tabelaClientes.getModel();
            if (tabelaClientes.isEditing()) {
                tabelaClientes.getCellEditor().stopCellEditing();
            }
            model.setRowCount(0);

            List<Object[]> list = service.getPedidosAgrupadosPorCliente();
            for (Object[] row : list) {
                if (row.length == model.getColumnCount()) {
                    model.addRow(row);
                } else {
                    System.err.println("Número de colunas incompatível: " + row.length + " colunas, esperado " + model.getColumnCount());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_rdoClienteActionPerformed
    /**
     * Ação executada quando o RadioButton "Total por Produto" é selecionado.
     * 
     * <p>Oculta o painel de filtros, seleciona a aba "Por Produto" 
     * na interface e carrega os dados do total de vendas por produto 
     * na tabela correspondente.</p>
     * 
     * @param evt O evento de ação do RadioButton.
     */
    private void rdoProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoProdutoActionPerformed
        pnlFiltro.setVisible(false);
        pnlTabelas.setSelectedIndex(2);
        try {
            DefaultTableModel model = (DefaultTableModel) tabelaProdutos.getModel();
            if (tabelaProdutos.isEditing()) {
                tabelaProdutos.getCellEditor().stopCellEditing();
            }
            model.setRowCount(0);

            List<Object[]> list = service.getPedidosAgrupadosPorProduto();
            for (Object[] row : list) {
                if (row.length == model.getColumnCount()) {
                    model.addRow(row);
                } else {
                    System.err.println("Número de colunas incompatível: " + row.length + " colunas, esperado " + model.getColumnCount());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_rdoProdutoActionPerformed
    /**
     * Ação executada quando o botão "Buscar" é pressionado.
     * 
     * <p>Chama o método filtrarPesquisa() para realizar a 
     * filtragem dos pedidos de acordo com os filtros selecionados.</p>
     * 
     * @param evt O evento de ação do botão.
     */
    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        filtrarPesquisa();
    }//GEN-LAST:event_btnBuscarActionPerformed
    /**
     * Ação executada quando o botão "Cancelar Pedido" é pressionado.
     * 
     * <p>Cancela os pedidos selecionados na tabela de pedidos. 
     * Uma caixa de diálogo de confirmação é exibida antes do 
     * cancelamento. Se o usuário confirmar, os pedidos 
     * selecionados serão cancelados e a tabela será atualizada.</p>
     * 
     * @param evt O evento de ação do botão.
     */
    private void btnCancelarPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarPedidoActionPerformed
        List<Pedido> list = getSelectedData();
        if (!list.isEmpty()) {
            DefaultOption option = new DefaultOption() {
                @Override
                public boolean closeWhenClickOutside() {
                    return true;
                }
            };
            String actions[] = new String[]{"Sair", "Cancelar"};
            JLabel label = new JLabel("Você tem certeza que deseja cancelar " + list.size() + " pedido(s) ?");
            label.setBorder(new EmptyBorder(0, 25, 0, 25));
            GlassPanePopup.showPopup(new SimplePopupBorder(label, "Confirmar", actions, (pc, i) -> {
                if (i == 1) {
                    // cancelar
                    try {
                        for (Pedido d : list) {
                            service.cancelarPedido(d.getId());
                        }
                        pc.closePopup();
                        Notifications.getInstance().show(Notifications.Type.SUCCESS, "Pedido cancelado com sucesso!");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    carregarPedidos();
                } else {
                    pc.closePopup();
                }
            }), option);
        } else {
            Notifications.getInstance().show(Notifications.Type.WARNING, "Por favor, selecione um pedido para cancelar.");
        }
    }//GEN-LAST:event_btnCancelarPedidoActionPerformed

    /**
     * Retorna uma lista com os pedidos selecionados na tabela de pedidos.
     * 
     * @return Uma lista de objetos Pedido contendo os pedidos selecionados.
     */
    private List<Pedido> getSelectedData() {
        List<Pedido> list = new ArrayList<>();
        for (int i = 0; i < tabelaPedidos.getRowCount(); i++) {
            if ((boolean) tabelaPedidos.getValueAt(i, 0)) {
                Pedido dados = (Pedido) tabelaPedidos.getValueAt(i, 2);
                list.add(dados);
            }
        }
        return list;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PedidoManager().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private dev.imrob.appvendas.gui.componentes.ButtonAction btnCancelarPedido;
    private javax.swing.JComboBox<Object> cboStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JPanel painel;
    private javax.swing.JPanel pnlFiltro;
    private javax.swing.JTabbedPane pnlTabelas;
    private javax.swing.ButtonGroup radioGroup;
    private javax.swing.JRadioButton rdoCliente;
    private javax.swing.JRadioButton rdoPedido;
    private javax.swing.JRadioButton rdoProduto;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JScrollPane scrollClientes;
    private javax.swing.JScrollPane scrollProdutos;
    private javax.swing.JTable tabelaClientes;
    private javax.swing.JTable tabelaPedidos;
    private javax.swing.JTable tabelaProdutos;
    private javax.swing.JTextField txtIdCliente;
    private javax.swing.JTextField txtIdProduto;
    private javax.swing.JTextField txtPesquisar;
    private javax.swing.JFormattedTextField txtfPeriodo;
    // End of variables declaration//GEN-END:variables

}
