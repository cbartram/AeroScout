package ui;

import lombok.Getter;
import model.Configuration;
import model.Symbol;
import model.validation.CombatFilterValidator;
import model.validation.DiscordUrlValidator;
import model.validation.Validation;
import model.validation.ValueFilterValidator;
import org.osbot.L;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static javax.swing.JOptionPane.showMessageDialog;

public class GUI extends JDialog {
	private static final Object mutex = new Object();
	private static volatile GUI instance;

	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JTextField equipmentFilterTextField;
	private JScrollPane equipmentFilterScrollPane;
	private JButton addEquipmentButton;
	private JLabel itemValueLabelTip;
	private DefaultListModel<String> equipmentList = new DefaultListModel<>();
	// populated by the default list model + custom accessor method bc logic is slightly more complex
	private JList<String> equipmentFilterList;

	@Getter
	private JTextField discordUrlTextField;

	@Getter
	private JTextField combatFilterTextField;

	@Getter
	private JTextField combatFilterBetweenTextField;

	@Getter
	private JComboBox<String> combatFilterComboBox;

	@Getter
	private JTextField itemValueFilterTextField;
	private JCheckBox membersCheckbox;
	private JCheckBox pvpWorlds;

	// Grabbing the configuration instance will automatically attempt to load any AeroScout.properties files
	private final Configuration config = Configuration.getInstance();
	private static final List<Validation> validations = new ArrayList<>();
	private final List<String> errorMessages = new ArrayList<>();

	/**
	 * Configuration is implemented as a singleton instance which cannot be instantiated directly. This ensures all
	 * reads and writes to the properties file are performed on the same instance of the class (and subsequently the
	 * same instance of the Properties object). There should never be different properties that are written to disk
	 * from what is loaded in memory.
	 * <p>
	 * i.e The script should always use an edit to a loaded property via the GUI rather than a possibly outdated property
	 * loaded from disk.
	 *
	 * @return Configuration instance.
	 */
	public static GUI getInstance() {
		GUI result = instance;
		if (result == null) {
			synchronized (mutex) {
				result = instance;
				if (result == null)
					instance = result = new GUI();
				// Initialize the validations which will need to be applied to the GUI fields
				validations.addAll(Arrays.asList(
						new CombatFilterValidator(),
						new DiscordUrlValidator(),
						new ValueFilterValidator()
				));
			}
		}
		return result;
	}

	private GUI() {
		$$$setupUI$$$();
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);

		combatFilterComboBox.addItem(Symbol.LESS_THAN.getText());
		combatFilterComboBox.addItem(Symbol.GREATER_THAN.getText());
		combatFilterComboBox.addItem(Symbol.EQUAL.getText());
		combatFilterComboBox.addItem(Symbol.BETWEEN.getText());
		combatFilterComboBox.setSelectedItem(Symbol.LESS_THAN.getText());

		// Add loaded properties to fill in GUI fields
		for (String key : config.getProperties().stringPropertyNames()) {
			// Mapping between property names and GUI field names
			final String value = config.get(key);
			switch (key) {
				case "discord.url":
					this.discordUrlTextField.setText(value);
					break;
				case "filter.combat.symbol":
					this.combatFilterComboBox.setSelectedItem(value);
					if (Symbol.of((String) combatFilterComboBox.getSelectedItem()) == Symbol.BETWEEN) {
						this.combatFilterBetweenTextField.setEnabled(true);
					}
					break;
				case "filter.combat.text.simple":
					this.combatFilterTextField.setText(value);
					break;
				case "filter.combat.text.between":
					this.combatFilterBetweenTextField.setText(value);
					break;
				case "filter.item":
					this.itemValueFilterTextField.setText(value);
					break;
				case "filter.equipment":
					for (String item : value.split(",")) {
						equipmentList.addElement(item);
					}
					break;
				case "world.hopper.members":
					this.membersCheckbox.setSelected(Boolean.parseBoolean(value));
					break;
				case "world.hopper.pvp":
					this.pvpWorlds.setSelected(Boolean.parseBoolean(value));
					break;
				default:
					System.out.println("[WARN] Unknown property: " + key + ". Field mapping cannot be determined.");
			}
		}
		buttonOK.addActionListener(e -> {
			// Run Validations to ensure GUI input is correct
			for (Validation validation : validations) {
				if (!validation.validate(this)) {
					errorMessages.add(validation.getErrorMessage());
				}
			}

			if (errorMessages.size() > 0) {
				showMessageDialog(null, String.join(", ", errorMessages));
			} else {
				close();
			}
			errorMessages.clear();
		});

		buttonCancel.addActionListener(e -> close(false));
		combatFilterComboBox.addActionListener(e -> {
			if (Symbol.of((String) combatFilterComboBox.getSelectedItem()) == Symbol.BETWEEN) {
				combatFilterBetweenTextField.setEnabled(true);
			} else {
				combatFilterBetweenTextField.setEnabled(false);
				combatFilterBetweenTextField.setText("");
			}
		});
		addEquipmentButton.addActionListener(e -> {
			if (equipmentFilterTextField.getText().length() > 0) {
				equipmentList.addElement(equipmentFilterTextField.getText());
				equipmentFilterTextField.setText("");
			}
		});

