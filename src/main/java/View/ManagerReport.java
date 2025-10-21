package View;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.Color;

import org.apache.poi.ss.usermodel.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import Dao.WeighRecordDAO;
import Model.User;
import com.toedter.calendar.JDateChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ManagerReport extends JPanel {
  public User currentUser;
  JLabel footerLabel;
  private JTable table;
  private DefaultTableModel tableModel;
  public WeighRecordDAO weighRecordDAO = new WeighRecordDAO();
  //====== khai báo các biến thẻ trong class =============
  JDateChooser dateTo;
  JDateChooser dateFrom;
  JTextField codeLopField;
  JTextField quyCachField;
  JTextField maGaiField;
  //====== khai báo ColumnName =============
  String[] columnNames = {"STT", "Mã Lốp", "Quy Cách", "PR", "Mã Gai", "TT/TL", "Chỉ Số Tải", "Tốc Độ", "Thương Hiệu", "Vành", "Mã vạch", "Khối Lượng Cân(kg)", "Kết Quả", "TCTK(kg)", "Min(kg)", "Max(kg)", "Thời Gian Cân"};


  public ManagerReport(User account,JLabel Label) {
    this.currentUser = account;
    this.footerLabel = Label;

    setLayout(new BorderLayout());
    add(createTopPanel(), BorderLayout.NORTH);
    add(createTablePanel(), BorderLayout.CENTER);
    updateTableSearch();
  }

  private JPanel createTopPanel() {
    LocalDateTime now = LocalDateTime.now();
    Date currentDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());

    JPanel topPanel = new JPanel(new BorderLayout());

    // Panel chứa 2 label và input hàng đầu
    JPanel firstRowPanel = new JPanel();
    firstRowPanel.setPreferredSize(new Dimension(1542, 50));
    firstRowPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
    firstRowPanel.setLayout(new BoxLayout(firstRowPanel, BoxLayout.X_AXIS));

    JPanel leftFirstPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    JPanel rightFirstPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    rightFirstPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));

    JLabel fromDateLabel = new JLabel("Từ ngày");
     dateFrom = new JDateChooser();
    dateFrom.setDate(currentDate);
    dateFrom.setDateFormatString("dd/MM/yyyy");
    dateFrom.addPropertyChangeListener("date", evt -> {
      updateTableSearch();
    });

    JLabel toDateLabel = new JLabel("Đến ngày");
     dateTo = new JDateChooser();
    dateTo.setDate(currentDate);
    dateTo.setDateFormatString("dd/MM/yyyy");
    dateTo.addPropertyChangeListener("date", evt -> {
      updateTableSearch();
    });



    JButton btnExport = new JButton("Export Excel");
    btnExport.addActionListener(e -> {
      ExcelEportData();
    });
    formatLabel(fromDateLabel);
    formatLabel(toDateLabel);
    dateFrom.setPreferredSize(new Dimension(170, 25));
    dateTo.setPreferredSize(new Dimension(170, 25));
    btnExport.setPreferredSize(new Dimension(150, 40));

    leftFirstPanel.add(fromDateLabel);
    leftFirstPanel.add(dateFrom);
    leftFirstPanel.add(Box.createHorizontalStrut(20));
    leftFirstPanel.add(toDateLabel);
    leftFirstPanel.add(dateTo);

    rightFirstPanel.add(btnExport);

    firstRowPanel.add(leftFirstPanel);
    firstRowPanel.add(Box.createHorizontalGlue());
    firstRowPanel.add(rightFirstPanel);

    // Panel chứa 2 label và input hàng thứ hai
    JPanel secondRowPanel = new JPanel();
    secondRowPanel.setPreferredSize(new Dimension(1542, 50));
    secondRowPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
    secondRowPanel.setLayout(new BoxLayout(secondRowPanel, BoxLayout.X_AXIS));

    JPanel leftSecondPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    JPanel rightSecondPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    rightSecondPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));

    JLabel codeLopLabel = new JLabel("Mã Lốp");
     codeLopField = new JTextField();
    codeLopField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        updateTableSearch();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        updateTableSearch();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        updateTableSearch();
      }
    });


    JLabel quyCachLabel = new JLabel("Quy Cách");
     quyCachField = new JTextField();
    quyCachField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        updateTableSearch();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        updateTableSearch();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        updateTableSearch();
      }
    });


    JLabel maGaiLabel = new JLabel("Mã Gai");
     maGaiField = new JTextField();
    maGaiField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        updateTableSearch();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        updateTableSearch();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        updateTableSearch();
      }
    });


    formatLabel(codeLopLabel);
    formatLabel(quyCachLabel);
    formatLabel(maGaiLabel);
    codeLopField.setPreferredSize(new Dimension(170, 25));
    quyCachField.setPreferredSize(new Dimension(170, 25));
    maGaiField.setPreferredSize(new Dimension(170, 25));

    leftSecondPanel.add(codeLopLabel);
    leftSecondPanel.add(codeLopField);
    leftSecondPanel.add(Box.createHorizontalStrut(20));
    leftSecondPanel.add(quyCachLabel);
    leftSecondPanel.add(quyCachField);
    leftSecondPanel.add(maGaiLabel);
    leftSecondPanel.add(maGaiField);


    secondRowPanel.add(leftSecondPanel);
    secondRowPanel.add(Box.createHorizontalGlue());
    secondRowPanel.add(rightSecondPanel);

    // Gộp hai hàng vào topPanel
    JPanel combinedPanel = new JPanel(new GridLayout(2, 1, 0, 10));
    combinedPanel.add(firstRowPanel);
    combinedPanel.add(secondRowPanel);

    topPanel.add(combinedPanel, BorderLayout.CENTER);

    return topPanel;
  }

  private void formatLabel(JLabel label) {
    label.setOpaque(true);
    label.setForeground(Color.BLACK);
    label.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
  }

  private JPanel createTablePanel() {
    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.setPreferredSize(new Dimension(1542, 350));
    bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));


    // Khởi tạo table model không cho sửa
    tableModel = new DefaultTableModel( columnNames,0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false; // tất cả cell đều không cho sửa
      }
    };
    table = new JTable(tableModel);
    styleTable2(table);







    JScrollPane scrollPane = new JScrollPane(table);
    bottomPanel.add(scrollPane, BorderLayout.CENTER);

    return bottomPanel;
  }



  private void styleTable2(JTable table) {
    table.setRowHeight(30);
    table.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
    table.getTableHeader().setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
    table.getTableHeader().setBackground(new Color(255, 153, 51));
    table.getTableHeader().setForeground(Color.BLACK);
    table.setGridColor(Color.GRAY);
    table.setSelectionBackground(new Color(204, 229, 255));
    table.setSelectionForeground(Color.BLACK);

    TableColumnModel columnModel = table.getColumnModel();
    columnModel.getColumn(0).setPreferredWidth(50);
    columnModel.getColumn(1).setPreferredWidth(150);
    columnModel.getColumn(2).setPreferredWidth(100);
    columnModel.getColumn(3).setPreferredWidth(100);
    columnModel.getColumn(4).setPreferredWidth(100);
    columnModel.getColumn(5).setPreferredWidth(50);
    columnModel.getColumn(6).setPreferredWidth(100);
    columnModel.getColumn(7).setPreferredWidth(50);
    columnModel.getColumn(8).setPreferredWidth(100);
    columnModel.getColumn(9).setPreferredWidth(100);
    columnModel.getColumn(10).setPreferredWidth(100);
    columnModel.getColumn(11).setPreferredWidth(70);
    columnModel.getColumn(12).setPreferredWidth(70);
    columnModel.getColumn(13).setPreferredWidth(70);
    columnModel.getColumn(14).setPreferredWidth(70);
    columnModel.getColumn(15).setPreferredWidth(70);
    columnModel.getColumn(16).setPreferredWidth(100);

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


  // ===== Hàm tìm kiếm theo ngày và các thẻ input =====

  private void updateTableSearch() {
    try {
      java.util.Date utilFrom = dateFrom.getDate();
      java.util.Date utilTo   = dateTo.getDate();

      java.sql.Date sqlFrom = new java.sql.Date(utilFrom.getTime());
      java.sql.Date sqlTo   = new java.sql.Date(utilTo.getTime());

      String codeLop = codeLopField.getText();
      String quyCach = quyCachField.getText();
      String maGai   = maGaiField.getText();

      List<Object[]> allRows = weighRecordDAO.searchProductsWithDate(
              sqlFrom, sqlTo, codeLop, quyCach, maGai
      );

      tableModel.setRowCount(0);
      int stt = 1;
      for (Object[] row : allRows) {
        Object[] newRow = new Object[columnNames.length];
        newRow[0] = stt++;
        for (int i = 1; i < columnNames.length; i++) {
          newRow[i] = row[i];
        }
        tableModel.addRow(newRow);
      }
    } catch (Exception e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(null, "Lỗi khi load dữ liệu bảng: " + e.getMessage());
    }
  }

  public void ExcelEportData() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Chọn nơi lưu file Excel");
    int userSelection = fileChooser.showSaveDialog(null);

    if (userSelection == JFileChooser.APPROVE_OPTION) {
      File fileToSave = fileChooser.getSelectedFile();

      // Thêm đuôi .xlsx nếu chưa có
      if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".xlsx")) {
        fileToSave = new File(fileToSave.getAbsolutePath() + ".xlsx");
      }

      try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = workbook.createSheet("Báo cáo");

        // ===== Tạo font & style cho tiêu đề =====
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);

        // Merge ô cho tiêu đề
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Bảng Báo Cáo Cân BTR");
        titleCell.setCellStyle(titleStyle);

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnNames.length - 1));

        // ===== Tạo style cho header =====
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);

        // Ghi hàng header (dòng 1 -> index = 1 vì title là dòng 0)
        Row headerRow = sheet.createRow(1);
        for (int i = 0; i < columnNames.length; i++) {
          Cell cell = headerRow.createCell(i);
          cell.setCellValue(columnNames[i]);
          cell.setCellStyle(headerStyle);
        }

        // ===== Ghi dữ liệu từ JTable =====
        int rowCount = 2; // bắt đầu từ dòng 2
        for (int i = 0; i < tableModel.getRowCount(); i++) {
          Row row = sheet.createRow(rowCount++);
          for (int j = 0; j < tableModel.getColumnCount(); j++) {
            Object value = tableModel.getValueAt(i, j);
            Cell cell = row.createCell(j);

            if (value == null) {
              cell.setCellValue("");
            } else if (value instanceof Number) {
              // Nếu là số, ghi kiểu numeric
              cell.setCellValue(((Number) value).doubleValue());
            } else {
              // Nếu là text, giữ nguyên
              cell.setCellValue(value.toString());
            }
          }
        }


        // Auto resize cột
        for (int i = 0; i < columnNames.length; i++) {
          sheet.autoSizeColumn(i);
        }

        // Lưu file
        try (FileOutputStream fos = new FileOutputStream(fileToSave)) {
          workbook.write(fos);
        }

        JOptionPane.showMessageDialog(null, "Xuất Excel thành công: " + fileToSave.getAbsolutePath());
      } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Lỗi khi xuất Excel: " + ex.getMessage());
      }
    }
  }

  public void loadData() {
    updateTableSearch();
  }

}