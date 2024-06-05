
package dev.imrob.appvendas.gui.vendas;

import com.formdev.flatlaf.FlatClientProperties;
import static dev.imrob.appvendas.config.FeignConfig.extrairMensagemDeErro;
import dev.imrob.appvendas.entity.Cliente;
import dev.imrob.appvendas.entity.Produto;
import dev.imrob.appvendas.entity.dto.CriarItemPedidoDTO;
import dev.imrob.appvendas.entity.dto.CriarPedidoDTO;
import dev.imrob.appvendas.gui.componentes.CheckBoxTableHeaderRenderer;
import dev.imrob.appvendas.gui.componentes.TableHeaderAlignment;
import dev.imrob.appvendas.gui.vendas.modal.AplicarDesconto;
import dev.imrob.appvendas.gui.vendas.modal.InserirCliente;
import dev.imrob.appvendas.service.PedidoService;
import dev.imrob.appvendas.service.ProdutoService;
import dev.imrob.appvendas.util.Util;
import feign.FeignException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import raven.popup.DefaultOption;
import raven.popup.GlassPanePopup;
import raven.popup.component.SimplePopupBorder;
import raven.toast.Notifications;

/**
 * Tela principal do ponto de venda (PDV).
 * 
 * <p>Esta classe representa a interface gráfica do ponto de venda, 
 * fornecendo funcionalidades para:</p>
 * 
 * <ul>
 *  <li>Iniciar uma nova venda.</li>
 *  <li>Adicionar produtos à venda, buscando-os por código.</li>
 *  <li>Ajustar a quantidade de produtos na venda.</li>
 *  <li>Remover produtos da venda.</li>
 *  <li>Aplicar descontos à venda.</li>
 *  <li>Finalizar a venda, processando o pagamento.</li>
 *  <li>Cancelar a venda em andamento.</li>
 * </ul>
 * 
 * <p>A tela exibe informações sobre a venda em andamento, 
 * como o cliente, os produtos adicionados, o subtotal, o desconto 
 * (se houver) e o valor total. </p>
 * 
 * <p>A classe utiliza componentes da biblioteca Swing para a 
 * construção da interface gráfica e faz uso de notificações 
 * para informar o usuário sobre o andamento das operações.</p>
 * 
 * @author Robson Silva
 */
public class PontoDeVenda extends javax.swing.JFrame {
    private Boolean vendaIniciou = false;
    private boolean popupAberto = false;
    private Double porcentagemDesconto = 0d;
    private Cliente cliente = new Cliente();
    
    private ProdutoService produtoService = new ProdutoService();
    
