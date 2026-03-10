package cz.projektant_pata.pg4.__FinalProject.admin_panel;

import cz.projektant_pata.pg4.__FinalProject.shared.entity.Order;
import cz.projektant_pata.pg4.__FinalProject.shared.entity.OrderItem;
import cz.projektant_pata.pg4.__FinalProject.service.OrderService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

@Component
public class OrderPanel extends JPanel {

    @Autowired
    private OrderService orderService;

    private JTable orderTable;
    private DefaultTableModel orderTableModel;
    private JTextArea detailArea;

    public OrderPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    // inicializace cuz autowire
    @PostConstruct
    private void init() {
        SwingUtilities.invokeLater(this::initUI);
    }

    private void initUI() {
        // horni panel s tlacitky
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("🔄 Obnovit objednávky");
        refreshButton.addActionListener(e -> loadOrders());
        topPanel.add(refreshButton);

        JButton detailButton = new JButton("📋 Zobrazit detail");
        detailButton.addActionListener(e -> showOrderDetail());
        topPanel.add(detailButton);

        add(topPanel, BorderLayout.NORTH);

        // tabulka
        String[] columns = {"ID", "Datum", "Status", "Cena (Kč)", "Počet položek"};
        orderTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(orderTableModel);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // split panel - nahore tabulka, dole detail
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(new JScrollPane(orderTable));

        detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        splitPane.setBottomComponent(new JScrollPane(detailArea));
        splitPane.setDividerLocation(300);

        add(splitPane, BorderLayout.CENTER);

        loadOrders();
    }

    // nacti db do tabyljky
    private void loadOrders() {
        orderTableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        orderService.findAll().forEach(order -> {
            orderTableModel.addRow(new Object[]{
                    order.getId(),
                    order.getCreatedAt().format(formatter),
                    order.getStatus(),
                    order.getPrice(),
                    order.getOrderItems() != null ? order.getOrderItems().size() : 0
            });
        });
    }

    // deetail objednavky
    private void showOrderDetail() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vyberte objednávku!");
            return;
        }

        // get objednavka a uctenka
        Long orderId = (Long) orderTableModel.getValueAt(selectedRow, 0);
        orderService.findById(orderId).ifPresent(order -> {
            StringBuilder detail = new StringBuilder();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

            detail.append("========================================\n");
            detail.append("         DETAIL OBJEDNÁVKY #").append(order.getId()).append("\n");
            detail.append("========================================\n\n");
            detail.append("Datum: ").append(order.getCreatedAt().format(formatter)).append("\n");
            detail.append("Status: ").append(order.getStatus()).append("\n");
            detail.append("Celková cena: ").append(order.getPrice()).append(" Kč\n\n");
            detail.append("----------------------------------------\n");
            detail.append("POLOŽKY:\n");
            detail.append("----------------------------------------\n\n");

            // vypis polozky objednavky
            if (order.getOrderItems() != null) {
                for (OrderItem item : order.getOrderItems()) {
                    detail.append(String.format("%dx %s\n", item.getCount(), item.getFood()));
                    detail.append(String.format("   Cena: %.2f Kč\n\n", item.getPrice()));
                }
            }

            detailArea.setText(detail.toString());
        });
    }
}
