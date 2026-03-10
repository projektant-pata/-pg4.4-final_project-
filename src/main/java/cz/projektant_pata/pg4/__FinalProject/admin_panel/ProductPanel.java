package cz.projektant_pata.pg4.__FinalProject.admin_panel;

import cz.projektant_pata.pg4.__FinalProject.shared.entity.Category;
import cz.projektant_pata.pg4.__FinalProject.shared.entity.CategoryItem;
import cz.projektant_pata.pg4.__FinalProject.service.CategoryItemService;
import cz.projektant_pata.pg4.__FinalProject.service.CategoryService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;

@Component
public class ProductPanel extends JPanel {

    @Autowired
    private CategoryItemService categoryItemService;

    @Autowired
    private CategoryService categoryService;

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JTextField priceField;
    private JComboBox<Category> categoryCombo;

    public ProductPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    @PostConstruct
    private void init() {
        SwingUtilities.invokeLater(this::initUI);
    }
    private void initUI() {
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));

        formPanel.add(new JLabel("Název produktu:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Cena (Kč):"));
        priceField = new JTextField();
        formPanel.add(priceField);

        formPanel.add(new JLabel("Kategorie:"));
        categoryCombo = new JComboBox<>();
        loadCategoriesToCombo();
        formPanel.add(categoryCombo);

        JButton addButton = new JButton("➕ Přidat produkt");
        addButton.addActionListener(e -> addProduct());
        formPanel.add(addButton);

        JButton deleteButton = new JButton("🗑️ Smazat vybraný");
        deleteButton.addActionListener(e -> deleteProduct());
        formPanel.add(deleteButton);

        add(formPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Název", "Cena (Kč)", "Kategorie"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("🔄 Obnovit");
        refreshButton.addActionListener(e -> {
            loadProducts();
            loadCategoriesToCombo();
        });
        bottomPanel.add(refreshButton);
        add(bottomPanel, BorderLayout.SOUTH);

        loadProducts();
    }

    private void loadCategoriesToCombo() {
        categoryCombo.removeAllItems();
        categoryService.findAll().forEach(categoryCombo::addItem);
    }

    private void loadProducts() {
        tableModel.setRowCount(0);
        categoryItemService.findAll().forEach(item -> {
            tableModel.addRow(new Object[]{
                    item.getId(),
                    item.getName(),
                    item.getPrice(),
                    item.getCategory() != null ? item.getCategory().getName() : "Bez kategorie"
            });
        });
    }

    private void addProduct() {
        String name = nameField.getText().trim();
        String priceStr = priceField.getText().trim();

        if (name.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vyplňte všechna pole!");
            return;
        }

        try {
            BigDecimal price = new BigDecimal(priceStr);
            Category category = (Category) categoryCombo.getSelectedItem();

            CategoryItem item = new CategoryItem();
            item.setName(name);
            item.setPrice(price);
            item.setCategory(category);

            categoryItemService.save(item);

            nameField.setText("");
            priceField.setText("");
            loadProducts();
            JOptionPane.showMessageDialog(this, "Produkt přidán!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Neplatná cena!", "Chyba", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vyberte produkt ke smazání!");
            return;
        }

        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Opravdu smazat tento produkt?",
                "Potvrzení",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            categoryItemService.deleteById(id);
            loadProducts();
            JOptionPane.showMessageDialog(this, "Produkt smazán!");
        }
    }
}
