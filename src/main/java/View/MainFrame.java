package View;

import javax.swing.*;
import java.awt.*;
import Model.User;


public class MainFrame extends JFrame {
  private CardLayout cardLayout;
  private JPanel contentPanel;
  public User currentUser;
  private JButton btnLogOut;
  private JButton btnConnect;
  private JButton btnSetting;
  private SerialPortConfigFrame serialPortConfigFrame;

  JLabel footerLabel = new JLabel();
  public MainFrame(User account) {
    this.currentUser = account;

    setTitle("HMTC - PHẦN MỀM CÂN LỐP TBR – V1.0 (" + currentUser.getUsername() +")");
    setSize(1920, 1080);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());
    setResizable(false); // Người dùng không thể thay đổi kích thước

    // Khởi tạo thanh menu
    JMenuBar menuBar = new JMenuBar();
    menuBar.setLayout(new FlowLayout(FlowLayout.LEFT));

    JMenuItem menuManagerCode = new JMenuItem("Quản lý cân", loadIcon("images/can.png", 20, 19));
    JMenuItem menuManagerCode2 = new JMenuItem("Quản lý mã lốp", loadIcon("images/code.png", 20, 20));
    JMenuItem menuManagerReport = new JMenuItem("Quản lý báo cáo", loadIcon("images/report.png", 20, 20));
    JMenuItem menuUser = new JMenuItem("Người dùng", loadIcon("images/user.png", 20, 20));
    JMenuItem menuBackupData = new JMenuItem("Sao lưu", loadIcon("images/backup.png", 20, 20));

    menuBar.add(menuManagerCode);
    menuBar.add(menuManagerCode2);
    menuBar.add(menuManagerReport);
    menuBar.add(menuUser);
    menuBar.add(menuBackupData);
    setJMenuBar(menuBar);

    // Khởi tạo panel chứa nội dung với CardLayout
    cardLayout = new CardLayout();
    contentPanel = new JPanel(cardLayout);

    // Nút kết nối
    btnConnect = new JButton("Chưa kết nối");
    btnConnect.setBackground(Color.RED);
    btnConnect.setFont(new Font("Arial", Font.BOLD, 19));
    btnConnect.setForeground(Color.BLACK);
    btnConnect.setBorder(BorderFactory.createLineBorder(Color.RED));
    btnConnect.setFocusPainted(false);

    // Thêm các trang vào CardLayout
// Lấy reference tới panel khi khởi tạo
    ManagerCode managerCodePanel = new ManagerCode(currentUser, footerLabel,btnConnect);
    ManagerMaLop managerMaLopPanel = new ManagerMaLop(currentUser, footerLabel);
    ManagerReport managerReportPanel = new ManagerReport(currentUser, footerLabel);

    contentPanel.add(managerCodePanel, "View.ManagerCode");
    contentPanel.add(managerMaLopPanel, "View.ManagerMaLop");
    contentPanel.add(managerReportPanel, "View.ManagerReport");

    add(contentPanel, BorderLayout.CENTER);



    // menu action
    menuManagerCode.addActionListener(e -> {
      managerCodePanel.loadData();
      cardLayout.show(contentPanel, "View.ManagerCode");
      managerCodePanel.requestFocusInWindow(); // để key binding hoạt động
    });

    menuManagerCode2.addActionListener(e -> {
      managerMaLopPanel.loadData();
      cardLayout.show(contentPanel, "View.ManagerMaLop");
    });

    menuManagerReport.addActionListener(e -> {
      managerReportPanel.loadData();
      cardLayout.show(contentPanel, "View.ManagerReport");
    });




    menuUser.addActionListener(e -> {
      ManagerUser userForm = new ManagerUser(currentUser,footerLabel);

      JDialog dialog = new JDialog(this, "Quản lý người dùng", true);
      dialog.setContentPane(userForm);
      dialog.pack();
      dialog.setLocationRelativeTo(this);
      dialog.setVisible(true);
      dialog.setResizable(false);
    });
    menuBackupData.addActionListener(e -> {
      BackupData backupData = new BackupData(currentUser,footerLabel);

      JDialog dialog = new JDialog(this, "Sao Lưu Dữ Liệu", true);
      dialog.setContentPane(backupData);
      dialog.pack();
      dialog.setLocationRelativeTo(this);
      dialog.setVisible(true);
      dialog.setResizable(false);
    });



    // Thêm thanh dưới cùng (footer)
    JPanel footerPanel = new JPanel(new BorderLayout());
    footerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // padding

// Label bên trái
    footerLabel.setText("Tình trạng: bạn vừa mới đăng nhập thành công - vai trờ: "+currentUser.getRole());
    footerLabel.setFont(new Font("Arial", Font.BOLD, 17));
    footerLabel.setOpaque(true);              // Quan trọng: cho phép hiển thị background
    footerLabel.setForeground(Color.BLACK);   // Chữ trắng
    footerLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5)); // padding nhỏ cho đẹp
    footerPanel.add(footerLabel, BorderLayout.WEST);


    // Panel chứa các nút bên phải
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonPanel.setOpaque(false);



    // Nút cài đặt
    btnSetting = new JButton(loadIcon("images/setting1.png", 22, 22));
    btnSetting.setFont(new Font("Arial", Font.BOLD, 15));
    btnSetting.setBackground(new Color(152, 201, 226, 255));
    btnSetting.setOpaque(true);
    btnSetting.setBorderPainted(false);
    btnSetting.setFocusPainted(false);
    btnSetting.addActionListener(e -> {
      if (serialPortConfigFrame == null || !serialPortConfigFrame.isDisplayable()) {
        serialPortConfigFrame = new SerialPortConfigFrame(btnConnect, (ManagerCode) contentPanel.getComponent(0),footerLabel);
      } else {
        serialPortConfigFrame.setVisible(true);
      }
    });


// Nút Đăng xuất bên phải
    // Nút đăng xuất
    btnLogOut = new JButton(loadIcon("images/logout.png", 22, 22));
    btnLogOut.setFont(new Font("Arial", Font.BOLD, 15));
    btnLogOut.setBackground(new Color(152, 201, 226, 255));
    btnLogOut.setOpaque(true);
    btnLogOut.setBorderPainted(false);
    btnLogOut.setFocusPainted(false);
    btnLogOut.addActionListener(e -> {
      int confirm = JOptionPane.showConfirmDialog(
              this,
              "Bạn có chắc chắn muốn đăng xuất?",
              "Đăng xuất",
              JOptionPane.YES_NO_OPTION
      );

      if (confirm == JOptionPane.YES_OPTION) {
        if (managerCodePanel != null && managerCodePanel.isComPortOpen()) {
          managerCodePanel.closeConnection();
        }
        this.dispose();
        SwingUtilities.invokeLater(() -> {
          Login loginFrame = new Login();
          loginFrame.setVisible(true);
        });
      }
    });

// Thêm nút vào panel
    buttonPanel.add(btnConnect);
    buttonPanel.add(btnSetting);
    buttonPanel.add(btnLogOut);

    footerPanel.add(buttonPanel, BorderLayout.EAST);

// Gắn footer vào frame
    add(footerPanel, BorderLayout.SOUTH);

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


}