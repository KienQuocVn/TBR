package View;

import Dao.UserDAO;
import Model.User;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ManagerUser extends JPanel {
  private JTextField idField;
  private JTextField usernameField;
  private JPasswordField passwordField;
  private JTextField fullNameField;
  private JTextField roleBox;
  private JTextField createdAtField;
  private JTextField updatedAtField;
  private JButton updateButton;

  private User currentUser;
  JLabel footerLabel;
  public ManagerUser(User user,JLabel Label) {
    this.currentUser = user;
    this.footerLabel = Label;


    setLayout(new GridBagLayout());
    setBackground(Color.WHITE);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;

    int row = 0;

    // ID
    gbc.gridx = 0; gbc.gridy = row; add(new JLabel("ID:"), gbc);
    idField = new JTextField(15);
    idField.setEditable(false);
    gbc.gridx = 1; add(idField, gbc);

    // Username
    row++;
    gbc.gridx = 0; gbc.gridy = row; add(new JLabel("Tên đăng nhập:"), gbc);
    usernameField = new JTextField(15);
    gbc.gridx = 1; add(usernameField, gbc);

    // Password
    row++;
    gbc.gridx = 0; gbc.gridy = row; add(new JLabel("Mật khẩu:"), gbc);
    passwordField = new JPasswordField(15);
    gbc.gridx = 1; add(passwordField, gbc);

    // Full name
    row++;
    gbc.gridx = 0; gbc.gridy = row; add(new JLabel("Họ tên:"), gbc);
    fullNameField = new JTextField(15);
    gbc.gridx = 1; add(fullNameField, gbc);

    // Role
    row++;
    gbc.gridx = 0; gbc.gridy = row; add(new JLabel("Vai trò:"), gbc);
    roleBox = new JTextField(15);
    roleBox.setEditable(false);
    gbc.gridx = 1; add(roleBox, gbc);

    // CreatedAt
    row++;
    gbc.gridx = 0; gbc.gridy = row; add(new JLabel("Ngày tạo:"), gbc);
    createdAtField = new JTextField(15);
    createdAtField.setEditable(false);
    gbc.gridx = 1; add(createdAtField, gbc);

    // UpdatedAt
    row++;
    gbc.gridx = 0; gbc.gridy = row; add(new JLabel("Ngày cập nhật:"), gbc);
    updatedAtField = new JTextField(15);
    updatedAtField.setEditable(false);
    gbc.gridx = 1; add(updatedAtField, gbc);

    // Nút cập nhật
    row++;
    gbc.gridx = 1; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
    updateButton = new JButton("Cập nhật");
    add(updateButton, gbc);

    // Đổ dữ liệu User vào form
    fillUserData(user);

// Xử lý sự kiện cập nhật
    updateButton.addActionListener(e -> {
      saveUserData(); // cập nhật object currentUser từ form

      try {
        UserDAO userDAO = new UserDAO();
        userDAO.update(currentUser); // gọi DAO cập nhật DB

        JOptionPane.showMessageDialog(this,
                "Cập nhật người dùng thành công!",
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE);
      } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
                "Lỗi khi cập nhật người dùng: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
      }
    });

  }

  // Gán dữ liệu từ User vào form
  private void fillUserData(User user) {
    if (user == null) return;
    idField.setText(user.getId() != null ? user.getId().toString() : "");
    usernameField.setText(user.getUsername());
    passwordField.setText(user.getPasswordHash());
    fullNameField.setText(user.getFullName());
    roleBox.setText(user.getRole());
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    createdAtField.setText(user.getCreatedAt() != null ? user.getCreatedAt().format(formatter) : "");
    updatedAtField.setText(user.getUpdatedAt() != null ? user.getUpdatedAt().format(formatter) : "");
  }

  // Lưu dữ liệu từ form vào User object
  private void saveUserData() {
    currentUser.setUsername(usernameField.getText());
    currentUser.setPasswordHash(new String(passwordField.getPassword()));
    currentUser.setFullName(fullNameField.getText());
    currentUser.setRole(roleBox.getText());
    currentUser.setUpdatedAt(LocalDateTime.now());

    // Cập nhật UI
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    updatedAtField.setText(currentUser.getUpdatedAt().format(formatter));
  }
}
