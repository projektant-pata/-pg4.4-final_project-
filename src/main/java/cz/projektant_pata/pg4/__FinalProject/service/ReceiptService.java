package cz.projektant_pata.pg4.__FinalProject.service;

import cz.projektant_pata.pg4.__FinalProject.dto.CartItemDTO;
import cz.projektant_pata.pg4.__FinalProject.dto.ShoppingCartDTO;
import cz.projektant_pata.pg4.__FinalProject.shared.entity.Order;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class ReceiptService {

    private static final String RECEIPTS_DIR = "receipts/";

    public String generateReceipt(Order order, ShoppingCartDTO cart) {
        File dir = new File(RECEIPTS_DIR);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                System.err.println("Nepodařilo se vytvořit složku: " + RECEIPTS_DIR);
            }
        }

        String filename = RECEIPTS_DIR + "receipt_" + order.getId() + "_" +
                System.currentTimeMillis() + ".txt";

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("========================================\n");
            writer.write("         ÚČTENKA - BRAINROT FOOD        \n");
            writer.write("========================================\n\n");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
            writer.write("Číslo objednávky: " + order.getId() + "\n");
            writer.write("Datum: " + order.getCreatedAt().format(formatter) + "\n");
            writer.write("Status: " + order.getStatus() + "\n\n");

            writer.write("----------------------------------------\n");
            writer.write("POLOŽKY:\n");
            writer.write("----------------------------------------\n\n");

            for (CartItemDTO item : cart.getItems()) {
                writer.write(String.format("%dx %s\n",
                        item.getCount(),
                        item.getProduct().getName()));
                writer.write(String.format("   Cena: %.2f Kč\n",
                        item.getProduct().getPrice()));

                if (!item.getSelectedChangeables().isEmpty()) {
                    writer.write("   Přílohy:\n");
                    item.getSelectedChangeables().forEach(ch -> {
                        try {
                            writer.write(String.format("     + %s (%.2f Kč)\n",
                                    ch.getName(), ch.getPrice()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }

                writer.write(String.format("   Celkem: %.2f Kč\n\n",
                        item.getTotalPrice()));
            }

            writer.write("========================================\n");
            writer.write(String.format("CELKOVÁ CENA: %.2f Kč\n",
                    order.getPrice()));
            writer.write("========================================\n\n");
            writer.write("Děkujeme za vaši objednávku! 🔥\n");
            writer.write("Stay sigma, no cap fr fr\n");

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Účtenka uložena: " + new File(filename).getAbsolutePath());

        return filename;
    }
}
