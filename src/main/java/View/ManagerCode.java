package View;

import Dao.*;
import Model.*;
import Utils.ComboBoxUtils;
import com.fazecast.jSerialComm.SerialPort;
import com.toedter.calendar.JDateChooser;
import javax.swing.border.Border;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManagerCode extends JPanel {
  public User currentUser;
  JLabel footerLabel;
  private JTable table;
  private DefaultTableModel tableModel;
  private DaoSerialPortConfig daoSerialPortConfig;
  private JButton mainFrameBtnConnect; // Tham chiếu đến btnConnect của MainFrame
  //====== khai báo ColumnName =============
  String[] leftColumnNames = {
          "STT", "Mã lốp", "Quy cách", "KL cân (kg)", "Mã vạch", "Kết quả","PR","Mã gai","TT_TL",
          "Tốc độ","Chỉ số tải","Thương hiệu","Vành","TCTK(kg)","Min(kg)","Max(kg)","Thời gian cân","ID"
  };

  // ===== Khai báo biến thành viên =====
  private JComboBox<String> cboMaLop;
  private JTextField txtQuyCach;
  private JTextField txtPR;

  private JTextField txtMaGai;
  private JTextField txtTLMax;
  private JTextField txtTLMin;

  private JTextField txtChiSoTai;
  private JTextField txtTocDo;
  private JTextField txtVanh;

  private JTextField txtThuongHieu;
  private JTextField txtTTTL;
  private JTextField txtTLChuan;
  private ProductDAO productDAO =  new ProductDAO();
  private WeighRecordDAO weighRecordDAO =  new WeighRecordDAO();
  public MachineDAO machineDAO = new MachineDAO();
  public SerialPort comPort;
  private JLabel lblMiddleValue;
  private  JLabel lblBarcode;
  JTextField txtBarcode;

  public ManagerCode(User account,JLabel Label,JButton btnConnect) {
    this.currentUser = account;
    this.footerLabel = Label;
    this.mainFrameBtnConnect = btnConnect; // Gán tham chiếu btnConnect

    setLayout(new BorderLayout());

    add(createTopPanel(), BorderLayout.NORTH);
    add(createInfoPanel(), BorderLayout.CENTER);
    add(createTablePanel(), BorderLayout.SOUTH);
    loadProductDetails(); // gọi hàm khi chọn mã lốp
    updateTableSearch();
    initBarcodeScanner();

    if (comPort == null || !comPort.isOpen()) {
      // Tạo luồng riêng để gọi sendAndReceiveDataTest
      new Thread(() -> {
        try {
          sendAndReceiveDataTest();
        } catch (Exception ex) {
          ex.printStackTrace();
          System.out.println("Lỗi khi kết nối thiết bị:");

        }
      }).start();
    } else {
      System.out.println("Thiết bị đã được kết nối.");
    }




// Thêm Key Bindings để bắt sự kiện Space
    getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "spacePressed");

    getActionMap().put("spacePressed", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
           addMaCan(); // Gọi sự kiện khi nhấn Space
      }
    });



  }

  // Biến toàn cục
  private StringBuilder buffer = new StringBuilder();

  private void initBarcodeScanner() {
    KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
      if (e.getID() != KeyEvent.KEY_TYPED) return false;
      char ch = e.getKeyChar();

      // ✅ Chỉ nhận khi txtBarcode đang focus
      if (txtBarcode.isFocusOwner()) {
        if (ch == '\n') { // scanner kết thúc bằng Enter
          processBarcode(buffer.toString());
          buffer.setLength(0);
        } else {
          buffer.append(ch);
        }
      } else {
        buffer.setLength(0); // clear buffer nếu mất focus
      }

      return false;
    });
  }

  private void processBarcode(String barcode) {
    barcode = barcode.trim();

    if (!barcode.isEmpty()) {
      txtBarcode.setText(barcode);  // Ghi đè text cũ
      txtBarcode.requestFocusInWindow(); // ✅ đảm bảo lần quét sau vẫn vào đây
    }
  }




  private JPanel createTopPanel() {
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setPreferredSize(new Dimension(1542, 200));
    topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

    topPanel.add(createMiddleTopPanel(), BorderLayout.CENTER);
    topPanel.add(createRightTopPanel(), BorderLayout.EAST);

    return topPanel;
  }

  private JPanel createMiddleTopPanel() {
    JPanel middleTop = new JPanel(new BorderLayout());
    middleTop.setBackground(Color.WHITE);
    middleTop.setPreferredSize(new Dimension(500, 150));

    // Label giá trị
    lblMiddleValue = new JLabel("0.00", SwingConstants.CENTER);
    lblMiddleValue.setFont(new Font("Arial", Font.BOLD, 140));
    lblMiddleValue.setForeground(Color.RED);
    lblMiddleValue.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

    // Panel "NG" bên phải
    JPanel statusPanel = new JPanel(new BorderLayout());
    statusPanel.setBackground(new Color(255, 153, 51));
    statusPanel.setPreferredSize(new Dimension(110, 110));
    statusPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

    JLabel lblStatus = new JLabel("NG", SwingConstants.CENTER);
    lblStatus.setFont(new Font("Arial", Font.BOLD, 48));
    lblStatus.setForeground(Color.white);
    statusPanel.add(lblStatus, BorderLayout.CENTER);

    // Add vào middleTop
    middleTop.add(lblMiddleValue, BorderLayout.CENTER);
    middleTop.add(statusPanel, BorderLayout.EAST);

    return middleTop;
  }


  private JPanel createRightTopPanel() {
    JPanel rightTop = new JPanel(new BorderLayout());
    rightTop.setBackground(Color.WHITE);
    rightTop.setPreferredSize(new Dimension(500, 120));
    rightTop.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 20, 0, 0),
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
    lblBarcode = new JLabel("Mã vạch", SwingConstants.CENTER);
    lblBarcode.setFont(new Font("Arial", Font.BOLD, 22)); // to hơn
    lblBarcode.setAlignmentX(Component.CENTER_ALIGNMENT);

    // Input Barcode (tăng size)
     txtBarcode = new JTextField();
    txtBarcode.setFont(new Font("Arial", Font.PLAIN, 20));
    txtBarcode.setPreferredSize(new Dimension(350, 40)); // rộng và cao hơn
    txtBarcode.setMaximumSize(new Dimension(350, 40));
    txtBarcode.setAlignmentX(Component.CENTER_ALIGNMENT);

    JPanel subInputPanel = new JPanel();
    subInputPanel.setLayout(new BoxLayout(subInputPanel, BoxLayout.X_AXIS));
    subInputPanel.setOpaque(false);



    topContentPanel.add(lblBarcode);
    topContentPanel.add(Box.createVerticalStrut(5));
    topContentPanel.add(txtBarcode);
    topContentPanel.add(Box.createVerticalStrut(5));
    topContentPanel.add(subInputPanel);

    // Button Save (tăng size)
    JButton btnSave = new JButton("Lưu");
    btnSave.setFont(new Font("Arial", Font.BOLD, 20)); // to hơn
    btnSave.setBackground(new Color(0, 128, 255));
    btnSave.setForeground(Color.WHITE);
    btnSave.setFocusPainted(false);
    btnSave.setAlignmentX(Component.CENTER_ALIGNMENT);
    btnSave.setPreferredSize(new Dimension(120, 55)); // nút rộng và cao hơn
    btnSave.setMaximumSize(new Dimension(120, 55));
    btnSave.addActionListener(e -> {
      addMaCan();
    });



    centerPanel.add(topContentPanel, BorderLayout.CENTER);
    centerPanel.add(btnSave, BorderLayout.PAGE_END);



    rightTop.add(centerPanel, BorderLayout.CENTER);


    return rightTop;
  }

  public void addMaCan() {
    try {
      if (!validateInputs()) {
        return; // dừng nếu không hợp lệ
      }

      // 1. Lấy product đã chọn
      String productCode = (String) cboMaLop.getSelectedItem();
      Product product = productDAO.findByProductCode(productCode);

      if (product == null) {
        JOptionPane.showMessageDialog(null, "Không tìm thấy sản phẩm trong hệ thống!");
        return;
      }


      // 32. Tạo weigh record mới
      WeighRecord wr = createWeighRecord(product);
      weighRecordDAO.insert(wr);

      // 4. Thông báo
      footerLabel.setText("Thông báo: bạn vừa mới cân mã hàng " +product.getProductCode()+ " ("+ lblMiddleValue.getText() +" Kg) " +" thành công:"   );
      JOptionPane.showMessageDialog(null, "Lưu thành công!");
      updateTableSearch();

    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null, "Lỗi khi lưu: " + ex.getMessage());
      ex.printStackTrace();
    }
  }


  private JPanel createInfoPanel() {
    JPanel infoPanel = new JPanel(new GridLayout(1, 4, 0, 0));
    infoPanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(10, Color.GRAY),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)
    ));
    infoPanel.setBackground(Color.white);
    JPanel column1 = createLabelInputPanelColumn1();
    JPanel column2 = createLabelInputPanelColumn2();
    JPanel column3 = createLabelInputPanelColumn3();
    JPanel column4 = createLabelInputPanelColumn4();

    infoPanel.add(column1);
    infoPanel.add(column2);
    infoPanel.add(column3);
    infoPanel.add(column4);

    return infoPanel;
  }

  // Cột 1
  private JPanel createLabelInputPanelColumn1() {
    cboMaLop   = createComboBox();  //

    cboMaLop.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        loadProductDetails(); // gọi hàm khi chọn mã lốp
      }
    });
    txtQuyCach = createTextField();
    txtPR      = createTextField();

    return createLabelInputPanel(
            new String[]{"Mã Lốp", "Quy Cách", "PR"},
            new JComponent[]{cboMaLop, txtQuyCach, txtPR}
    );
  }


  // ===== Column 2 =====
  private JPanel createLabelInputPanelColumn2() {
    txtMaGai = createTextField();
    txtTLMax = createTextField();
    txtTLMin = createTextField();
    return createLabelInputPanel(
            new String[]{"Mã Gai", "KL Max", "KL Min"},
            new JComponent[]{txtMaGai, txtTLMax, txtTLMin}
    );
  }

  // ===== Column 3 =====
  private JPanel createLabelInputPanelColumn3() {
    txtChiSoTai = createTextField();
    txtTocDo    = createTextField();
    txtVanh     = createTextField();
    return createLabelInputPanel(
            new String[]{"Chỉ Số Tải", "Tốc Độ", "Vành"},
            new JComponent[]{txtChiSoTai, txtTocDo, txtVanh}
    );
  }

  // ===== Column 4 =====
  private JPanel createLabelInputPanelColumn4() {
    txtThuongHieu = createTextField();
    txtTTTL       = createTextField();
    txtTLChuan    = createTextField();
    return createLabelInputPanel(
            new String[]{"Thương Hiệu", "TT/TL", "KL Chuẩn"},
            new JComponent[]{txtThuongHieu, txtTTTL, txtTLChuan}
    );
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

  private JPanel createLabelInputPanel(String[] labels, JComponent[] fields) {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(Color.white);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);

    Font labelFont = new Font("Arial", Font.BOLD, 20);
    int labelWidth = 150;

    for (int i = 0; i < labels.length; i++) {
      JLabel label = new JLabel(labels[i]);
      label.setFont(labelFont);
      label.setPreferredSize(new Dimension(labelWidth, 30));

      gbc.gridx = 0;
      gbc.weightx = 0;
      panel.add(label, gbc);

      gbc.gridx = 1;
      gbc.weightx = 1;

      if (fields[i] instanceof JTextField tf) {
        tf.setFont(new Font("Arial", Font.BOLD, 28));
        tf.setForeground(new Color(0, 70, 140));
        tf.setBackground(Color.WHITE);
        tf.setCaretColor(Color.BLACK);
        tf.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
      } else if (fields[i] instanceof JComboBox<?> cb) {
        cb.setFont(new Font("Arial", Font.BOLD, 20));
        cb.setForeground(new Color(0, 70, 140));
        cb.setBackground(Color.WHITE);
        cb.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
      }

      panel.add(fields[i], gbc);
    }
    return panel;
  }

  JButton XoaButton;
  JDateChooser fromDate;
  JDateChooser toDate;
  private JPanel createTablePanel() {
    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.setPreferredSize(new Dimension(1542, 600));
    bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

    LocalDateTime now = LocalDateTime.now();
    Date currentDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());

