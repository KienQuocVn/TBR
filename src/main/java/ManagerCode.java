import javax.swing.*;
import java.awt.*;

public class ManagerCode extends JPanel {

  public ManagerCode() {
    setLayout(new BorderLayout());
    add(createTopPanel(), BorderLayout.NORTH);
    add(createInfoPanel(), BorderLayout.CENTER);
    add(createBottomPanel(), BorderLayout.SOUTH);
  }

  // Tạo panel phía trên (Hiển thị số, input, nút bấm)
  private JPanel createTopPanel() {
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setPreferredSize(new Dimension(1542, 150));

    topPanel.add(createLeftTopPanel(), BorderLayout.WEST);
    topPanel.add(createMiddleTopPanel(), BorderLayout.CENTER);
    topPanel.add(createRightTopPanel(), BorderLayout.EAST);

    return topPanel;
  }

  // Tạo panel bên trái
  private JPanel createLeftTopPanel() {
    JPanel leftTop = new JPanel(new BorderLayout());
    leftTop.setPreferredSize(new Dimension(500, 100));
    leftTop.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(10, 20, 10, 0),
        BorderFactory.createLineBorder(Color.GRAY, 3)
    ));

    JLabel lblValue = new JLabel("0.0", SwingConstants.CENTER);
    lblValue.setOpaque(true);
    lblValue.setBackground(Color.BLACK);
    lblValue.setFont(new Font("Arial", Font.BOLD, 48));
    lblValue.setForeground(Color.GREEN);

    JPanel statusPanel = new JPanel(new BorderLayout());
    statusPanel.setBackground(Color.BLACK);
    statusPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 2, 0, 0, Color.WHITE),
        BorderFactory.createEmptyBorder(0, 40, 0, 40)
    ));

    JLabel lblStatus = new JLabel("NG", SwingConstants.CENTER);
    lblStatus.setFont(new Font("Arial", Font.BOLD, 48));
    lblStatus.setForeground(Color.GREEN);

    statusPanel.add(lblStatus, BorderLayout.CENTER);

    leftTop.add(lblValue, BorderLayout.CENTER);
    leftTop.add(statusPanel, BorderLayout.EAST);

    return leftTop;
  }

  // Tạo panel ở giữa (Hiển thị số)
  private JPanel createMiddleTopPanel() {
    JPanel middleTop = new JPanel();
    middleTop.setPreferredSize(new Dimension(500, 100));
    middleTop.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(10, 20, 10, 0),
        BorderFactory.createLineBorder(Color.BLACK, 2)
    ));

    JLabel lblMiddleValue = new JLabel("0.0", SwingConstants.CENTER);
    lblMiddleValue.setFont(new Font("Arial", Font.BOLD, 48));
    lblMiddleValue.setForeground(Color.RED);

    middleTop.add(lblMiddleValue);

    return middleTop;
  }

  // Tạo panel bên phải (Chứa các nút bấm)
  private JPanel createRightTopPanel() {
    JPanel rightTop = new JPanel(new GridLayout(2, 1, 5, 5));
    rightTop.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

    JButton btnSelectMode = new JButton("Chọn chế độ");
    btnSelectMode.setMargin(new Insets(10, 50, 10, 50));

    JButton btnConfirm = new JButton("Xác Nhận");
    btnConfirm.setMargin(new Insets(10, 50, 10, 50));

    rightTop.add(btnSelectMode);
    rightTop.add(btnConfirm);

    JPanel rightTopWrapper = new JPanel(new BorderLayout());
    rightTopWrapper.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
    rightTopWrapper.add(rightTop, BorderLayout.CENTER);

    return rightTopWrapper;
  }

  private JPanel createInfoPanel() {
    JPanel infoPanel = new JPanel(new BorderLayout());
    infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
    JPanel leftInfo = createLabelInputPanelLeft();

    JPanel rightInfoWrapper = new JPanel(new BorderLayout());
    rightInfoWrapper.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
    rightInfoWrapper.add(createLabelInputPanelRight(), BorderLayout.CENTER);

    JPanel barcodePanel = new JPanel(new GridLayout(2, 1, 5, 5));

    JPanel barcodeRow = new JPanel(new GridLayout(2, 1, 5, 5));
    JLabel lblBarcode = new JLabel("Bar Code", SwingConstants.CENTER);
    lblBarcode.setFont(new Font("Arial", Font.BOLD, 20));

    JTextField txtBarcode1 = new JTextField(20);
    txtBarcode1.setFont(new Font("Arial", Font.PLAIN, 18));

    barcodeRow.add(lblBarcode);
    barcodeRow.add(txtBarcode1);

    // Hàng 2: JTextField thứ 2
    JTextField txtBarcode2 = new JTextField(20);
    txtBarcode2.setFont(new Font("Arial", Font.PLAIN, 18));

    // Thêm vào barcodePanel
    barcodePanel.add(barcodeRow);
    barcodePanel.add(txtBarcode2);

    infoPanel.add(leftInfo, BorderLayout.WEST);
    infoPanel.add(rightInfoWrapper, BorderLayout.CENTER);
    infoPanel.add(barcodePanel, BorderLayout.EAST);

    return infoPanel;
  }



  private JPanel createLabelInputPanelLeft() {
    return createLabelInputPanel(new String[]{
        "Tên sản phẩm", "Mã sản phẩm", "Ngày sản xuất",
        "Hạn sử dụng", "Số lượng", "Ghi chú"
    }, 30);
  }

  private JPanel createLabelInputPanelRight() {
    return createLabelInputPanel(new String[]{
        "Nhà sản xuất", "Xuất xứ", "Lô sản xuất",
        "Nhà cung cấp", "Trọng lượng", "Ghi chú bổ sung"
    }, 20);
  }

  private JPanel createLabelInputPanel(String[] labels, int textFieldSize) {
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);

    Font labelFont = new Font("Arial", Font.BOLD, 20);
    Font textFont = new Font("Arial", Font.PLAIN, 18);
    int labelWidth = 150;

    for (String labelText : labels) {
      JLabel label = new JLabel(labelText);
      label.setFont(labelFont);
      label.setPreferredSize(new Dimension(labelWidth, 30));
      JTextField textField = new JTextField(textFieldSize);
      textField.setFont(textFont);
      gbc.gridx = 0;
      gbc.weightx = 0;
      panel.add(label, gbc);

      gbc.gridx = 1;
      gbc.weightx = 1;
      panel.add(textField, gbc);
    }
    return panel;
  }


  private JPanel createBottomPanel() {
    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.setPreferredSize(new Dimension(1542, 350));

    // Panel chứa txtInput, btnOk, btnPrint
    JTextField txtInput = new JTextField(20);
    JButton btnOk = new JButton("OKE");
    JButton btnPrint = new JButton("In Phiếu");

    JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
    contentPanel.setBackground(Color.GRAY);
    contentPanel.add(txtInput);
    contentPanel.add(btnOk);
    contentPanel.add(Box.createHorizontalStrut(1150));
    contentPanel.add(btnPrint);

    // Tạo bảng trái
    String[] columnNames = {"Cột 1", "Cột 2", "Cột 3", "Cột 4"};
    Object[][] data = {
        {"A1", "B1", "C1", "D1"},
        {"A2", "B2", "C2", "D2"},
        {"A3", "B3", "C3", "D3"},
        {"A4", "B4", "C4", "D4"}
    };
    JTable tableLeft = new JTable(data, columnNames);
    JScrollPane scrollLeft = new JScrollPane(tableLeft);

    // Tạo bảng phải
    JTable tableRight = new JTable(data, columnNames);
    JScrollPane scrollRight = new JScrollPane(tableRight);

    // Panel chứa hai bảng
    JPanel tablePanel = new JPanel(new GridLayout(1, 2, 10, 10)); // 2 bảng ngang
    tablePanel.add(scrollLeft);
    tablePanel.add(scrollRight);

    // Thêm vào bottomPanel
    bottomPanel.add(contentPanel, BorderLayout.NORTH);
    bottomPanel.add(tablePanel, BorderLayout.CENTER);

    return bottomPanel;
  }



}
