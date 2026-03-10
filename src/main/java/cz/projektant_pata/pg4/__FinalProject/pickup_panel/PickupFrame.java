package cz.projektant_pata.pg4.__FinalProject.pickup_panel;

import cz.projektant_pata.pg4.__FinalProject.event.OrderCompletedEvent;
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
public class PickupFrame extends JFrame {

    @Autowired
    private OrderService orderService;

    private DefaultTableModel preparingTableModel;
    private JTable preparingTable;

    private DefaultTableModel readyTableModel;
    private JTable readyTable;

    private JTextArea detailsArea;

    public PickupFrame() {
        setTitle("📦 Výdej - Brainrot Food");
        setSize(1200, 800);
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
        headerPanel.setBackground(new Color(33, 150, 243));
        JLabel titleLabel = new JLabel("📦 VÝDEJ OBJEDNÁVEK");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setDividerLocation(700);

        JPanel tablesPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        tablesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel preparingPanel = createPreparingPanel();
        tablesPanel.add(preparingPanel);

        JPanel readyPanel = createReadyPanel();
        tablesPanel.add(readyPanel);

        mainSplit.setLeftComponent(tablesPanel);

        JPanel detailPanel = createDetailPanel();
        mainSplit.setRightComponent(detailPanel);

        add(mainSplit, BorderLayout.CENTER);

        loadOrders();
    }

    private JPanel createPreparingPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.ORANGE, 2),
                "🍳 V PŘÍPRAVĚ",
                0, 0, new Font("Arial", Font.BOLD, 14), Color.ORANGE
        ));

        String[] columns = {"ID", "Čas", "Položek", "Cena"};
        preparingTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        preparingTable = new JTable(preparingTableModel);
        preparingTable.setFont(new Font("Arial", Font.PLAIN, 13));
        preparingTable.setRowHeight(25);
        preparingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        preparingTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showOrderDetails(preparingTable, preparingTableModel);
            }
        });

        JScrollPane scrollPane = new JScrollPane(preparingTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createReadyPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GREEN, 2),
                "✅ PŘIPRAVENO K VÝDEJI",
                0, 0, new Font("Arial", Font.BOLD, 14), Color.GREEN.darker()
        ));

        String[] columns = {"ID", "Čas", "Položek", "Cena"};
        readyTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        readyTable = new JTable(readyTableModel);
        readyTable.setFont(new Font("Arial", Font.PLAIN, 13));
        readyTable.setRowHeight(25);
        readyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        readyTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showOrderDetails(readyTable, readyTableModel);
            }
        });

        JScrollPane scrollPane = new JScrollPane(readyTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton pickupButton = new JButton("📦 VYDAT OBJEDNÁVKU");
        pickupButton.setFont(new Font("Arial", Font.BOLD, 14));
        pickupButton.setBackground(new Color(76, 175, 80));
        pickupButton.setForeground(Color.WHITE);
        pickupButton.setFocusPainted(false);
        pickupButton.addActionListener(e -> pickupOrder());
        panel.add(pickupButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel detailLabel = new JLabel("📋 Detail objednávky:");
        detailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(detailLabel, BorderLayout.NORTH);

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        detailsArea.setBackground(new Color(245, 245, 245));
        JScrollPane detailScroll = new JScrollPane(detailsArea);
        panel.add(detailScroll, BorderLayout.CENTER);

        return panel;
    }

    private void loadOrders() {
        preparingTableModel.setRowCount(0);
        readyTableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        orderService.findByStatus("PREPARING").forEach(order -> {
            preparingTableModel.addRow(new Object[]{
                    order.getId(),
                    order.getCreatedAt().format(formatter),
                    order.getOrderItems().size(),
                    String.format("%.2f Kč", order.getPrice())
            });
        });

        orderService.findByStatus("READY").forEach(order -> {
            readyTableModel.addRow(new Object[]{
                    order.getId(),
                    order.getCreatedAt().format(formatter),
                    order.getOrderItems().size(),
                    String.format("%.2f Kč", order.getPrice())
            });
        });
    }

    private void showOrderDetails(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            detailsArea.setText("");
            return;
        }

        Long orderId = (Long) model.getValueAt(selectedRow, 0);
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

    private void pickupOrder() {
        int selectedRow = readyTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vyberte objednávku k vydání!");
            return;
        }

        Long orderId = (Long) readyTableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Vydat objednávku #" + orderId + " zákazníkovi?",
                "Potvrzení výdeje",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            orderService.completeOrder(orderId);
            JOptionPane.showMessageDialog(this, "Objednávka vydána! 📦");
        }
    }

    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        if ("PREPARING".equals(event.getOrder().getStatus())) {
            SwingUtilities.invokeLater(() -> {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                Order order = event.getOrder();

                preparingTableModel.addRow(new Object[]{
                        order.getId(),
                        order.getCreatedAt().format(formatter),
                        order.getOrderItems().size(),
                        String.format("%.2f Kč", order.getPrice())
                });

                Toolkit.getDefaultToolkit().beep();
            });
        }
    }

    @EventListener
    public void onOrderReady(OrderReadyEvent event) {
        SwingUtilities.invokeLater(() -> {
            Long orderId = event.getOrder().getId();

            for (int i = 0; i < preparingTableModel.getRowCount(); i++) {
                Long id = (Long) preparingTableModel.getValueAt(i, 0);
                if (id.equals(orderId)) {
                    preparingTableModel.removeRow(i);
                    break;
                }
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            Order order = event.getOrder();

            readyTableModel.addRow(new Object[]{
                    order.getId(),
                    order.getCreatedAt().format(formatter),
                    order.getOrderItems().size(),
                    String.format("%.2f Kč", order.getPrice())
            });

            Toolkit.getDefaultToolkit().beep();
            toFront();

            JOptionPane.showMessageDialog(this,
                    "Objednávka #" + orderId + " je připravena k výdeji!",
                    "✅ Připraveno",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }

    @EventListener
    public void onOrderCompleted(OrderCompletedEvent event) {
        SwingUtilities.invokeLater(() -> {
            Long completedId = event.getOrder().getId();

            for (int i = 0; i < readyTableModel.getRowCount(); i++) {
                Long id = (Long) readyTableModel.getValueAt(i, 0);
                if (id.equals(completedId)) {
                    readyTableModel.removeRow(i);
                    detailsArea.setText("");
                    break;
                }
            }
        });
    }
}
