package cz.projektant_pata.pg4.__FinalProject.admin_panel;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class AdminFrame extends JFrame {

    @Autowired
    private CategoryPanel categoryPanel;

    @Autowired
    private ProductPanel productPanel;

    @Autowired
    private OrderPanel orderPanel;

    private JTabbedPane tabbedPane;

    public AdminFrame() {
        setTitle("Admin Panel - Brainrot Food Management 🔥");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();
        add(tabbedPane);
    }

    @PostConstruct
    private void init() {
        SwingUtilities.invokeLater(() -> {
            tabbedPane.addTab("📁 Kategorie", categoryPanel);
            tabbedPane.addTab("🍔 Produkty", productPanel);
            tabbedPane.addTab("📦 Objednávky", orderPanel);

            tabbedPane.revalidate();
            tabbedPane.repaint();
        });
    }
}
