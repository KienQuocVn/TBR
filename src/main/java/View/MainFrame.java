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

    JMenuItem menuManagerCode = new JMenuItem("Manager Code", loadIcon("images/code.png", 20, 20));
    JMenuItem menuManagerReport = new JMenuItem("Manager Report", loadIcon("images/report.png", 20, 20));
    JMenuItem menuUser = new JMenuItem("User", loadIcon("images/user.png", 20, 20));
    JMenuItem menuInformation = new JMenuItem("Information", loadIcon("images/info.png", 20, 20));
    JMenuItem menuBackupData = new JMenuItem("Backup Data", loadIcon("images/backup.png", 20, 20));
    JMenuItem menuExit = new JMenuItem("Exit", loadIcon("images/exit.png", 20, 20));


    menuBar.add(menuManagerCode);
    menuBar.add(menuManagerReport);
    menuBar.add(menuUser);
    menuBar.add(menuBackupData);
    menuBar.add(menuInformation);
    menuBar.add(menuExit);
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
  private ImageIcon loadIcon(String path, int width, int height) {
    java.net.URL location = getClass().getClassLoader().getResource(path);
    if (location == null) {
      System.err.println("Không tìm thấy ảnh: " + path);
      return new ImageIcon();
    }
    ImageIcon icon = new ImageIcon(location);
    Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
    return new ImageIcon(img);
  }


  public static void main(String[] args) {
    SwingUtilities.invokeLater(MainFrame::new);
  }
}
