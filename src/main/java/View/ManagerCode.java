package View;

import com.toedter.calendar.JDateChooser;
import javax.swing.border.Border;

import javax.swing.*;
import java.awt.*;
import com.toedter.calendar.JDateChooser;

public class ManagerCode extends JPanel {

  public ManagerCode() {
    setLayout(new BorderLayout());
    add(createTopPanel(), BorderLayout.NORTH);
    add(createInfoPanel(), BorderLayout.CENTER);
    add(createTablePanel(), BorderLayout.SOUTH);
  }

  // Tạo panel phía trên (Hiển thị số, input, nút bấm)
  private JPanel createTopPanel() {
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setPreferredSize(new Dimension(1542, 200));

    topPanel.add(createLeftTopPanel(), BorderLayout.WEST);
    topPanel.add(createMiddleTopPanel(), BorderLayout.CENTER);
    topPanel.add(createRightTopPanel(), BorderLayout.EAST);

    return topPanel;
  }

  private JPanel createLeftTopPanel() {
    JPanel leftTop = new JPanel();
    leftTop.setLayout(new BoxLayout(leftTop, BoxLayout.Y_AXIS));
    leftTop.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Font
    Font labelFont = new Font("Arial", Font.BOLD, 16);
    Font inputFont = new Font("Arial", Font.PLAIN, 14);

    // "Chọn chế độ"
    JLabel chonCheDoLabel = new JLabel("Chọn chế độ");
    chonCheDoLabel.setFont(labelFont);


    JComboBox<String> modeComboBox = new JComboBox<>(new String[]{"AUTO"});
    modeComboBox.setFont(inputFont);
    modeComboBox.setMaximumSize(new Dimension(200, 25));

    // "Date From"
    JDateChooser dateFrom = new JDateChooser();
    dateFrom.setFont(inputFont);
    dateFrom.setDateFormatString("dd/MM/yyyy");
    dateFrom.setDate(java.sql.Date.valueOf("2025-07-25"));
    dateFrom.setMaximumSize(new Dimension(200, 40));

    // "Ngày LH"
    JLabel ngayLHLabel = new JLabel("Ngày LH");
    ngayLHLabel.setFont(labelFont);
    JTextField ngayLHField = new JTextField("25.07.2025");
    ngayLHField.setFont(inputFont);
    ngayLHField.setMaximumSize(new Dimension(200, 40));

    // Add to panel
    leftTop.add(chonCheDoLabel);
    leftTop.add(modeComboBox);
    leftTop.add(Box.createVerticalStrut(10));
    leftTop.add(dateFrom);
    leftTop.add(Box.createVerticalStrut(10));
    leftTop.add(ngayLHLabel);
    leftTop.add(ngayLHField);

    // Wrapper để canh lề
    JPanel leftTopWrapper = new JPanel(new BorderLayout());
    leftTopWrapper.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
    leftTopWrapper.add(leftTop, BorderLayout.NORTH);

    return leftTopWrapper;
  }