   /**
     * Construtor da classe PontoDeVenda.
     * 
     * <p>Inicializa a tela, define configurações personalizadas, 
     * estiliza os componentes visuais da tela e define atalhos de 
     * teclado para ações comuns.</p>
     */
    public PontoDeVenda() {
        initComponents();
        configPersonalizada();
        estilizarTabela();
        getRootPane().putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, true);
    }
    
    /**
     * Define configurações personalizadas para a tela do PDV.
     * 
     * <p>Esta função realiza as seguintes ações:</p>
     * <ul>
     *  <li>Configura a ação de fechamento da tela.</li>
     *  <li>Instala o componente GlassPanePopup para exibir pop-ups.</li>
     *  <li>Configura o componente Notifications para exibir notificações na tela.</li>
     *  <li>Inicializa componentes visuais.</li>
     *  <li>Define atalhos de teclado para ações como iniciar venda, adicionar produto,
     *  excluir produto, cancelar venda, aplicar desconto e finalizar venda.</li>
     * </ul>
     */
    private void configPersonalizada(){
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        GlassPanePopup.install(this);
        Notifications.getInstance().setJFrame(this);
        pnlTroco.setVisible(false);               
        
        criarAction("IniciarVendaAction", KeyEvent.VK_F1);
        criarAction("AdicionarProdutoAction", KeyEvent.VK_F4);
        criarAction("ExcluirProdutoAction", KeyEvent.VK_F5);
        criarAction("CancelarVendaAction", KeyEvent.VK_F8);
        criarAction("AplicarDescontoAction", KeyEvent.VK_F9);
        criarAction("FinalizarVendaAction", KeyEvent.VK_F12);
    }
    
    /**
     * Aplica estilos visuais personalizados aos componentes da tabela e do painel de pagamento.
     */
    private void estilizarTabela() {
        pnlTabela.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:25;"
                + "background:$Table.background");
        pnlInformacao.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:25;"
                + "background:rgb(46, 54, 64)");
        txtInformacoes.putClientProperty(FlatClientProperties.STYLE, ""
                + "foreground: rgb(255,255,255);");
        pnlPagamento.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:25;"
                + "background:$Table.background;");
        pnlTroco.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:25;"
                + "background:$Table.background;");
        btnFinalizar.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:25;"
                + "background:rgb(46, 54, 64);"
                + "foreground:rgb(255,255,255);");

        scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, ""
                + "trackArc:999;"
                + "trackInsets:3,3,3,3;"
                + "thumbInsets:3,3,3,3;"
                + "background:$Table.background;");
        
        txtCodigoProduto.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Digite o codigo do produto...");
        txtCodigoProduto.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:15;"
                + "borderWidth:0;"
                + "focusWidth:0;"
                + "innerFocusWidth:0;"
                + "margin:5,20,5,20;"
                + "background:$Panel.background;");
        
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
        
        tabela.getColumnModel().getColumn(0).setHeaderRenderer(new CheckBoxTableHeaderRenderer(tabela, 0));
        tabela.getTableHeader().setDefaultRenderer(new TableHeaderAlignment(tabela));
    }
    
    /**
     * Cria um atalho de teclado para uma ação específica.
     * 
     * @param nomeAction Nome da ação a ser associada ao atalho.
     * @param keyCode Código da tecla a ser utilizada como atalho.
     */
    private void criarAction(String nomeAction, int keyCode) {
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(keyCode, 0), nomeAction);

        ActionMap actionMap = this.getRootPane().getActionMap();
        actionMap.put(nomeAction, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nomeAction == "IniciarVendaAction") {
                    System.out.println("F1 - Iniciar Venda");
                        if (!popupAberto){
                            iniciarVenda();
                        }
                } else if (vendaIniciou) {
                switch (nomeAction) {
                    case "AdicionarProdutoAction":
                        System.out.println("F4 - Adicionar Produto");
                            adicionarProduto();
                        break;
                    case "ExcluirProdutoAction":
                        System.out.println("F5 - Excluir Produto");
                            excluirProduto();
                        break;
                    case "CancelarVendaAction":
                        System.out.println("F8 - Cancelar Venda");
                            cancelarVenda();
                        break;
                    case "AplicarDescontoAction":
                        System.out.println("F9 - Aplicar Desconto");
                            aplicarDesconto();
                        break;
                    case "FinalizarVendaAction":
                        System.out.println("F12 - Finalizar Venda");
                            finalizarVenda();
                        break;
                }
                } else {
                    Notifications.getInstance().show(Notifications.Type.ERROR, "Você não possui uma venda iniciada.");
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlTabela = new javax.swing.JPanel();
        scroll = new javax.swing.JScrollPane();
        tabela = new javax.swing.JTable();
        btnAdicionar = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        txtCodigoProduto = new javax.swing.JTextField();
        pnlInformacao = new javax.swing.JPanel();
        txtInformacoes = new javax.swing.JLabel();
        pnlPagamento = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtIdCliente = new javax.swing.JLabel();
        txtNome = new javax.swing.JLabel();
        txtLimite = new javax.swing.JLabel();
        txtDiaFechamento = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        txtTotal = new javax.swing.JLabel();
        btnFinalizar = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        txtDesconto = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        txtSubTotal = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        cboPagamento = new javax.swing.JComboBox<>();
        pnlTroco = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        txtDinheiro = new javax.swing.JFormattedTextField();
        txtTroco = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("framePDV"); // NOI18N

        scroll.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        tabela.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "X", "#", "DESCRICAO", "PREÇO", "QTD", "SUBTOTAL"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabela.getTableHeader().setReorderingAllowed(false);
        scroll.setViewportView(tabela);
        if (tabela.getColumnModel().getColumnCount() > 0) {
            tabela.getColumnModel().getColumn(0).setPreferredWidth(40);
            tabela.getColumnModel().getColumn(0).setMaxWidth(40);
            tabela.getColumnModel().getColumn(1).setPreferredWidth(40);
            tabela.getColumnModel().getColumn(1).setMaxWidth(40);
            tabela.getColumnModel().getColumn(3).setMinWidth(100);
            tabela.getColumnModel().getColumn(3).setPreferredWidth(100);
            tabela.getColumnModel().getColumn(3).setMaxWidth(100);
            tabela.getColumnModel().getColumn(4).setPreferredWidth(40);
            tabela.getColumnModel().getColumn(4).setMaxWidth(40);
            tabela.getColumnModel().getColumn(5).setPreferredWidth(100);
            tabela.getColumnModel().getColumn(5).setMaxWidth(100);
        }

        btnAdicionar.setText("ADICIONAR");
        btnAdicionar.addActionListener(this::btnAdicionarActionPerformed);

        btnExcluir.setText("EXCLUIR");
        btnExcluir.addActionListener(this::btnExcluirActionPerformed);

        txtCodigoProduto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCodigoProdutoKeyPressed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlTabelaLayout = new org.jdesktop.layout.GroupLayout(pnlTabela);
        pnlTabela.setLayout(pnlTabelaLayout);
        pnlTabelaLayout.setHorizontalGroup(
            pnlTabelaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlTabelaLayout.createSequentialGroup()
                .add(12, 12, 12)
                .add(txtCodigoProduto, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 218, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(btnAdicionar)
                .add(18, 18, 18)
                .add(btnExcluir)
                .add(12, 12, 12))
            .add(scroll, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
        );
        pnlTabelaLayout.setVerticalGroup(
            pnlTabelaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlTabelaLayout.createSequentialGroup()
                .add(12, 12, 12)
                .add(pnlTabelaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnAdicionar)
                    .add(btnExcluir)
                    .add(txtCodigoProduto, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(12, 12, 12)
                .add(scroll, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE))
        );

        txtInformacoes.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        txtInformacoes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtInformacoes.setText("CAIXA LIVRE");

        org.jdesktop.layout.GroupLayout pnlInformacaoLayout = new org.jdesktop.layout.GroupLayout(pnlInformacao);
        pnlInformacao.setLayout(pnlInformacaoLayout);
        pnlInformacaoLayout.setHorizontalGroup(
            pnlInformacaoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlInformacaoLayout.createSequentialGroup()
                .addContainerGap()
                .add(txtInformacoes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlInformacaoLayout.setVerticalGroup(
            pnlInformacaoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlInformacaoLayout.createSequentialGroup()
                .addContainerGap()
                .add(txtInformacoes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Dados do cliente");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setText("Nome");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setText("Limite de compras");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setText("Dia fechamento da fatura");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel10.setText("Codigo");

        txtIdCliente.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtIdCliente.setText(".");

        txtNome.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtNome.setText(".");

        txtLimite.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtLimite.setText(".");

        txtDiaFechamento.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtDiaFechamento.setText(".");

        txtTotal.setFont(new java.awt.Font("Segoe UI", 1, 50)); // NOI18N
        txtTotal.setText("R$ 0,00");

        btnFinalizar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnFinalizar.setText("FINALIZAR");
        btnFinalizar.addActionListener(this::btnFinalizarActionPerformed);

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel18.setText("DESCONTO");

        txtDesconto.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtDesconto.setText("R$ 0,00");

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel20.setText("SUBTOTAL");

        txtSubTotal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtSubTotal.setText("R$ 0,00");

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel23.setText("Forma de Pagamento");

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel24.setText("Total");

        cboPagamento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SELECIONE", "CARTAO CREDITO", "CARTAO DEBITO", "PIX", "DINHEIRO" }));
        cboPagamento.addActionListener(this::cboPagamentoActionPerformed);

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel15.setText("Dinheiro");

        txtDinheiro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtDinheiroKeyPressed(evt);
            }
        });

        txtTroco.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtTroco.setText("R$ 0,00");

        jLabel25.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel25.setText("Troco");

        org.jdesktop.layout.GroupLayout pnlTrocoLayout = new org.jdesktop.layout.GroupLayout(pnlTroco);
        pnlTroco.setLayout(pnlTrocoLayout);
        pnlTrocoLayout.setHorizontalGroup(
            pnlTrocoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlTrocoLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlTrocoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlTrocoLayout.createSequentialGroup()
                        .add(jLabel25)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(txtTroco))
                    .add(pnlTrocoLayout.createSequentialGroup()
                        .add(jLabel15)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(txtDinheiro, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 125, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlTrocoLayout.setVerticalGroup(
            pnlTrocoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlTrocoLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlTrocoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtDinheiro, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel15))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlTrocoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel25)
                    .add(txtTroco))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout pnlPagamentoLayout = new org.jdesktop.layout.GroupLayout(pnlPagamento);
        pnlPagamento.setLayout(pnlPagamentoLayout);
        pnlPagamentoLayout.setHorizontalGroup(
            pnlPagamentoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlPagamentoLayout.createSequentialGroup()
                .add(12, 12, 12)
                .add(pnlPagamentoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlPagamentoLayout.createSequentialGroup()
                        .add(jLabel6)
                        .add(0, 0, Short.MAX_VALUE))
                    .add(pnlPagamentoLayout.createSequentialGroup()
                        .add(pnlTroco, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlPagamentoLayout.createSequentialGroup()
                        .add(pnlPagamentoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jSeparator2)
                            .add(pnlPagamentoLayout.createSequentialGroup()
                                .add(jLabel23)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(cboPagamento, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 156, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, btnFinalizar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jSeparator1)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, pnlPagamentoLayout.createSequentialGroup()
                                .add(jLabel24)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(txtTotal))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, pnlPagamentoLayout.createSequentialGroup()
                                .add(4, 4, 4)
                                .add(pnlPagamentoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel8)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel7)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel9)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel10))
                                .add(18, 18, 18)
                                .add(pnlPagamentoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(txtIdCliente, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                                    .add(txtNome, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(txtLimite, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(txtDiaFechamento, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .add(0, 0, Short.MAX_VALUE))
                            .add(pnlPagamentoLayout.createSequentialGroup()
                                .add(0, 0, Short.MAX_VALUE)
                                .add(pnlPagamentoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                    .add(pnlPagamentoLayout.createSequentialGroup()
                                        .add(jLabel18)
                                        .add(198, 198, 198)
                                        .add(txtDesconto))
                                    .add(pnlPagamentoLayout.createSequentialGroup()
                                        .add(jLabel20)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(txtSubTotal)))))
                        .add(12, 12, 12))))
        );
        pnlPagamentoLayout.setVerticalGroup(
            pnlPagamentoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlPagamentoLayout.createSequentialGroup()
                .add(12, 12, 12)
                .add(jLabel6)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPagamentoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel10)
                    .add(txtIdCliente))
                .add(5, 5, 5)
                .add(pnlPagamentoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(txtNome))
                .add(5, 5, 5)
                .add(pnlPagamentoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel8)
                    .add(txtLimite))
                .add(5, 5, 5)
                .add(pnlPagamentoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel9)
                    .add(txtDiaFechamento))
                .add(10, 10, 10)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPagamentoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel23)
                    .add(cboPagamento, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(pnlTroco, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPagamentoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel20)
                    .add(txtSubTotal))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPagamentoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel18)
                    .add(txtDesconto))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(pnlPagamentoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtTotal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 44, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel24))
                .add(20, 20, 20)
                .add(btnFinalizar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 41, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(12, 12, 12))
        );

        jLabel2.setText("F4 = Adicionar Produto");

        jLabel3.setText("F5 = Excluir Produto");

        jLabel4.setText("F8 = Cancelar Venda");

        jLabel5.setText("F9 = Aplicar Desconto");

        jLabel22.setText("F1 = Iniciar Venda");

        jLabel26.setText("F12 = Finalizar Venda");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(20, 20, 20)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlInformacao, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(pnlTabela, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(18, 18, 18)
                        .add(pnlPagamento, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(jLabel22)
                        .add(18, 18, 18)
                        .add(jLabel2)
                        .add(18, 18, 18)
                        .add(jLabel3)
                        .add(18, 18, 18)
                        .add(jLabel4)
                        .add(18, 18, 18)
                        .add(jLabel5)
                        .add(18, 18, 18)
                        .add(jLabel26)
                        .add(0, 0, Short.MAX_VALUE)))
                .add(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(35, 35, 35)
                .add(pnlInformacao, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlTabela, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(pnlPagamento, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jLabel3)
                    .add(jLabel4)
                    .add(jLabel5)
                    .add(jLabel22)
                    .add(jLabel26))
                .add(10, 10, 10))
        );

        getAccessibleContext().setAccessibleDescription("");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarActionPerformed
        if (!vendaIniciou) {
            Notifications.getInstance().show(Notifications.Type.WARNING, "Necessário iniciar a venda antes de adicionar um produto");
            
        } else {
            adicionarProduto();
        }
    }//GEN-LAST:event_btnAdicionarActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        excluirProduto();
    }//GEN-LAST:event_btnExcluirActionPerformed

    private void txtCodigoProdutoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoProdutoKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            adicionarProduto();
            txtCodigoProduto.selectAll();
        }
    }//GEN-LAST:event_txtCodigoProdutoKeyPressed

    private void txtDinheiroKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDinheiroKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            calcularTroco();
        }
    }//GEN-LAST:event_txtDinheiroKeyPressed

    private void cboPagamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPagamentoActionPerformed
        if (cboPagamento.getSelectedItem().equals("DINHEIRO")) {
            pnlTroco.setVisible(true);

        } else {
            pnlTroco.setVisible(false);

        }
    }//GEN-LAST:event_cboPagamentoActionPerformed

    private void btnFinalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinalizarActionPerformed
        finalizarVenda();
    }//GEN-LAST:event_btnFinalizarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PontoDeVenda.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PontoDeVenda.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PontoDeVenda.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PontoDeVenda.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PontoDeVenda().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdicionar;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnFinalizar;
    private javax.swing.JComboBox<String> cboPagamento;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPanel pnlInformacao;
    private javax.swing.JPanel pnlPagamento;
    private javax.swing.JPanel pnlTabela;
    private javax.swing.JPanel pnlTroco;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTable tabela;
    private javax.swing.JTextField txtCodigoProduto;
    private javax.swing.JLabel txtDesconto;
    private javax.swing.JLabel txtDiaFechamento;
    private javax.swing.JFormattedTextField txtDinheiro;
    private javax.swing.JLabel txtIdCliente;
    private javax.swing.JLabel txtInformacoes;
    private javax.swing.JLabel txtLimite;
    private javax.swing.JLabel txtNome;
    private javax.swing.JLabel txtSubTotal;
    private javax.swing.JLabel txtTotal;
    private javax.swing.JLabel txtTroco;
    // End of variables declaration//GEN-END:variables

    /**
     * Retorna um array de inteiros com os índices das linhas selecionadas na tabela.
     * 
     * @return Um array de inteiros contendo os índices das linhas selecionadas.
     */
    private int[] getSelectedRows() {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < tabela.getRowCount(); i++) {
            if ((boolean) tabela.getValueAt(i, 0)) { // Coluna 0: Checkbox
                indices.add(i);
            }
        }
        return indices.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Inicia uma nova venda.
     * 
     * <p>Se já houver uma venda em andamento, será exibida uma caixa 
     * de diálogo perguntando se o usuário deseja alterar o cliente 
     * da venda. Se o usuário confirmar, um novo cliente poderá 
     * ser selecionado. Caso contrário, a venda atual será mantida.</p>
     * 
     * <p>Se não houver uma venda em andamento, o usuário será 
     * direcionado para a seleção de um cliente para iniciar a venda.</p>
     */
    private void iniciarVenda() {
        if (vendaIniciou) {
            DefaultOption option = new DefaultOption() {
                @Override
                public boolean closeWhenClickOutside() {
                    return true;
                }
            };
            String actions[] = new String[]{"Não", "Sim"};
            JLabel label = new JLabel("<html>Você já tem uma venda iniciada!"+ "<br>" +"Deseja alterar o cliente da venda?</html>");
            label.setBorder(new EmptyBorder(0, 25, 0, 25));
            popupAberto = true;
            GlassPanePopup.showPopup(new SimplePopupBorder(label, "Confirmar", actions, (pc, i) -> {
                
                if (i == 1) {
                    inserirCliente();
                    pc.closePopup();
                } else {
                    pc.closePopup();
                    popupAberto = false;
                }
            }), option);
        } else {
            inserirCliente();
        }
    }

    /**
     * Adiciona um produto à venda.
     * 
     * <p>O código do produto é obtido a partir do campo de texto 
     * txtCodigoProduto. Se o código for válido e o produto for 
     * encontrado, ele será adicionado à tabela de itens da venda. 
     * Caso contrário, uma mensagem de erro será exibida.</p>
     * 
     * <p>Se o produto já estiver na tabela, a quantidade do produto 
     * será incrementada.</p>
     */
    private void adicionarProduto() {        
        try {
            Long id = Long.valueOf(txtCodigoProduto.getText());
        
            if (!produtoJaAdicionado(id)) {
                try {
                    Produto produto = produtoService.findById(id);

                    if (produto != null) {
                        adicionarProdutoNaTabela(produto);
                    } else {
                        Notifications.getInstance().show(Notifications.Type.WARNING, "Produto não encontrado!");
                        
                    }
                } catch (FeignException.NotFound ex) {
                        String mensagemErro = extrairMensagemDeErro(ex.contentUTF8()); 
                        Notifications.getInstance().show(Notifications.Type.WARNING, mensagemErro);
                        
                    }
            } else {
                incrementarQuantidadeProduto(id);
            }
        } catch (NumberFormatException numberFormatException) {
            Notifications.getInstance().show(Notifications.Type.WARNING, "Digite um código válido");
            
        }
    }

    /**
     * Exclui os produtos selecionados da tabela de itens da venda.
     * 
     * <p>Uma caixa de diálogo de confirmação será exibida antes 
     * da exclusão dos produtos. Se o usuário confirmar a exclusão, 
     * as linhas selecionadas serão removidas da tabela e o total 
     * da venda será recalculado.</p>
     */
    private void excluirProduto() {
        int[] indicesLinhasMarcadas = getSelectedRows();
        
        if (indicesLinhasMarcadas.length > 0) {
            DefaultOption option = new DefaultOption() {
                @Override
                public boolean closeWhenClickOutside() {
                    return true;
                }
            };
            String actions[] = new String[]{"Cancelar", "Excluir"};
            JLabel label = new JLabel("Você tem certeza que deseja excluir " + indicesLinhasMarcadas.length + " produto(s)?");
            label.setBorder(new EmptyBorder(0, 25, 0, 25));
            GlassPanePopup.showPopup(new SimplePopupBorder(label, "Confirmar", actions, (pc, i) -> {
                popupAberto = true;
                if (i == 1) {
                    DefaultTableModel model = (DefaultTableModel) tabela.getModel();

                    for (int idx = indicesLinhasMarcadas.length - 1; idx >= 0; idx--) {
                        model.removeRow(tabela.convertRowIndexToModel(indicesLinhasMarcadas[idx]));
                    }

                    calcularTotal();

                    pc.closePopup();
                    popupAberto = false;
                    Notifications.getInstance().show(Notifications.Type.SUCCESS, "Produto(s) excluído(s) com sucesso!");
                    
                } else {
                    pc.closePopup();
                    popupAberto = false;
                    
                }
            }), option);
        } else {
            Notifications.getInstance().show(Notifications.Type.WARNING, "Por favor, selecione um ou mais produtos para excluir.");
            
        }
    }

    /**
     * Cancela a venda em andamento.
     * 
     * <p>Uma caixa de diálogo de confirmação será exibida antes 
     * do cancelamento da venda. Se o usuário confirmar o cancelamento, 
     * a venda será cancelada e os dados da venda serão limpos.</p>
     */
    private void cancelarVenda() {
        if (vendaIniciou) {
            DefaultOption option = new DefaultOption() {
                @Override
                public boolean closeWhenClickOutside() {
                    return true;
                }
            };
            String actions[] = new String[]{"Não", "Sim"};
            JLabel label = new JLabel("Tem certeza que deseja cancelar essa venda?");
            label.setBorder(new EmptyBorder(0, 25, 0, 25));
            GlassPanePopup.showPopup(new SimplePopupBorder(label, "Confirmar", actions, (pc, i) -> {
                popupAberto = true;
                if (i == 1) {
                    limparVenda();
                    pc.closePopup();
                    popupAberto = false;
                    Notifications.getInstance().show(Notifications.Type.SUCCESS, "Venda cancelada com sucesso!");
                    
                } else {
                    pc.closePopup();
                    popupAberto = false;
                }
            }), option);
        } else {
            Notifications.getInstance().show(Notifications.Type.ERROR, "Você não possui nenhuma venda iniciada!");
            
        }
    }
    
    /**
     * Limpa os dados da venda atual e reinicia a tela para uma nova venda.
     */
    private void limparVenda() {
        cliente = new Cliente();
                    txtIdCliente.setText(".");
                    txtNome.setText(".");
                    txtLimite.setText(".");
                    txtDiaFechamento.setText(".");
                    txtInformacoes.setText("CAIXA LIVRE");
                    txtSubTotal.setText("R$ 0,00");
                    txtDesconto.setText("R$ 0,00");
                    txtTotal.setText("R$ 0,00");
                    txtTroco.setText("R$ 0,00");
                    txtDinheiro.setText("");               
                    limparTabela();
                    vendaIniciou = false;
    }
    
    /**
     * Exibe um pop-up para o usuário inserir o código do cliente e iniciar a venda.
     */
    private void inserirCliente(){
        InserirCliente inserirCliente = new InserirCliente();
        popupAberto = true;
        DefaultOption option = new DefaultOption() {
            @Override
            public boolean closeWhenClickOutside() {
                return true;
            }
        };
        String actions[] = new String[]{"Cancelar", "Iniciar"};
        GlassPanePopup.showPopup(new SimplePopupBorder(inserirCliente, "Digite o codigo do cliente", actions, (pc, i) -> {
            if (i == 1) {
                try {
                    cliente = inserirCliente.getCliente();
                    txtIdCliente.setText(String.valueOf(cliente.getId()));
                    txtNome.setText(cliente.getNome());
                    txtLimite.setText(String.format("R$ %.2f", cliente.getLimiteCompra()));
                    txtDiaFechamento.setText(String.valueOf(cliente.getDiaFechamentoFatura()));
                    pc.closePopup();
                    popupAberto = false;
                    Notifications.getInstance().show(Notifications.Type.SUCCESS, "Venda iniciada...");
                    vendaIniciou = true;
                    txtInformacoes.setText("Adicione um produto");

                } catch (RuntimeException ex) {
                    Notifications.getInstance().show(Notifications.Type.WARNING, "Por favor, informe um valor válido!");

                }
            } else {
                pc.closePopup();
                popupAberto = false;
            }
        }), option);
    }

    /**
     * Aplica um desconto à venda atual.
     * 
     * <p>Exibe um pop-up para o usuário inserir o valor do desconto. 
     * O desconto pode ser um valor fixo ou um percentual sobre 
     * o valor total da venda. Após o desconto ser aplicado, 
     * o valor total da venda é atualizado.</p>
     */
    private void aplicarDesconto() {
        AplicarDesconto telaCupom = new AplicarDesconto();
        DefaultOption option = new DefaultOption() {
            @Override
            public boolean closeWhenClickOutside() {
                return true;
            }
        };
        String actions[] = new String[]{"Cancelar", "Aplicar"};
        GlassPanePopup.showPopup(new SimplePopupBorder(telaCupom, "Aplicar Desconto", actions, (pc, i) -> {
            popupAberto = true;
            if (i == 1) {
                // desconto
                try {
                    porcentagemDesconto = telaCupom.getDesconto();
                    calcularTotal();
                    pc.closePopup();
                    popupAberto = false;
                    Notifications.getInstance().show(Notifications.Type.SUCCESS, "Desconto aplicado com sucesso!");
                    
                } catch (Exception ex) {
                    Notifications.getInstance().show(Notifications.Type.WARNING, ex.getMessage());
                    
                }
            } else {
                pc.closePopup();
                popupAberto = false;
                
            }
        }), option);
    }
    
    /**
     * Calcula o valor do desconto com base no subtotal e na porcentagem de desconto.
     */
    private void calcularDesconto(){
        BigDecimal subTotal = Util.toBigDecimal(txtSubTotal.getText());
        BigDecimal valorDesconto = subTotal.multiply(BigDecimal.valueOf(porcentagemDesconto));
        txtDesconto.setText(String.format("R$ %.2f", valorDesconto));
    }

    /**
     * Finaliza a venda em andamento.
     * 
     * <p>Se houver uma venda em andamento, os itens da venda 
     * serão enviados para o servidor para processamento. 
     * Se a venda for finalizada com sucesso, a tela será 
     * limpa para uma nova venda. Caso contrário, uma 
     * mensagem de erro será exibida.</p>
     */
    private void finalizarVenda() {
        if (vendaIniciou) {
            if (cboPagamento.getSelectedItem().equals("SELECIONE")) {
                Notifications.getInstance().show(Notifications.Type.WARNING, "Selecione uma forma de pagamento.");
                return;
            }
            CriarPedidoDTO pedidoDTO = new CriarPedidoDTO();
            pedidoDTO.setClienteId(cliente.getId());

            BigDecimal valorTotal = Util.toBigDecimal(txtTotal.getText());
            pedidoDTO.setValorTotal(valorTotal);

            Set<CriarItemPedidoDTO> itensPedido = new HashSet<>();
            for (int i = 0; i < tabela.getRowCount(); i++) {
                Long produtoId = Long.valueOf(tabela.getValueAt(i, 1).toString()); 
                Integer quantidade = Integer.parseInt(tabela.getValueAt(i, 4).toString()); 

                CriarItemPedidoDTO itemPedidoDTO = new CriarItemPedidoDTO(produtoId, quantidade);
                itensPedido.add(itemPedidoDTO);
            }
            pedidoDTO.setItens(itensPedido);

            try {
                new PedidoService().criarPedido(pedidoDTO); 
                Notifications.getInstance().show(Notifications.Type.SUCCESS, "Venda realizada com sucesso!");
                limparVenda();

            } catch (FeignException.UnprocessableEntity ex) {
                String mensagemErro = extrairMensagemDeErro(ex.contentUTF8()); 
                Notifications.getInstance().show(Notifications.Type.WARNING, mensagemErro);
            }
        } else {
            Notifications.getInstance().show(Notifications.Type.ERROR, "Você não possui nenhuma venda iniciada!");
        }
    }

    /**
     * Calcula o troco a ser dado ao cliente, com base no valor total da venda e no valor pago em dinheiro.
     */
    private void calcularTroco() {
        double total = Double.parseDouble(txtTotal.getText().replace("R$", "").replace(",", "."));
        double dinheiro = Double.parseDouble(txtDinheiro.getText().replace("R$", "").replace(",", "."));
        double troco = Math.max(0, dinheiro - total);
        txtTroco.setText(String.format("R$ %.2f", troco));
    }
    
    /**
     * Calcula e atualiza o valor total da venda, incluindo o desconto (se houver).
     */
    private void calcularTotal() {
    BigDecimal subtotal = BigDecimal.ZERO;

        for (int i = 0; i < tabela.getRowCount(); i++) {
            Object valorSubtotal = tabela.getValueAt(i, 5); 

            if (valorSubtotal != null) {
                try {
                    BigDecimal valorLinha = Util.toBigDecimal(valorSubtotal);
                    subtotal = subtotal.add(valorLinha);
                } catch (NumberFormatException ex) {
                    Notifications.getInstance().show(Notifications.Type.WARNING, "Formato de número inválido na linha " + i + ": " + ex.getMessage());
                }
            }
        }
        calcularDesconto();
        txtSubTotal.setText(String.format("R$ %.2f", subtotal));
        BigDecimal desconto = Util.toBigDecimal(txtDesconto.getText());
        BigDecimal total = subtotal.subtract(desconto);
        txtTotal.setText(String.format("R$ %.2f", total));
    }
    
    /**
     * Remove todos os itens da tabela de produtos da venda.
     */
    private void limparTabela() {
        DefaultTableModel model = (DefaultTableModel) tabela.getModel();
        model.setRowCount(0);
    }
    
    /**
     * Atualiza o valor do subtotal de uma linha específica na tabela de itens da venda.
     * 
     * @param linha O índice da linha a ser atualizada.
     */
    private void atualizarSubtotalTabela(int linha) {
        Object colunaPreco = tabela.getValueAt(linha, 3).toString().replace("R$ ", "");
        BigDecimal preco = Util.toBigDecimal(colunaPreco);
        
        int quantidade = Integer.parseInt(tabela.getValueAt(linha, 4).toString()); 
        BigDecimal subtotal = preco.multiply(new BigDecimal(quantidade)); 

        tabela.setValueAt(String.format("R$ %.2f", subtotal), linha, 5); 
        calcularTotal();
    }
    
    /**
     * Adiciona um produto à tabela de itens da venda.
     * 
     * @param produto O produto a ser adicionado.
     */
    private void adicionarProdutoNaTabela(Produto produto) {
        DefaultTableModel model = (DefaultTableModel) tabela.getModel();

        model.addRow(new Object[]{
            false,
            produto.getId(),
            produto.getDescricao(),
            String.format("R$ %.2f", produto.getPreco()),
            1,
            String.format("R$ %.2f", produto.getPreco())
        });

        tabela.getModel().addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int linha = e.getFirstRow();
                int coluna = e.getColumn();
                if (coluna == 4) {
                    atualizarSubtotalTabela(linha);
                }
            }
        });
        calcularTotal();
        txtInformacoes.setText(produto.getDescricao());
    }
    
    /**
     * Verifica se um produto já foi adicionado à tabela de itens da venda.
     * 
     * @param produtoId O ID do produto a ser verificado.
     * @return true se o produto já estiver na tabela, false caso contrário.
     */
    private boolean produtoJaAdicionado(Long produtoId) {
        if (tabela.getRowCount() == 0) {
            return false;
        }

        for (int i = 0; i < tabela.getRowCount(); i++) {
            Long idNaTabela = Long.valueOf(tabela.getValueAt(i, 1).toString());
            if (produtoId.equals(idNaTabela)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Incrementa a quantidade de um produto na tabela de itens da venda.
     * 
     * @param produtoId O ID do produto cuja quantidade deve ser incrementada.
     */
    private void incrementarQuantidadeProduto(Long produtoId) {
        for (int i = 0; i < tabela.getRowCount(); i++) {
            Long idNaTabela = Long.valueOf(tabela.getValueAt(i, 1).toString()); 
            if (produtoId.equals(idNaTabela)) {
                int quantidade = Integer.parseInt(tabela.getValueAt(i, 4).toString());
                tabela.setValueAt(++quantidade, i, 4); 
                
                atualizarSubtotalTabela(i);
                
                String descricao = tabela.getValueAt(i, 2).toString();
                txtInformacoes.setText(descricao);
                break; 
            }
        }
    }
}