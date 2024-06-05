package dev.imrob.appvendas.gui;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import dev.imrob.appvendas.gui.cliente.ClienteManager;
import dev.imrob.appvendas.gui.componentes.DesktopPaneCustom;
import dev.imrob.appvendas.gui.componentes.Item;
import dev.imrob.appvendas.gui.pedido.PedidoManager;
import dev.imrob.appvendas.gui.produto.ProdutoManager;
import dev.imrob.appvendas.gui.vendas.PontoDeVenda;
import java.awt.*;
import java.awt.geom.Point2D;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import raven.swing.blur.BlurBackground;
import raven.swing.blur.BlurChild;
import raven.swing.blur.style.*;

public class Menu extends JPanel {

    private DesktopPaneCustom desktopPaneCustom;

    public Menu(DesktopPaneCustom desktopPaneCustom) {
        this.desktopPaneCustom = desktopPaneCustom;
        init();
    }

    private void init() {
        background = new BlurBackground(new ImageIcon(getClass().getResource("/image/imagemfundo1.jpeg")).getImage());
        background.setLayout(new MigLayout("al center center"));
        blurChild = new BlurChild(getStyle());
        blurChild.setLayout(new MigLayout("insets 50,wrap 5,gap 20", "[sg,fill]", "[sg,fill]"));
        background.add(blurChild);

        setLayout(new BorderLayout());

        add(background);

        createMenu();
    }

    private void createMenu() {
        Item botaoClientes = new Item("Clientes", "menu/user.svg");
        Item botaoProdutos = new Item("Produtos", "menu/product.svg");
        Item botaoPedidos = new Item("Pedidos", "menu/pedidos1.svg");
        Item botaoVendas = new Item("Vendas", "menu/carrinho.svg");
        Item botaoEmail = new Item("Email", "menu/gmail.svg");
        Item botaoCalculadora = new Item("Calculadora", "menu/calculator.svg");
        Item botaoConfig = new Item("Configurações", "menu/setting.svg");

        botaoClientes.addActionListener(e -> {
            ClienteManager janela = new ClienteManager();
            aplicarTemaAJanela(janela);
            janela.setVisible(true);
        });
        
        botaoProdutos.addActionListener(e -> {
            ProdutoManager janela = new ProdutoManager();
            aplicarTemaAJanela(janela);
            janela.setVisible(true);
        });
        
        botaoPedidos.addActionListener(e -> {
            PedidoManager janela = new PedidoManager();
            aplicarTemaAJanela(janela);
            janela.setVisible(true);
        });
        
        botaoVendas.addActionListener(e -> {
            PontoDeVenda janela = new PontoDeVenda();
            aplicarTemaAJanela(janela);
            janela.setVisible(true);
        });

        blurChild.add(botaoClientes);
        blurChild.add(botaoProdutos);
        blurChild.add(botaoPedidos);
        blurChild.add(botaoVendas);
        blurChild.add(botaoEmail);
        blurChild.add(botaoCalculadora);
        blurChild.add(botaoConfig);

    }

    private Style getStyle() {
        return new Style()
                .setBlur(30)
                .setBorder(new StyleBorder(30)
                        .setBorderWidth(1.5f)
                        .setOpacity(0.25f)
                        .setBorderColor(new GradientColor(new Color(150, 150, 150), new Color(230, 230, 230), new Point2D.Float(0, 0), new Point2D.Float(0, 1)))
                        .setDropShadow(new StyleDropShadow(new Color(0, 0, 0), 0.2f, new Insets(12, 12, 20, 20)))
                )
                .setOverlay(new StyleOverlay(new Color(255, 255, 255), 0.1f));
    }
    
    private void aplicarTemaAJanela(JFrame janela) {
    try {
        UIManager.setLookAndFeel(new FlatMacLightLaf());
        SwingUtilities.updateComponentTreeUI(janela);
    } catch (UnsupportedLookAndFeelException e) {
        e.printStackTrace(); 
    }
}

    private BlurChild blurChild;
    private BlurBackground background;
}
