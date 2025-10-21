package Utils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.List;
import java.util.function.Supplier;

public class ComboBoxUtils {

  public static JComboBox<String> createSuggestionComboBox(Supplier<List<String>> dataSupplier) {
    // Lấy dữ liệu lần đầu từ DB
    List<String> allItems = dataSupplier.get();
    JComboBox<String> comboBox = new JComboBox<>(new DefaultComboBoxModel<>(allItems.toArray(new String[0])));
    comboBox.setEditable(true);
    comboBox.setSelectedItem(null); // ✅ luôn rỗng khi khởi tạo
    comboBox.setSelectedIndex(-1);

    JTextField textField = (JTextField) comboBox.getEditor().getEditorComponent();

    textField.getDocument().addDocumentListener(new DocumentListener() {
      private Timer timer;
      private boolean isUpdating = false;

      @Override
      public void insertUpdate(DocumentEvent e) { scheduleUpdate(); }
      @Override
      public void removeUpdate(DocumentEvent e) { scheduleUpdate(); }
      @Override
      public void changedUpdate(DocumentEvent e) { scheduleUpdate(); }

      private void scheduleUpdate() {
        if (isUpdating) return;
        if (timer != null && timer.isRunning()) timer.stop();

        timer = new Timer(300, e -> updateSuggestions(textField.getText(), comboBox));
        timer.setRepeats(false);
        timer.start();
      }

      private void updateSuggestions(String input, JComboBox<String> comboBox) {
        SwingUtilities.invokeLater(() -> {
          isUpdating = true;

          List<String> allItems = dataSupplier.get();
          DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();

          if (input.isEmpty()) {
            allItems.forEach(model::addElement);
            comboBox.setModel(model);

            // ✅ giữ rỗng khi user xoá hết
            comboBox.setSelectedItem(null);
            comboBox.setSelectedIndex(-1);
            textField.setText("");
          } else {
            allItems.stream()
                    .filter(item -> item.toLowerCase().contains(input.toLowerCase()))
                    .forEach(model::addElement);
            comboBox.setModel(model);

            comboBox.setSelectedItem(input);
            textField.setText(input);
          }

          if (model.getSize() > 0) comboBox.showPopup();
          else comboBox.hidePopup();

          isUpdating = false;
        });
      }
    });

    // Khi click mũi tên ▼ nếu editor đang rỗng → show toàn bộ
    comboBox.addActionListener(e -> {
      if (comboBox.isPopupVisible() && textField.getText().isEmpty()) {
        SwingUtilities.invokeLater(() -> {
          List<String> allItems2 = dataSupplier.get();
          DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(allItems2.toArray(new String[0]));
          comboBox.setModel(model);

          comboBox.setSelectedItem(null);
          comboBox.setSelectedIndex(-1);
          textField.setText(""); // ✅ ép rỗng

          comboBox.showPopup();
        });
      }
    });

    return comboBox;
  }
}
