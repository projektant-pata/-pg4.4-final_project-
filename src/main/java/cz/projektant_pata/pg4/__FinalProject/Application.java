package cz.projektant_pata.pg4.__FinalProject;

import cz.projektant_pata.pg4.__FinalProject.admin_panel.AdminFrame;
import cz.projektant_pata.pg4.__FinalProject.preparation_panel.PreparationFrame;
import cz.projektant_pata.pg4.__FinalProject.pickup_panel.PickupFrame;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = new SpringApplicationBuilder(Application.class)
				.headless(false)
				.run(args);

		EventQueue.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}

			JCheckBox adminCheckBox = new JCheckBox("Admin Panel", false);
			JCheckBox prepCheckBox = new JCheckBox("Přípravna", false);
			JCheckBox pickupCheckBox = new JCheckBox("Výdej", true);

			JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
			panel.add(new JLabel("Vyberte okna k otevření:"));
			panel.add(adminCheckBox);
			panel.add(prepCheckBox);
			panel.add(pickupCheckBox);

			int result = JOptionPane.showConfirmDialog(null, panel,
					"Brainrot Food - Spuštění",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);

			if (result == JOptionPane.OK_OPTION) {
				int xOffset = 50;

				if (adminCheckBox.isSelected()) {
					AdminFrame adminFrame = ctx.getBean(AdminFrame.class);
					adminFrame.setLocation(xOffset, 50);
					adminFrame.setVisible(true);
					xOffset += adminFrame.getWidth() + 10;
				}

				if (prepCheckBox.isSelected()) {
					PreparationFrame preparationFrame = ctx.getBean(PreparationFrame.class);
					preparationFrame.setLocation(xOffset, 50);
					preparationFrame.setVisible(true);
					xOffset += preparationFrame.getWidth() + 10;
				}

				if (pickupCheckBox.isSelected()) {
					PickupFrame pickupFrame = ctx.getBean(PickupFrame.class);
					pickupFrame.setLocation(xOffset, 50);
					pickupFrame.setVisible(true);
				}
			}
		});

		System.out.println("✅ Web server: http://localhost:8080");
		System.out.println("✅ GUI spuštěno");
	}
}
