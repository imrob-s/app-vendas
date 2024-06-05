
package dev.imrob.appvendas.gui.cliente;

import com.formdev.flatlaf.FlatClientProperties;
import static dev.imrob.appvendas.config.FeignConfig.extrairMensagemDeErro;
import dev.imrob.appvendas.entity.Cliente;
import dev.imrob.appvendas.gui.componentes.CheckBoxTableHeaderRenderer;
import dev.imrob.appvendas.gui.componentes.TableHeaderAlignment;
import dev.imrob.appvendas.service.ClienteService;
import feign.FeignException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import raven.popup.DefaultOption;
import raven.popup.GlassPanePopup;
import raven.popup.component.SimplePopupBorder;
import raven.swing.AvatarIcon;
import raven.toast.Notifications;

/**
 * Tela de gerenciamento de clientes.
 *
 * <p>Esta classe representa a interface gráfica para gerenciar clientes, 
 * fornecendo funcionalidades para:</p>
 * 
 * <ul>
 *  <li>Visualizar a lista de clientes cadastrados.</li>
 *  <li>Cadastrar novos clientes.</li>
 *  <li>Editar clientes existentes.</li>
 *  <li>Excluir clientes.</li>
 *  <li>Pesquisar clientes por nome.</li>
 * </ul>
 * 
 * <p>A tela exibe uma tabela com a lista de clientes, 
 * permitindo a seleção de múltiplos clientes para edição ou exclusão. 
 * A pesquisa de clientes é realizada em tempo real, filtrando a lista 
 * de acordo com o texto digitado no campo de pesquisa.</p>
 * 
 * <p>A classe utiliza componentes da biblioteca Swing para a 
 * construção da interface gráfica e faz uso de notificações 
 * para informar o usuário sobre o andamento das operações.</p>
 * 
 * @author Robson Silva
 */
public class ClienteManager extends javax.swing.JFrame {
    private ClienteService service = new ClienteService();

