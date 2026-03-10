package cz.projektant_pata.pg4.__FinalProject.admin_panel;

import cz.projektant_pata.pg4.__FinalProject.shared.entity.Category;
import cz.projektant_pata.pg4.__FinalProject.service.CategoryService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

@Component
public class CategoryPanel extends JPanel {

    @Autowired
    private CategoryService categoryService;

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField nameField;

    public CategoryPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    // inicializace cuz autowire
    @PostConstruct
    private void init() {
        SwingUtilities.invokeLater(this::initUI);
    }

    // vytvor gui komponenty
    private void initUI() {
        // horni panel s formularem a tlacitky
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formPanel.add(new JLabel("Název kategorie:"));
        nameField = new JTextField(20);
        formPanel.add(nameField);

        JButton addButton = new JButton("➕ Přidat");
        addButton.addActionListener(e -> addCategory());
        formPanel.add(addButton);

        JButton deleteButton = new JButton("🗑️ Smazat vybranou");
        deleteButton.addActionListener(e -> deleteCategory());
        formPanel.add(deleteButton);

        JButton refreshButton = new JButton("🔄 Obnovit");
        refreshButton.addActionListener(e -> loadCategories());
        formPanel.add(refreshButton);

        add(formPanel, BorderLayout.NORTH);

        // tabulka
        String[] columns = {"ID", "Název"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        loadCategories();
    }

    // nacti db do tabulky
    private void loadCategories() {
        System.out.println("loadCategories() voláno");
        System.out.println("categoryService: " + categoryService);

        tableModel.setRowCount(0);

        java.util.List<Category> categories = categoryService.findAll();
        System.out.println("Počet kategorií z DB: " + categories.size());

        categories.forEach(category -> {
            System.out.println("Přidávám kategorii: " + category.getId() + " - " + category.getName());
            tableModel.addRow(new Object[]{
                    category.getId(),
                    category.getName()
            });
        });

        System.out.println("Počet řádků v tabulce: " + tableModel.getRowCount());
    }

    // pridej novou kategorii
    private void addCategory() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Název nesmí být prázdný!");
            return;
        }

        // save db
        Category category = new Category();
        category.setName(name);
        categoryService.save(category);

        // refresh
        nameField.setText("");
        loadCategories();
        JOptionPane.showMessageDialog(this, "Kategorie přidána!");
    }

    // kill kategorie
    private void deleteCategory() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vyberte kategorii ke smazání!");
            return;
        }

        // double-check dialog
        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Opravdu smazat tuto kategorii?",
                "Potvrzení",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                categoryService.deleteById(id);
                loadCategories();
                JOptionPane.showMessageDialog(this, "Kategorie smazána!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Chyba při mazání: " + e.getMessage(),
                        "Chyba",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
