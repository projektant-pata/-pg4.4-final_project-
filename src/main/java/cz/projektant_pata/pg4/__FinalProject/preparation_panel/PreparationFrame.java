package cz.projektant_pata.pg4.__FinalProject.preparation_panel;

import cz.projektant_pata.pg4.__FinalProject.event.OrderCreatedEvent;
import cz.projektant_pata.pg4.__FinalProject.event.OrderReadyEvent;
import cz.projektant_pata.pg4.__FinalProject.shared.entity.Order;
import cz.projektant_pata.pg4.__FinalProject.shared.entity.OrderItem;
import cz.projektant_pata.pg4.__FinalProject.service.OrderService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

@Component
public class PreparationFrame extends JFrame {

    @Autowired
    private OrderService orderService;

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextArea detailsArea;

    public PreparationFrame() {
        setTitle("🍳 Přípravna - Brainrot Food");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    @PostConstruct
    private void init() {
        SwingUtilities.invokeLater(this::initUI);
    }

    private void initUI() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(255, 140, 0));
        JLabel titleLabel = new JLabel("🍳 PŘIPRAVOVANÉ OBJEDNÁVKY");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"ID", "Čas", "Položek", "Cena"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showOrderDetails();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        JButton completeButton = new JButton("✅ DOKONČIT OBJEDNÁVKU");
        completeButton.setFont(new Font("Arial", Font.BOLD, 16));
        completeButton.setBackground(new Color(76, 175, 80));
        completeButton.setForeground(Color.WHITE);
        completeButton.setFocusPainted(false);
        completeButton.addActionListener(e -> completeSelectedOrder());
        leftPanel.add(completeButton, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel detailLabel = new JLabel("📋 Detail objednávky:");
        detailLabel.setFont(new Font("Arial", Font.BOLD, 16));
        rightPanel.add(detailLabel, BorderLayout.NORTH);

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        detailsArea.setBackground(new Color(245, 245, 245));
        JScrollPane detailScroll = new JScrollPane(detailsArea);
        rightPanel.add(detailScroll, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(500);
        add(splitPane, BorderLayout.CENTER);

        loadPreparingOrders();
    }

    private void loadPreparingOrders() {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        orderService.findByStatus("PREPARING").forEach(order -> {
            tableModel.addRow(new Object[]{
                    order.getId(),
                    order.getCreatedAt().format(formatter),
                    order.getOrderItems().size(),
                    String.format("%.2f Kč", order.getPrice())
            });
        });
    }

    private void showOrderDetails() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            detailsArea.setText("");
            return;
        }

        Long orderId = (Long) tableModel.getValueAt(selectedRow, 0);
        orderService.findById(orderId).ifPresent(order -> {
            StringBuilder details = new StringBuilder();
            details.append("═══════════════════════════════════\n");
            details.append(String.format("   OBJEDNÁVKA #%d\n", order.getId()));
            details.append("═══════════════════════════════════\n\n");
            details.append(String.format("Čas: %s\n",
                    order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))));
            details.append(String.format("Status: %s\n\n", order.getStatus()));
            details.append("POLOŽKY:\n");
            details.append("───────────────────────────────────\n");

            for (OrderItem item : order.getOrderItems()) {
                details.append(String.format("%dx  %s\n", item.getCount(), item.getFood()));
                details.append(String.format("    %.2f Kč\n\n", item.getPrice()));
            }

            details.append("───────────────────────────────────\n");
            details.append(String.format("CELKEM: %.2f Kč\n", order.getPrice()));
            details.append("═══════════════════════════════════\n");

            detailsArea.setText(details.toString());
        });
    }

    private void completeSelectedOrder() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vyberte objednávku k dokončení!");
            return;
        }

        Long orderId = (Long) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Dokončit objednávku #" + orderId + "?",
                "Potvrzení",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            orderService.readyOrder(orderId);
            JOptionPane.showMessageDialog(this, "Objednávka dokončena! ✅");
        }
    }

    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        if ("PREPARING".equals(event.getOrder().getStatus())) {
            SwingUtilities.invokeLater(() -> {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                Order order = event.getOrder();

                tableModel.addRow(new Object[]{
                        order.getId(),
                        order.getCreatedAt().format(formatter),
                        order.getOrderItems().size(),
                        String.format("%.2f Kč", order.getPrice())
                });

                //ONI JDOU ZVUKY XDD
                Toolkit.getDefaultToolkit().beep();
                toFront();
                requestFocus();

                JOptionPane.showMessageDialog(this,
                        "Nová objednávka #" + order.getId() + "!",
                        "🔔 Nová objednávka",
                        JOptionPane.INFORMATION_MESSAGE);
            });
        }
    }

    @EventListener
    public void onOrderReady(OrderReadyEvent event) {
        SwingUtilities.invokeLater(() -> {
            Long completedId = event.getOrder().getId();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Long id = (Long) tableModel.getValueAt(i, 0);
                if (id.equals(completedId)) {
                    tableModel.removeRow(i);
                    detailsArea.setText("");
                    break;
                }
            }
        });
    }
}