    /**
     * Construtor da classe ClienteManager.
     * 
     * <p>Inicializa a tela, define configurações personalizadas, 
     * carrega os dados dos clientes na tabela e configura 
     * os componentes visuais da tela.</p>
     */
    public ClienteManager() {
        initComponents();
        init();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        getRootPane().putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, true);
    }
    
    /**
     * Inicializa componentes e configurações da tela.
     * 
     * <p>Esta função realiza as seguintes ações:</p>
     * 
     * <ul>
     *  <li>Configura o GlassPanePopup para exibir pop-ups na tela.</li>
     *  <li>Configura o sistema de notificações para a tela.</li>
     *  <li>Aplica estilos visuais personalizados aos componentes da tela, 
     *  como painéis, tabela, barra de rolagem, botões e campos de texto.</li>
     *  <li>Define ícones e placeholders para campos de texto.</li>
     *  <li>Configura a renderização da tabela, incluindo a adição de 
     *  caixas de seleção para cada linha e o alinhamento do cabeçalho da tabela.</li>
     *  <li>Carrega os dados dos clientes na tabela.</li>
     * </ul>
     */
    private void init() {
        GlassPanePopup.install(this);
        Notifications.getInstance().setJFrame(this);
        painel.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:25;"
                + "background:$Table.background");
        
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

        scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, ""
                + "trackArc:999;"
                + "trackInsets:3,3,3,3;"
                + "thumbInsets:3,3,3,3;"
                + "background:$Table.background;");
        
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
                + "background:$Panel.background;");
        btnEditar.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:rgb(221, 221, 221);");
        btnExcluir.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:rgb(255, 153, 153);");
        btnNovo.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:rgb(117, 230, 181);");
        
        tabela.getColumnModel().getColumn(0).setHeaderRenderer(new CheckBoxTableHeaderRenderer(tabela, 0));
        tabela.getTableHeader().setDefaultRenderer(new TableHeaderAlignment(tabela));
        
        carregarClientes();
    }
    
    /**
     * Carrega os dados dos clientes na tabela.
     * 
     * <p>Obtém a lista de clientes do serviço ClienteService e 
     * adiciona cada cliente como uma nova linha na tabela.</p>
     */
    private void carregarClientes() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabela.getModel();
            if (tabela.isEditing()) {
                tabela.getCellEditor().stopCellEditing();
            }
            model.setRowCount(0);
            List<Cliente> list = service.findAll();
            for (Cliente p : list) {
                model.addRow(p.toTableRow(tabela.getRowCount() + 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Atualiza o filtro da tabela de clientes de acordo com o texto digitado no campo de pesquisa.
     * 
     * <p>Utiliza um RowFilter para filtrar as linhas da tabela 
     * com base em uma expressão regular. A expressão regular 
     * é construída a partir do texto digitado no campo de pesquisa.</p>
     */
    private void atualizarFiltro() {
        DefaultTableModel model = (DefaultTableModel) tabela.getModel();
        var sorter = new TableRowSorter<>(model);
        tabela.setRowSorter(sorter);
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
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        painel = new javax.swing.JPanel();
        scroll = new javax.swing.JScrollPane();
        tabela = new javax.swing.JTable();
        jSeparator1 = new javax.swing.JSeparator();
        txtPesquisar = new javax.swing.JTextField();
        lbTitle = new javax.swing.JLabel();
        btnExcluir = new dev.imrob.appvendas.gui.componentes.ButtonAction();
        btnNovo = new dev.imrob.appvendas.gui.componentes.ButtonAction();
        btnEditar = new dev.imrob.appvendas.gui.componentes.ButtonAction();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        scroll.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        tabela.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "X", "#", "CLIENTE", "LIMITE COMPRAS", "DIA FECHAMENTO FATURA"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false
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
            tabela.getColumnModel().getColumn(1).setMaxWidth(40);
            tabela.getColumnModel().getColumn(3).setMinWidth(120);
            tabela.getColumnModel().getColumn(3).setPreferredWidth(120);
            tabela.getColumnModel().getColumn(3).setMaxWidth(120);
            tabela.getColumnModel().getColumn(4).setMinWidth(180);
            tabela.getColumnModel().getColumn(4).setPreferredWidth(180);
            tabela.getColumnModel().getColumn(4).setMaxWidth(180);
        }

        txtPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPesquisarKeyTyped(evt);
            }
        });

        lbTitle.setText("CLIENTES");

        btnExcluir.setText("Excluir");
        btnExcluir.addActionListener(this::btnExcluirActionPerformed);

        btnNovo.setText("Novo");
        btnNovo.addActionListener(this::btnNovoActionPerformed);

        btnEditar.setText("Editar");
        btnEditar.addActionListener(this::btnEditarActionPerformed);

        org.jdesktop.layout.GroupLayout painelLayout = new org.jdesktop.layout.GroupLayout(painel);
        painel.setLayout(painelLayout);
        painelLayout.setHorizontalGroup(
            painelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(scroll, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 832, Short.MAX_VALUE)
            .add(jSeparator1)
            .add(painelLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(painelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lbTitle)
                    .add(painelLayout.createSequentialGroup()
                        .add(txtPesquisar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(btnNovo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(10, 10, 10)
                        .add(btnEditar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(10, 10, 10)
                        .add(btnExcluir, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(20, 20, 20))
        );
        painelLayout.setVerticalGroup(
            painelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(painelLayout.createSequentialGroup()
                .add(lbTitle)
                .add(10, 10, 10)
                .add(painelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnExcluir, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnEditar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnNovo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtPesquisar))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 10, Short.MAX_VALUE)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(scroll, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 484, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(20, 20, 20)
                .add(painel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(40, 40, 40)
                .add(painel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(20, 20, 20))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Ação executada quando o botão "Novo" é pressionado.
     * 
     * <p>Abre um pop-up para o cadastro de um novo cliente. 
     * Após o preenchimento dos dados do cliente, o usuário 
     * pode salvar o novo cliente ou cancelar a operação.</p>
     * 
     * @param evt O evento de ação do botão.
     */
    private void btnNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoActionPerformed
        CriarCliente criarCliente = new CriarCliente();
        criarCliente.setDados(service, null);
        DefaultOption option = new DefaultOption() {
            @Override
            public boolean closeWhenClickOutside() {
                return true;
            }
        };
        String actions[] = new String[]{"Cancelar", "Salvar"};
        GlassPanePopup.showPopup(new SimplePopupBorder(criarCliente, "Cadastrar Cliente", actions, (pc, i) -> {
            if (i == 1) {
                // save
                try {
                    service.save(criarCliente.getDados());
                    pc.closePopup();
                    Notifications.getInstance().show(Notifications.Type.SUCCESS, "Cliente cadastrado com sucesso!");
                    carregarClientes();
                } catch (ParseException ex) {
                    Notifications.getInstance().show(Notifications.Type.WARNING, "Por favor, informe um valor válido!");
                } catch (FeignException.UnprocessableEntity ex) {
                    String mensagemErro = extrairMensagemDeErro(ex.contentUTF8()); 
                    Notifications.getInstance().show(Notifications.Type.WARNING, mensagemErro);
                }
            } else {
                pc.closePopup();
            }
        }), option);
    }//GEN-LAST:event_btnNovoActionPerformed

    private void txtPesquisarKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarKeyTyped
        atualizarFiltro();
    }//GEN-LAST:event_txtPesquisarKeyTyped

    /**
     * Ação executada quando o botão "Editar" é pressionado.
     * 
     * <p>Permite editar um cliente selecionado na tabela. 
     * Abre um pop-up com os dados do cliente selecionado, 
     * permitindo a edição dos campos. Após a edição, o 
     * usuário pode salvar as alterações ou cancelar a operação.</p>
     * 
     * @param evt O evento de ação do botão.
     */
    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        List<Cliente> list = getSelectedData();
        if (!list.isEmpty()) {
            if (list.size() == 1) {
                Cliente cliente = list.get(0);
                CriarCliente criar = new CriarCliente();
                criar.setDados(service, cliente);
                DefaultOption option = new DefaultOption() {
                    @Override
                    public boolean closeWhenClickOutside() {
                        return true;
                    }
                };
                String actions[] = new String[]{"Cancel", "Atualizar"};
                GlassPanePopup.showPopup(new SimplePopupBorder(criar, "Atualizar Cliente [" + cliente.getNome()+ "]", actions, (pc, i) -> {
                    if (i == 1) {
                        // edit
                        try {
                            Cliente clienteEdit = criar.getDados();
                            clienteEdit.setId(cliente.getId());
                            service.update(clienteEdit);
                            pc.closePopup();
                            Notifications.getInstance().show(Notifications.Type.SUCCESS, "Cliente atualizado com sucesso");
                            carregarClientes();
                        } catch (ParseException ex) {
                            Notifications.getInstance().show(Notifications.Type.WARNING, "Por favor, informe um valor válido!");
                        } catch (FeignException.UnprocessableEntity ex) {
                            String mensagemErro = extrairMensagemDeErro(ex.contentUTF8()); 
                            Notifications.getInstance().show(Notifications.Type.WARNING, mensagemErro);
                        }
                    } else {
                        pc.closePopup();
                    }
                }), option);
            } else {
                Notifications.getInstance().show(Notifications.Type.WARNING, "Por favor, selecione somente um cliente!");
            }
        } else {
            Notifications.getInstance().show(Notifications.Type.WARNING, "Por favor, selecione um cliente para atualizar.");
        }
    }//GEN-LAST:event_btnEditarActionPerformed

    /**
     * Ação executada quando o botão "Excluir" é pressionado.
     * 
     * <p>Exclui os clientes selecionados na tabela. 
     * Uma caixa de diálogo de confirmação é exibida antes da 
     * exclusão. Se o usuário confirmar, os clientes 
     * selecionados serão excluídos.</p>
     * 
     * @param evt O evento de ação do botão.
     */
    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        List<Cliente> list = getSelectedData();
        if (!list.isEmpty()) {
            DefaultOption option = new DefaultOption() {
                @Override
                public boolean closeWhenClickOutside() {
                    return true;
                }
            };
            String actions[] = new String[]{"Sair", "Excluir"};
            JLabel label = new JLabel("Você tem certeza que deseja excluir " + list.size() + " cliente(s) ?");
            label.setBorder(new EmptyBorder(0, 25, 0, 25));
            GlassPanePopup.showPopup(new SimplePopupBorder(label, "Confirmar", actions, (pc, i) -> {
                if (i == 1) {
                    // delete
                    try {
                        for (Cliente d : list) {
                            service.delete(d.getId());
                        }
                        pc.closePopup();
                        Notifications.getInstance().show(Notifications.Type.SUCCESS, "Cliente excluido com sucesso!");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    carregarClientes();
                } else {
                    pc.closePopup();
                }
            }), option);
        } else {
            Notifications.getInstance().show(Notifications.Type.WARNING, "Por favor, selecione um cliente para excluir.");
        }
    }//GEN-LAST:event_btnExcluirActionPerformed

    /**
     * Retorna uma lista com os clientes selecionados na tabela.
     * 
     * @return Uma lista de objetos Cliente contendo os clientes selecionados.
     */
    private List<Cliente> getSelectedData() {
        List<Cliente> list = new ArrayList<>();
        for (int i = 0; i < tabela.getRowCount(); i++) {
            if ((boolean) tabela.getValueAt(i, 0)) {
                Cliente dados = (Cliente) tabela.getValueAt(i, 2);
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
                new ClienteManager().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private dev.imrob.appvendas.gui.componentes.ButtonAction btnEditar;
    private dev.imrob.appvendas.gui.componentes.ButtonAction btnExcluir;
    private dev.imrob.appvendas.gui.componentes.ButtonAction btnNovo;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JPanel painel;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTable tabela;
    private javax.swing.JTextField txtPesquisar;
    // End of variables declaration//GEN-END:variables

}