  // Tạo panel ở giữa (Hiển thị số và chế độ)
  private JPanel createMiddleTopPanel() {
    JPanel middleTop = new JPanel();
    middleTop.setPreferredSize(new Dimension(500, 200));
    middleTop.setLayout(new BoxLayout(middleTop, BoxLayout.Y_AXIS));
    middleTop.setBackground(Color.WHITE);

    // Label chế độ
    JLabel lblMode = new JLabel("Chế độ: AUTO", SwingConstants.CENTER);
    lblMode.setFont(new Font("Arial", Font.BOLD, 20));
    lblMode.setAlignmentX(Component.CENTER_ALIGNMENT);
    lblMode.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(13, 343, 13, 343)
    ));

    // Label giá trị
    JLabel lblMiddleValue = new JLabel("0.0", SwingConstants.CENTER);
    lblMiddleValue.setFont(new Font("Arial", Font.BOLD, 120));
    lblMiddleValue.setForeground(Color.RED);
    lblMiddleValue.setAlignmentX(Component.CENTER_ALIGNMENT);
    lblMiddleValue.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(0, 328, 0, 328)
    ));

    // Add vào panel
    middleTop.add(lblMode);
    middleTop.add(Box.createVerticalStrut(5));
    middleTop.add(lblMiddleValue);

    return middleTop;
  }




  private JPanel createRightTopPanel() {
    JPanel rightTop = new JPanel(new BorderLayout());
    rightTop.setPreferredSize(new Dimension(500, 100));
    rightTop.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(30, 20, 30, 0),
            BorderFactory.createLineBorder(Color.GRAY, 3)
    ));

    // Panel trung tâm
    JPanel centerPanel = new JPanel(new BorderLayout());
    centerPanel.setOpaque(false);

    // Panel chứa các thành phần trên cùng
    JPanel topContentPanel = new JPanel();
    topContentPanel.setLayout(new BoxLayout(topContentPanel, BoxLayout.Y_AXIS));
    topContentPanel.setOpaque(false);

    // Label "Mã Barcode"
    JLabel lblBarcode = new JLabel("Mã Barcode", SwingConstants.CENTER);
    lblBarcode.setFont(new Font("Arial", Font.BOLD, 16));
    lblBarcode.setAlignmentX(Component.CENTER_ALIGNMENT);

    // Input Barcode
    JTextField txtBarcode = new JTextField();
    txtBarcode.setFont(new Font("Arial", Font.PLAIN, 18));
    txtBarcode.setMaximumSize(new Dimension(300, 30));
    txtBarcode.setAlignmentX(Component.CENTER_ALIGNMENT);

    JPanel subInputPanel = new JPanel();
    subInputPanel.setLayout(new BoxLayout(subInputPanel, BoxLayout.X_AXIS));
    subInputPanel.setOpaque(false);

    // Ô vuông
    JTextField txtStartPoint = new JTextField(2);
    JTextField txtEndPoint = new JTextField(2);

    Dimension squareSize = new Dimension(30, 30);
    txtStartPoint.setMaximumSize(squareSize);
    txtEndPoint.setMaximumSize(squareSize);

    txtStartPoint.setHorizontalAlignment(JTextField.CENTER);
    txtEndPoint.setHorizontalAlignment(JTextField.CENTER);

    // Canh đều hai bên dưới txtBarcode
    subInputPanel.add(Box.createHorizontalStrut(5));
    subInputPanel.add(txtStartPoint);
    subInputPanel.add(Box.createHorizontalGlue());
    subInputPanel.add(txtEndPoint);
    subInputPanel.add(Box.createHorizontalStrut(5));

    // Add components to topContentPanel with reduced struts
    topContentPanel.add(lblBarcode);
    topContentPanel.add(Box.createVerticalStrut(2));
    topContentPanel.add(txtBarcode);
    topContentPanel.add(Box.createVerticalStrut(2));
    topContentPanel.add(subInputPanel);

    // Button Save
    JButton btnSave = new JButton("Save");
    btnSave.setFont(new Font("Arial", Font.BOLD, 16));
    btnSave.setBackground(new Color(0, 153, 102));
    btnSave.setForeground(Color.YELLOW);
    btnSave.setFocusPainted(false);
    btnSave.setAlignmentX(Component.CENTER_ALIGNMENT);

    // Add top content and button to centerPanel
    centerPanel.add(topContentPanel, BorderLayout.CENTER);
    centerPanel.add(btnSave, BorderLayout.PAGE_END);

    // Panel "NG" bên phải
    JPanel statusPanel = new JPanel(new BorderLayout());
    statusPanel.setBackground(Color.BLACK);
    statusPanel.setPreferredSize(new Dimension(100, 100));
    statusPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

    JLabel lblStatus = new JLabel("NG", SwingConstants.CENTER);
    lblStatus.setFont(new Font("Arial", Font.BOLD, 48));
    lblStatus.setForeground(Color.RED);
    statusPanel.add(lblStatus, BorderLayout.CENTER);

    rightTop.add(centerPanel, BorderLayout.CENTER);
    rightTop.add(statusPanel, BorderLayout.EAST);

    return rightTop;
  }



  private JPanel createInfoPanel() {
    JPanel infoPanel = new JPanel(new GridLayout(1, 4, 10, 0));
    infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

    JPanel column1 = createLabelInputPanelColumn1();
    JPanel column2 = createLabelInputPanelColumn2();
    JPanel column3 = createLabelInputPanelColumn3();
    JPanel column4 = createLabelInputPanelColumn4();

    // Add rounded border to each column
    column1.setBorder(new RoundedBorder(10, Color.GRAY));
    column2.setBorder(new RoundedBorder(10, Color.GRAY));
    column3.setBorder(new RoundedBorder(10, Color.GRAY));
    column4.setBorder(new RoundedBorder(10, Color.GRAY));

    // Set same size for all panels
    Dimension fixedSize = new Dimension(350, column1.getPreferredSize().height);
    column1.setPreferredSize(fixedSize);
    column2.setPreferredSize(fixedSize);
    column3.setPreferredSize(fixedSize);
    column4.setPreferredSize(fixedSize);

    infoPanel.add(column1);
    infoPanel.add(column2);
    infoPanel.add(column3);
    infoPanel.add(column4);

    return infoPanel;
  }

  private JPanel createLabelInputPanelColumn1() {
    return createLabelInputPanel(new String[]{
            "Code", "Quy Cách", "PR"
    }, 30);
  }

  private JPanel createLabelInputPanelColumn2() {
    return createLabelInputPanel(new String[]{
            "Mã Gai", "KL Max", "KL Min"
    }, 30);
  }

  private JPanel createLabelInputPanelColumn3() {
    return createLabelInputPanel(new String[]{
            "Chỉ Số Tải", "Tốc Độ", "Vành"
    }, 30);
  }

  private JPanel createLabelInputPanelColumn4() {
    return createLabelInputPanel(new String[]{
            "Thương Hiệu", "TT/TL", "KL Chuẩn"
    }, 30);
  }

  // Custom RoundedBorder class
  private static class RoundedBorder implements Border {
    private int radius;
    private Color color;

    RoundedBorder(int radius, Color color) {
      this.radius = radius;
      this.color = color;
    }

    public Insets getBorderInsets(Component c) {
      return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
    }

    public boolean isBorderOpaque() {
      return true;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      Graphics2D g2d = (Graphics2D) g.create();
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setColor(color);
      g2d.drawRoundRect(x, y, width-1, height-1, radius, radius);
      g2d.dispose();
    }
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

  private JPanel createTablePanel() {
    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.setPreferredSize(new Dimension(1542, 350));

    JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
    contentPanel.setBackground(Color.GRAY);

// Khai báo JDateChooser
    JDateChooser fromDate = new JDateChooser();
    fromDate.setPreferredSize(new Dimension(120, 25));
    JDateChooser toDate = new JDateChooser();
    toDate.setPreferredSize(new Dimension(120, 25));

// Label cho ngày
    contentPanel.add(new JLabel("Từ ngày:"));
    contentPanel.add(fromDate);
    contentPanel.add(new JLabel("Đến ngày:"));
    contentPanel.add(toDate);

// ComboBox "Mã mới"
    contentPanel.add(new JLabel("Mã mới:"));
    String[] maMoiOptions = {"-- Chọn mã --", "CL01", "CL02", "CL03"};
    JComboBox<String> comboMaMoi = new JComboBox<>(maMoiOptions);
    comboMaMoi.setPreferredSize(new Dimension(150, 25));
    contentPanel.add(comboMaMoi);


    // ======= BẢNG 1 (bên trái) =======
    String[] leftColumnNames = {
            "STT", "Code lốp", "Quy cách", "KL cân (kg)", "Barcode", "Kết quả"
    };
    Object[][] leftData = {
            {"1", "CL01", "120/70-17", "12.5", "123456789", "OK"},
            {"2", "CL02", "130/70-17", "13.2", "987654321", "NG"}
    };
    JTable tableLeft = new JTable(leftData, leftColumnNames);
    JScrollPane scrollLeft = new JScrollPane(tableLeft);

    // ======= BẢNG 2 (bên phải) có cuộn ngang =======
    String[] rightColumnNames = {
            "Code lốp", "Quy cách", "PR", "Mã gai", "TT_TL", "Chỉ số tải", "Tốc độ",
            "Thương hiệu", "Vành", "TCTK (kg)", "Max (kg)", "Min (kg)", "Thời gian lưu"
    };
    Object[][] rightData = {
            {"CL01", "120/70-17", "6", "MG01", "TT", "58", "H", "IRC", "17", "12.0", "13.0", "11.0", "01/08/2025"},
            {"CL02", "130/70-17", "8", "MG02", "TL", "60", "V", "Michelin", "17", "13.0", "14.0", "12.0", "01/08/2025"}
    };
    JTable tableRight = new JTable(rightData, rightColumnNames);
    tableRight.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    JScrollPane scrollRight = new JScrollPane(tableRight);
    scrollRight.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollRight.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    // Panel chứa hai bảng
    JPanel tablePanel = new JPanel(new GridLayout(1, 2, 10, 10));
    tablePanel.add(scrollLeft);
    tablePanel.add(scrollRight);

    // Thêm vào bottomPanel
    bottomPanel.add(contentPanel, BorderLayout.NORTH);
    bottomPanel.add(tablePanel, BorderLayout.CENTER);

    return bottomPanel;
  }

}