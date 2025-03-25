package View;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
  private CardLayout cardLayout;
  private JPanel contentPanel;

  public MainFrame() {
    setTitle("Phần Mềm Cân Lớp TBR");
    setSize(1542, 800);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());

    // Khởi tạo thanh menu
    JMenuBar menuBar = new JMenuBar();
    menuBar.setLayout(new FlowLayout(FlowLayout.LEFT));

    JMenuItem menuManagerCode = new JMenuItem("Manager Code");
    JMenuItem menuManagerReport = new JMenuItem("Manager Report");
    JMenuItem menuUser = new JMenuItem("View.User");
    JMenuItem menuBackupData = new JMenuItem("Backup Data");

    menuBar.add(menuManagerCode);
    menuBar.add(menuManagerReport);
    menuBar.add(menuUser);
    menuBar.add(menuBackupData);
    setJMenuBar(menuBar);

    // Khởi tạo panel chứa nội dung với CardLayout
    cardLayout = new CardLayout();
    contentPanel = new JPanel(cardLayout);

    // Thêm các trang vào CardLayout
    contentPanel.add(new ManagerCode(), "View.ManagerCode");
    contentPanel.add(new ManagerReport(), "View.ManagerReport");
    contentPanel.add(new User(), "View.User");
    contentPanel.add(new BackupData(), "View.BackupData");

    add(contentPanel, BorderLayout.CENTER);

    // Xử lý sự kiện chuyển trang
    menuManagerCode.addActionListener(e -> cardLayout.show(contentPanel, "View.ManagerCode"));
    menuManagerReport.addActionListener(e -> cardLayout.show(contentPanel, "View.ManagerReport"));
    menuUser.addActionListener(e -> cardLayout.show(contentPanel, "View.User"));
    menuBackupData.addActionListener(e -> cardLayout.show(contentPanel, "View.BackupData"));

    setVisible(true);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(MainFrame::new);
  }
}