// ===== Thanh chọn ngày và combobox =====
    JPanel contentPanel = new JPanel(new BorderLayout());
    contentPanel.setBackground(Color.WHITE);

// Panel bên trái (FlowLayout để xếp ngang)
    JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
    leftPanel.setBackground(Color.WHITE);


// Khai báo JDateChooser
     fromDate = new JDateChooser();
    fromDate.setPreferredSize(new Dimension(120, 25));
    fromDate.setDate(currentDate);
    fromDate.setDateFormatString("dd/MM/yyyy");
    fromDate.addPropertyChangeListener("date", evt -> {
      updateTableSearch();
    });

     toDate = new JDateChooser();
    toDate.setPreferredSize(new Dimension(120, 25));
    toDate.setDate(currentDate);
    toDate.setDateFormatString("dd/MM/yyyy");
    toDate.addPropertyChangeListener("date", evt -> {
      updateTableSearch();
    });
// Thêm label và datechooser vào panel trái
    leftPanel.add(new JLabel("Từ ngày:"));
    leftPanel.add(fromDate);
    leftPanel.add(new JLabel("Đến ngày:"));
    leftPanel.add(toDate);

    XoaButton = new JButton("Xóa");
    XoaButton.setFocusPainted(false);
    XoaButton.setFont(new Font("Arial", Font.BOLD, 14));
    XoaButton.setBackground(new Color(204, 0, 0)); // Đỏ đậm
    XoaButton.setForeground(Color.WHITE);
    XoaButton.setEnabled(false);
    XoaButton.addActionListener(e -> {
      RemoveWeighRecord();
    });
    leftPanel.add(XoaButton);