		// call onCancel() when cross is clicked
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				close(false);
			}
		});

		contentPane.registerKeyboardAction(e -> close(false), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		contentPane.registerKeyboardAction(e -> {
			((DefaultListModel) equipmentFilterList.getModel()).remove(equipmentFilterList.getSelectedIndex());
		}, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

	/**
	 * Opens the GUI and shows it to the users.
	 */
	public void open() {
		pack();
		setVisible(true);
	}

	/**
	 * Boolean which returns true if the GUI dialog is open
	 * and false otherwise
	 *
	 * @return Boolean
	 */
	public boolean isOpen() {
		return isVisible();
	}

	/**
	 * Closes the GUI and persists the configuration properties file to disk.
	 */
	public void close() {
		close(true);
	}

	/**
	 * Saves the new configuration to disk in the AeroScout.properties file, closes the GUI
	 * and releases all of the native screen resources used by this Window, its subcomponents,
	 * and all of its owned children.
	 */
	private void close(boolean saveConfig) {
		if (saveConfig) config.save(this);
		setVisible(false);
		dispose();
	}

	public boolean useMembersWorlds() {
		return this.membersCheckbox.isSelected();
	}

	public boolean usePvpWorlds() {
		return this.pvpWorlds.isSelected();
	}


	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		createUIComponents();
		contentPane = new JPanel();
		contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 3, 3));
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(7, 6, new Insets(0, 0, 0, 0), -1, -1));
		contentPane.add(panel1);
		final JLabel label1 = new JLabel();
		label1.setText("Discord URL");
		label1.setToolTipText("The discord webhook to post player scout messages to.");
		panel1.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		discordUrlTextField = new JTextField();
		panel1.add(discordUrlTextField, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final JLabel label2 = new JLabel();
		label2.setText("Combat Filter");
		label2.setToolTipText("Scout for players within a specific combat bracket");
		panel1.add(label2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		combatFilterComboBox = new JComboBox();
		panel1.add(combatFilterComboBox, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label3 = new JLabel();
		label3.setText("Item Value Filter");
		label3.setToolTipText("Scout for players wearing equipment of at least a value specified here.");
		panel1.add(label3, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		itemValueFilterTextField = new JTextField();
		panel1.add(itemValueFilterTextField, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		equipmentFilterScrollPane = new JScrollPane();
		equipmentFilterScrollPane.setVerticalScrollBarPolicy(22);
		panel1.add(equipmentFilterScrollPane, new com.intellij.uiDesigner.core.GridConstraints(3, 3, 3, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		equipmentFilterList.setLayoutOrientation(1);
		equipmentFilterList.setSelectionMode(0);
		equipmentFilterScrollPane.setViewportView(equipmentFilterList);
		equipmentFilterTextField = new JTextField();
		panel1.add(equipmentFilterTextField, new com.intellij.uiDesigner.core.GridConstraints(3, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		addEquipmentButton = new JButton();
		addEquipmentButton.setText("Add");
		panel1.add(addEquipmentButton, new com.intellij.uiDesigner.core.GridConstraints(4, 2, 2, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		combatFilterTextField = new JTextField();
		combatFilterTextField.setText("");
		panel1.add(combatFilterTextField, new com.intellij.uiDesigner.core.GridConstraints(1, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		combatFilterBetweenTextField = new JTextField();
		combatFilterBetweenTextField.setEnabled(false);
		panel1.add(combatFilterBetweenTextField, new com.intellij.uiDesigner.core.GridConstraints(1, 5, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(60, 30), null, 0, false));
		final JLabel label4 = new JLabel();
		label4.setText("And");
		panel1.add(label4, new com.intellij.uiDesigner.core.GridConstraints(1, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JPanel panel2 = new JPanel();
		panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
		panel1.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(6, 5, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, new Dimension(161, 38), null, 0, false));
		final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
		panel2.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JPanel panel3 = new JPanel();
		panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
		panel2.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		buttonCancel = new JButton();
		buttonCancel.setText("Cancel");
		panel3.add(buttonCancel, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		buttonOK = new JButton();
		buttonOK.setText("OK");
		panel3.add(buttonOK, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		itemValueLabelTip = new JLabel();
		itemValueLabelTip.setText("(tip: You can add K,M, or B to the end for GP denominations)");
		panel1.add(itemValueLabelTip, new com.intellij.uiDesigner.core.GridConstraints(2, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
		panel1.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final JLabel label5 = new JLabel();
		label5.setText("Equipment Filter");
		label5.setToolTipText("Scout for players with specific equipment listed here.");
		panel1.add(label5, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label6 = new JLabel();
		label6.setText("World Hopper");
		label6.setToolTipText("Scout for players with specific equipment listed here.");
		panel1.add(label6, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		membersCheckbox = new JCheckBox();
		membersCheckbox.setText("hop to only members worlds");
		panel1.add(membersCheckbox, new com.intellij.uiDesigner.core.GridConstraints(6, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		pvpWorlds = new JCheckBox();
		pvpWorlds.setText("hop to pvp worlds");
		panel1.add(pvpWorlds, new com.intellij.uiDesigner.core.GridConstraints(6, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JSeparator separator1 = new JSeparator();
		contentPane.add(separator1);
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return contentPane;
	}

	private void createUIComponents() {
		equipmentFilterList = new JList<>(equipmentList);
		equipmentFilterList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		equipmentFilterList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		equipmentFilterList.setVisibleRowCount(-1);
	}

	public List<String> getEquipmentFilterList() {
		List<String> tempList = new ArrayList<>();
		for (int i = 0; i < equipmentFilterList.getModel().getSize(); i++) {
			String equipment = equipmentFilterList.getModel().getElementAt(i).trim().replaceAll(" ", "_");
			if (!equipment.equals("") && equipment.length() > 0)
				tempList.add(equipmentFilterList.getModel().getElementAt(i).toUpperCase(Locale.ROOT));
		}
		return tempList;
	}
}
