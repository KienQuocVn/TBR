package View;

import Model.User;
import io.github.cdimascio.dotenv.Dotenv;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class BackupData extends JPanel {

  // Load .env
  Dotenv dotenv = Dotenv.configure().load();
  String dbUrl = dotenv.get("DB_URL");
  String dbUsername = dotenv.get("DB_USERNAME");
  String dbPassword = dotenv.get("DB_PASSWORD");
  String dbName = dotenv.get("DB_NAME"); // Tên database, để restore

  private User currentUser;
  JLabel footerLabel;

  private JTextField txtBackupPath, txtRestoreFile;
  private JButton btnBrowseBackup, btnBackup, btnBrowseRestore, btnRestore;
  private JLabel lblStatus;

  public BackupData(User user, JLabel footer) {
    this.currentUser = user;
    this.footerLabel = footer;


    setLayout(new BorderLayout(10, 10));
    setPreferredSize(new Dimension(700, 250));

    JLabel title = new JLabel("Quản Lý Sao Lưu và Phục Hồi Dữ Liệu");
    title.setFont(new Font("Arial", Font.BOLD, 20));
    title.setHorizontalAlignment(SwingConstants.CENTER);
    add(title, BorderLayout.NORTH);

    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new GridLayout(2, 1, 10, 10));

    // ----- Backup Panel -----
    JPanel backupPanel = new JPanel(new BorderLayout(5,5));
    backupPanel.setBorder(BorderFactory.createTitledBorder("Sao Lưu"));

    txtBackupPath = new JTextField();
    btnBrowseBackup = new JButton("Chọn thư mục");
    btnBackup = new JButton("Backup");

    JPanel pathBackupPanel = new JPanel(new BorderLayout(5,5));
    pathBackupPanel.add(txtBackupPath, BorderLayout.CENTER);
    pathBackupPanel.add(btnBrowseBackup, BorderLayout.EAST);

    backupPanel.add(pathBackupPanel, BorderLayout.NORTH);
    backupPanel.add(btnBackup, BorderLayout.CENTER);

    // ----- Restore Panel -----
    JPanel restorePanel = new JPanel(new BorderLayout(5,5));
    restorePanel.setBorder(BorderFactory.createTitledBorder("Phục Hồi"));

    txtRestoreFile = new JTextField();
    btnBrowseRestore = new JButton("Chọn file .bak");
    btnRestore = new JButton("Restore");

    JPanel pathRestorePanel = new JPanel(new BorderLayout(5,5));
    pathRestorePanel.add(txtRestoreFile, BorderLayout.CENTER);
    pathRestorePanel.add(btnBrowseRestore, BorderLayout.EAST);

    restorePanel.add(pathRestorePanel, BorderLayout.NORTH);
    restorePanel.add(btnRestore, BorderLayout.CENTER);

    centerPanel.add(backupPanel);
    centerPanel.add(restorePanel);

    add(centerPanel, BorderLayout.CENTER);

    lblStatus = new JLabel("Trạng thái: Chưa thực hiện");
    lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
    add(lblStatus, BorderLayout.SOUTH);

    // --- Action backup ---
    btnBrowseBackup.addActionListener(e -> {
      JFileChooser chooser = new JFileChooser();
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        txtBackupPath.setText(chooser.getSelectedFile().getAbsolutePath());
      }
    });

    btnBackup.addActionListener(e -> backupDatabase());

    // --- Action restore ---
    btnBrowseRestore.addActionListener(e -> {
      JFileChooser chooser = new JFileChooser();
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Backup File (*.bak)", "bak"));
      if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        txtRestoreFile.setText(chooser.getSelectedFile().getAbsolutePath());
      }
    });

    btnRestore.addActionListener(e -> restoreDatabase());
  }

  private void backupDatabase() {
    String path = txtBackupPath.getText();
    if (path.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Vui lòng chọn thư mục để lưu backup.", "Lỗi", JOptionPane.ERROR_MESSAGE);
      return;
    }

    String backupFile = path + File.separator + "backup_" + System.currentTimeMillis() + ".bak";
    String sql = "BACKUP DATABASE [" + dbName + "] TO DISK = '" + backupFile + "' WITH FORMAT, INIT, NAME = 'Full Backup'";

    try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
         Statement stmt = conn.createStatement()) {

      stmt.execute(sql);
      lblStatus.setText("Sao lưu thành công: " + backupFile);
      footerLabel.setText("Sao lưu thành công : "+ dbName);
      JOptionPane.showMessageDialog(this, "Sao lưu thành công!\nFile: " + backupFile, "Thành công", JOptionPane.INFORMATION_MESSAGE);

    } catch (SQLException ex) {
      ex.printStackTrace();
      lblStatus.setText("Sao lưu thất bại!");
      JOptionPane.showMessageDialog(this, "Lỗi khi sao lưu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void restoreDatabase() {
    String filePath = txtRestoreFile.getText();
    if (filePath.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Vui lòng chọn file .bak để restore.", "Lỗi", JOptionPane.ERROR_MESSAGE);
      return;
    }

    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost;databaseName=warehouse_management;encrypt=false", dbUsername, dbPassword);
         Statement stmt = conn.createStatement()) {

      stmt.execute("ALTER DATABASE [" + dbName + "] SET SINGLE_USER WITH ROLLBACK IMMEDIATE;");
      stmt.execute("RESTORE DATABASE [" + dbName + "] FROM DISK = '" + filePath + "' WITH REPLACE;");
      stmt.execute("ALTER DATABASE [" + dbName + "] SET MULTI_USER;");

      lblStatus.setText("Restore thành công từ: " + filePath);

      JOptionPane.showMessageDialog(this, "Restore thành công. Vui lòng chạy lại chương trình!", "Thành công", JOptionPane.INFORMATION_MESSAGE);

      System.exit(0);
    } catch (SQLException ex) {
      ex.printStackTrace();
      lblStatus.setText("Restore thất bại!");
      JOptionPane.showMessageDialog(this, "Lỗi khi restore: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
  }



}