// Add vào contentPanel
    contentPanel.add(leftPanel, BorderLayout.WEST);



    // Khởi tạo table model không cho sửa
    tableModel = new DefaultTableModel( leftColumnNames,0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false; // tất cả cell đều không cho sửa
      }
    };
    table = new JTable(tableModel);

    JScrollPane scrollLeft = new JScrollPane(table);
    styleTable(table);

      table.getSelectionModel().addListSelectionListener(e -> {
        int selectedCount = table.getSelectedRowCount();
        if (selectedCount == 1) {
          XoaButton.setText("Xóa");
          XoaButton.setEnabled(true);
        } else if (selectedCount > 1) {
          XoaButton.setText("Xóa nhiều");
          XoaButton.setEnabled(true);
        } else {
          XoaButton.setText("Xóa");
          XoaButton.setEnabled(false);
        }
      });




    // Panel chính cho 2 bảng + separator
    JPanel tablesContainer = new JPanel();
    tablesContainer.setLayout(new BoxLayout(tablesContainer, BoxLayout.Y_AXIS));
    tablesContainer.add(scrollLeft);


    // Thêm vào bottomPanel
    bottomPanel.add(contentPanel, BorderLayout.NORTH);
    bottomPanel.add(tablesContainer, BorderLayout.CENTER);

    return bottomPanel;
  }

  // Hàm tạo textfield chuẩn
  private JTextField createTextField() {
    JTextField tf = new JTextField();
    tf.setFont(new Font("Arial", Font.BOLD, 18));
    tf.setForeground(new Color(0, 70, 140));
    tf.setBackground(Color.WHITE);
    tf.setCaretColor(Color.BLACK);
    tf.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
    return tf;
  }


  private JComboBox<String> createComboBox() {

    // Dùng class Utils bạn đã viết để tạo combobox có gợi ý
    JComboBox<String> comboBox = ComboBoxUtils.createSuggestionComboBox(() -> productDAO.getAllProductCodes());

    comboBox.setFont(new Font("Arial", Font.BOLD, 20));
    comboBox.setForeground(new Color(0, 70, 140));
    comboBox.setBackground(Color.WHITE);
    comboBox.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));

    return comboBox;
  }




  // Phương thức tùy chỉnh bảng JTable
  private void styleTable(JTable table) {
    table.setRowHeight(30);
    table.setFont(new Font("Arial", Font.PLAIN, 14));
    table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
    table.getTableHeader().setBackground(new Color(255, 153, 51));
    table.getTableHeader().setForeground(Color.BLACK);
    table.setGridColor(Color.GRAY);
    table.setSelectionBackground(new Color(204, 229, 255));
    table.setSelectionForeground(Color.BLACK);

    TableColumnModel columnModel = table.getColumnModel();

// Set độ rộng cho từng cột
    columnModel.getColumn(0).setPreferredWidth(30);   // STT
    columnModel.getColumn(1).setPreferredWidth(120);  // Mã lốp
    columnModel.getColumn(2).setPreferredWidth(150);  // Quy cách
    columnModel.getColumn(3).setPreferredWidth(100);  // KL cân
    columnModel.getColumn(4).setPreferredWidth(150);  // Mã vạch
    columnModel.getColumn(5).setPreferredWidth(100);  // Kết quả
    columnModel.getColumn(6).setPreferredWidth(60);   // PR
    columnModel.getColumn(7).setPreferredWidth(80);   // Mã gai
    columnModel.getColumn(8).setPreferredWidth(80);   // TT_TL
    columnModel.getColumn(9).setPreferredWidth(80);   // Tốc độ
    columnModel.getColumn(10).setPreferredWidth(100); // Chỉ số tải
    columnModel.getColumn(11).setPreferredWidth(120); // Thương hiệu
    columnModel.getColumn(12).setPreferredWidth(80);  // Vành
    columnModel.getColumn(13).setPreferredWidth(100); // TCTK(kg)
    columnModel.getColumn(14).setPreferredWidth(100); // Max(kg)
    columnModel.getColumn(15).setPreferredWidth(100); // Max(kg)
    columnModel.getColumn(16).setPreferredWidth(150); // Thời gian cân
    columnModel.getColumn(17).setPreferredWidth(150); // Thời gian cân
    // Màu nền xen kẽ
    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (!isSelected) {
          component.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
        }
        return component;
      }
    });

    // Ẩn cột cuối (ID)
    TableColumn idColumn = table.getColumnModel().getColumn(table.getColumnCount() - 1);
    idColumn.setMinWidth(0);
    idColumn.setMaxWidth(0);
    idColumn.setPreferredWidth(0);

    table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    // Căn lề giữa cho các cột
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(JLabel.CENTER);
    for (int i = 0; i < table.getColumnCount(); i++) {
      table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
    }

    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (column == 13) { // Cột "Trạng Thái"
          String status = value.toString();
          if (status.equals("Nhập")) {
            component.setBackground(new Color(152, 251, 152)); // Xanh lá
          } else if (status.equals("Chờ")) {
            component.setBackground(new Color(255, 255, 224)); // Vàng nhạt
          } else {
            component.setBackground(Color.WHITE);
          }
        } else if (!isSelected) {
          component.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
        }
        return component;
      }
    });

  }


  private void updateTableSearch() {
    try {
      java.util.Date utilFrom = fromDate.getDate();
      java.util.Date utilTo   = toDate.getDate();

      java.sql.Date sqlFrom = new java.sql.Date(utilFrom.getTime());
      java.sql.Date sqlTo   = new java.sql.Date(utilTo.getTime());

      List<Object[]> allRows = weighRecordDAO.searchProductsWithDate(sqlFrom,sqlTo);

      tableModel.setRowCount(0);
      int stt = 1;
      for (Object[] row : allRows) {
        Object[] newRow = new Object[leftColumnNames.length];
        newRow[0] = stt++;
        for (int i = 1; i < leftColumnNames.length; i++) {
          newRow[i] = row[i];
        }
        tableModel.addRow(newRow);
      }
    } catch (Exception e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(null, "Lỗi khi load dữ liệu bảng: " + e.getMessage());
    }
  }





  public void loadData() {
    updateComboBoxData(cboMaLop);
    loadProductDetails(); // gọi hàm khi chọn mã lốp
    updateTableSearch();
  }

  public void updateComboBoxData(JComboBox<String> comboBox) {
    Object selected = comboBox.getSelectedItem();

    // Lấy dữ liệu mới nhất từ DB
    List<String> dbCodes = productDAO.getAllProductCodes();
    if (dbCodes == null) dbCodes = new ArrayList<>();

    // Lưu item hiện có trong combobox
    Set<String> comboCodes = new HashSet<>();
    for (int i = 0; i < comboBox.getItemCount(); i++) {
      comboCodes.add(comboBox.getItemAt(i));
    }

    // Thêm mới những code chưa có
    for (String code : dbCodes) {
      if (!comboCodes.contains(code)) {
        comboBox.addItem(code);
      }
    }

    // Xóa những code đã bị xóa trong DB
    for (String code : new HashSet<>(comboCodes)) {
      if (!dbCodes.contains(code)) {
        comboBox.removeItem(code);
      }
    }

    JTextField editor = (JTextField) comboBox.getEditor().getEditorComponent();

    // ✅ Chỉ restore lại nếu selection cũ thực sự tồn tại trong DB
    if (selected != null && !selected.toString().trim().isEmpty() && dbCodes.contains(selected.toString())) {
      comboBox.setSelectedItem(selected);
      editor.setText(selected.toString());
    } else {
      // Giữ combobox rỗng, không auto chọn "gần nhất"
      comboBox.setSelectedItem(null);
      comboBox.setSelectedIndex(-1);
      editor.setText("");
    }
  }


  private void loadProductDetails() {
    String selectedProductCode = (String) cboMaLop.getSelectedItem(); // lấy mã lốp đang chọn
    if (selectedProductCode == null || selectedProductCode.trim().isEmpty()) {
      txtQuyCach.setText("");      // Quy Cách
      txtPR.setText("");    // PR
      txtMaGai.setText("");        // Mã Gai
      txtTTTL.setText("");         // TT/TL
      txtChiSoTai.setText("");     // Chỉ Số Tải
      txtTocDo.setText("");        // Tốc Độ
      txtThuongHieu.setText("");   // Thương Hiệu
      txtVanh.setText("");  // Vành

      // Min / Chuẩn / Max (BigDecimal -> String)
      txtTLMin.setText("");
      txtTLChuan.setText("");
      txtTLMax.setText("");
      return;
    }

    // Gọi DAO lấy dữ liệu
    Object[] row = productDAO.getOneForTable(selectedProductCode);

    if (row != null) {
      // Map đúng theo thứ tự bạn đã return trong getOneForTable()
      txtQuyCach.setText((String) row[2]);      // Quy Cách
      txtPR.setText(String.valueOf(row[3]));    // PR
      txtMaGai.setText((String) row[4]);        // Mã Gai
      txtTTTL.setText((String) row[5]);         // TT/TL
      txtChiSoTai.setText((String) row[6]);     // Chỉ Số Tải
      txtTocDo.setText((String) row[7]);        // Tốc Độ
      txtThuongHieu.setText((String) row[8]);   // Thương Hiệu
      txtVanh.setText(String.valueOf(row[9]));  // Vành

      // Min / Chuẩn / Max (BigDecimal -> String)
      txtTLMin.setText(row[10] != null ? row[10].toString() : "");
      txtTLChuan.setText(row[11] != null ? row[11].toString() : "");
      txtTLMax.setText(row[12] != null ? row[12].toString() : "");
    }
  }

  private boolean validateInputs() {
    if (cboMaLop.getSelectedItem() == null) {
      JOptionPane.showMessageDialog(null, "Vui lòng chọn mã lớp!");
      return false;
    }

    // Quy Cách
    if (txtBarcode.getText().trim().isEmpty()) {
      showError("Mã vạch không được để trống!", txtQuyCach);
      return false;
    }

    // Quy Cách
    if (txtQuyCach.getText().trim().isEmpty()) {
      showError("Quy Cách không được để trống!", txtQuyCach);
      return false;
    }
    // PR
    if (!isInteger(txtPR.getText())) {
      showError("PR phải là số nguyên!", txtPR);
      return false;
    }
    // Mã Gai
    if (txtMaGai.getText().trim().isEmpty()) {
      showError("Mã Gai không được để trống!", txtMaGai);
      return false;
    }
    // TT/TL
    String ttl = txtTTTL.getText().trim().toUpperCase();
    if (!(ttl.equals("TT") || ttl.equals("TL"))) {
      showError("Loại lốp phải là TT hoặc TL!", txtTTTL);
      return false;
    }
    // Chỉ Số Tải
    if (txtChiSoTai.getText().trim().isEmpty()) {
      showError("Chỉ Số Tải không được để trống!", txtChiSoTai);
      return false;
    }
    // Tốc Độ
    if (txtTocDo.getText().trim().isEmpty()) {
      showError("Tốc Độ không được để trống!", txtTocDo);
      return false;
    }
    // Thương Hiệu
    if (txtThuongHieu.getText().trim().isEmpty()) {
      showError("Thương Hiệu không được để trống!", txtThuongHieu);
      return false;
    }
    // Vành
    if (!isInteger(txtVanh.getText())) {
      showError("Vành phải là số nguyên!", txtVanh);
      return false;
    }
    // KL Min
    if (!isDouble(txtTLMin.getText())) {
      showError("Khối Lượng Min phải là số!", txtTLMin);
      return false;
    }
    // KL Chuẩn
    if (!isDouble(txtTLChuan.getText())) {
      showError("Khối Lượng Chuẩn phải là số!", txtTLChuan);
      return false;
    }
    // KL Max
    if (!isDouble(txtTLMax.getText())) {
      showError("Khối Lượng Max phải là số!", txtTLMax);
      return false;
    }

    return true;
  }



  private WeighRecord createWeighRecord(Product product) {
    WeighRecord wr = new WeighRecord();
    wr.setProduct(product);
    wr.setBarcode(txtBarcode.getText().trim());
    wr.setSpecification(txtQuyCach.getText().trim());
    wr.setPlyRating(Integer.parseInt(txtPR.getText().trim()));
    wr.setTreadCode(txtMaGai.getText().trim());
    wr.setMaxWeight(new BigDecimal(txtTLMax.getText().trim()));
    wr.setMinWeight(new BigDecimal(txtTLMin.getText().trim()));
    wr.setLoadIndex(txtChiSoTai.getText().trim());
    wr.setSpeedSymbol(txtTocDo.getText().trim());
    wr.setBrand(txtThuongHieu.getText().trim());
    wr.setType(txtTTTL.getText().trim());
    wr.setDeviation(new BigDecimal(txtTLChuan.getText().trim()));
    wr.setWeighDate(LocalDateTime.now());

    BigDecimal actualWeight = new BigDecimal(lblMiddleValue.getText().trim());
    wr.setActualWeight(actualWeight);

    // so sánh với min-max để set OK/NG
    if (actualWeight != null
            && product.getMinWeight() != null
            && product.getMaxWeight() != null) {

      if (actualWeight.compareTo(product.getMinWeight()) >= 0
              && actualWeight.compareTo(product.getMaxWeight()) <= 0) {
        wr.setResult("OK");
      } else {
        wr.setResult("NG");
      }
    } else {
      wr.setResult("NG");
    }

    // 2. Tạo Machine (dữ liệu giả)
    Machine machine = Machine.builder()
            .model("MC-01")
            .location("Khu A")
            .installationDate(LocalDate.now())
            .build();

    machineDAO.insert(machine);

    wr.setMachine(machine); // tạm null hoặc máy ảo
    return wr;
  }



  // ==== Hàm hỗ trợ ====
  private void showError(String message, JTextField field) {
    JOptionPane.showMessageDialog(null, message);
    field.requestFocus();
  }

  private boolean isInteger(String str) {
    try {
      Integer.parseInt(str.trim());
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private boolean isDouble(String str) {
    try {
      Double.parseDouble(str.trim());
      return true;
    } catch (Exception e) {
      return false;
    }
  }


  //Ham Xoa
  public void RemoveWeighRecord() {
    int[] selectedRows = table.getSelectedRows();
    if (selectedRows.length == 0) {
      JOptionPane.showMessageDialog(null, "Vui lòng chọn ít nhất một dòng để xóa!");
      return;
    }

    List<Integer> idsToDelete = new ArrayList<>();
    for (int row : selectedRows) {
      int modelRow = table.convertRowIndexToModel(row); // chuyển sang index của model
      Object idValue = tableModel.getValueAt(modelRow, tableModel.getColumnCount() - 1); // cột ID cuối
      idsToDelete.add(Integer.parseInt(idValue.toString()));
    }

    // Xác nhận xóa
    int confirm = JOptionPane.showConfirmDialog(null,
            "Bạn có chắc muốn xóa " + idsToDelete.size() + " dòng?",
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
      try {
        // Gọi DAO xóa theo danh sách ID
        for (Integer id : idsToDelete) {
          weighRecordDAO.delete(id);
        }
        JOptionPane.showMessageDialog(null, "Xóa thành công");
        // Refresh lại bảng
        updateTableSearch();
      } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Lỗi khi xóa dữ liệu: " + ex.getMessage());
      }
    }
  }

  // Thêm phương thức setter cho softwareLabel trong HomePanel
  public void setSoftwareLabelText(String text) {
    lblMiddleValue.setText(text);
  }

  public void sendAndReceiveDataTest() {
    daoSerialPortConfig = new DaoSerialPortConfig();
    SerialPortConfig config = daoSerialPortConfig.selectLatest();

    // Lấy tên cổng COM từ JComboBox
    String selectedPort = config.getComPort();
    if (selectedPort == null || selectedPort.isEmpty()) {
      System.out.println("Chưa chọn cổng COM.");
      return;
    }

    // Lấy các thông số cấu hình khác
    int selectedBaudRate =  config.getBaudRate();
    int selectedDataBits = config.getDataBits();
    int selectedStopBits = config.getStopBits();
    String selectedParity = config.getParityBits();

    // Loại bỏ trạng thái khỏi tên cổng
    String portName = selectedPort.split(" ")[0];
    comPort = SerialPort.getCommPort(portName);

    if (comPort != null && comPort.isOpen()) {
      comPort.closePort();
    }

    SerialPort[] availablePorts = SerialPort.getCommPorts();
    boolean PortEqual = false;

    for (SerialPort port : availablePorts) {
      String portNameCheck = port.getSystemPortName();
      boolean isConnected = port.isOpen();
      if(portNameCheck.equals(portName) &&  isConnected == false) {
        PortEqual = true;
      }
    }

    if(PortEqual) {
      if (comPort.openPort()) {
        mainFrameBtnConnect.setText("Đã kết nối");
        mainFrameBtnConnect.setBackground(Color.GREEN);
        mainFrameBtnConnect.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        daoSerialPortConfig.updateLatestStatus("ON");

        comPort.setBaudRate(selectedBaudRate);
        comPort.setNumDataBits(selectedDataBits);
        comPort.setNumStopBits(selectedStopBits);
        comPort.setParity(parseParity(selectedParity));
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 500, 0);

        // Khởi tạo vòng lặp liên tục gửi và nhận dữ liệu
        new Thread(() -> {
          while (true) {  // Vòng lặp liên tục
            try (OutputStream output = comPort.getOutputStream(); InputStream input = comPort.getInputStream()) {
              daoSerialPortConfig = new DaoSerialPortConfig();
              if(daoSerialPortConfig.selectLatest().getStatus().equals("OFF")) {
                closeConnection();
                break;
              }
              // Gửi lệnh "SI\r\n" để yêu cầu giá trị cân
              sendCommand(output, "S\r\n");
              // Nhận phản hồi từ thiết bị cân
              String response = readResponse(input);
              System.out.println("Nhận dữ liệu từ cân: " + response);

              // Lọc và xử lý dữ liệu nhận được
              String number = extractNumber(response);
              setSoftwareLabelText(number);  // Cập nhật giá trị lên giao diện

              // Thêm thời gian chờ trước khi gửi lệnh tiếp theo
              Thread.sleep(200);

            } catch (IOException | NullPointerException | InterruptedException e) {
              e.printStackTrace();
              break;  // Dừng vòng lặp nếu gặp lỗi
            }
          }
        }).start();  // Bắt đầu một luồng mới để xử lý vòng lặp liên tục
      } else {
        System.out.println("Không thể kết nối với " + portName);
        daoSerialPortConfig.updateLatestStatus("OFF");
      }
    }
    else {
      daoSerialPortConfig.updateLatestStatus("OFF");
    }

  }

  private int parseParity(String parity) {
    switch (parity) {
      case "None":
        return SerialPort.NO_PARITY;
      case "Odd":
        return SerialPort.ODD_PARITY;
      case "Even":
        return SerialPort.EVEN_PARITY;
      default:
        throw new IllegalArgumentException("Parity không hợp lệ: " + parity);
    }
  }





  // Các hàm hỗ trợ
  private void sendCommand(OutputStream output, String command) throws IOException {
    output.write(command.getBytes());
    output.flush();
    System.out.println("Đã gửi lệnh: " + command);
  }

  private String readResponse(InputStream input) throws IOException {
    StringBuilder response = new StringBuilder();
    byte[] buffer = new byte[1024];
    int numBytes;
    boolean endOfData = false;
    long startTime = System.currentTimeMillis();
    long timeout = 2000;  // Thời gian chờ tối đa 2 giây

    try {
      while (!endOfData) {
        numBytes = input.read(buffer);
        if (numBytes > 0) {
          response.append(new String(buffer, 0, numBytes));
        }

        // Kiểm tra ký tự kết thúc (LF hoặc CRLF)
        if (response.toString().contains("\n")) {
          endOfData = true;
        }



        // Dừng lại nếu có phản hồi đầy đủ
        try {
          Thread.sleep(100);  // Optional: Thêm thời gian chờ để đảm bảo phản hồi đầy đủ
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();  // Đặt lại trạng thái gián đoạn
          throw new IOException("Thread was interrupted while waiting for data.", e);
        }
      }
    } catch (IOException e) {
      System.out.println("Lỗi trong quá trình nhận dữ liệu: " + e.getMessage());
    }

    return response.toString().trim();
  }

  private String extractNumber(String data) {
    System.out.println("Dữ liệu đầu vào: " + data);

    // Regex tìm số (có thể có dấu '-'), hỗ trợ cả số nguyên & thập phân
    Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    Matcher matcher = pattern.matcher(data);

    if (matcher.find()) {
      double number = Double.parseDouble(matcher.group());

      // Trả về số dạng chuỗi, đảm bảo hiển thị .0 hoặc .5
      return String.format("%.2f", number);
    }

    System.out.println("Không tìm thấy giá trị số.");
    return "0.00";  // Nếu không tìm thấy số, trả về "0.0"
  }

  public boolean isComPortOpen() {
    return comPort != null && comPort.isOpen();
  }

  // Phương thức để đóng cổng COM
  public void closeConnection() {
    if (comPort != null && comPort.isOpen()) {
      comPort.closePort();
      System.out.println("Cổng COM đã được đóng.");
    }
    comPort = null; // Đảm bảo giải phóng tài nguyên
  }


}