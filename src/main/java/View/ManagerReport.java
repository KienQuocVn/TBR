package View;

import javax.swing.*;
import java.awt.*;
import com.toedter.calendar.JDateChooser;

public class ManagerReport extends JPanel {
  public ManagerReport() {
    setLayout(new BorderLayout());
    add(createTopPanel(), BorderLayout.NORTH);
    add(createTablePanel(), BorderLayout.CENTER);
  }

  private JPanel createTopPanel() {
    JPanel topPanel = new JPanel(new BorderLayout());

    // Panel chứa 2 label và input hàng đầu
    JPanel firstRowPanel = new JPanel();
    firstRowPanel.setPreferredSize(new Dimension(1542, 50));
    firstRowPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
    firstRowPanel.setLayout(new BoxLayout(firstRowPanel, BoxLayout.X_AXIS));

    JPanel leftFirstPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    JPanel rightFirstPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    rightFirstPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));

    JLabel fromDateLabel = new JLabel("From Date");
    JDateChooser dateFrom = new JDateChooser();
    JLabel toDateLabel = new JLabel("To Date");
    JDateChooser dateTo = new JDateChooser();
    JButton btnExport = new JButton("Export Excel");

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

    JLabel codeLopLabel = new JLabel("Code Lốp");
    JTextField codeLopField = new JTextField();
    JLabel quyCachLabel = new JLabel("Quy Cách");
    JTextField quyCachField = new JTextField();
    JLabel maGaiLabel = new JLabel("Mã Gai");
    JTextField maGaiField = new JTextField();
    JTextField maField = new JTextField(10);
    JButton btnSearch = new JButton("Search");

    formatLabel(codeLopLabel);
    formatLabel(quyCachLabel);
    formatLabel(maGaiLabel);
    codeLopField.setPreferredSize(new Dimension(170, 25));
    quyCachField.setPreferredSize(new Dimension(170, 25));
    maGaiField.setPreferredSize(new Dimension(170, 25));
    maField.setPreferredSize(new Dimension(250, 30));
    btnSearch.setPreferredSize(new Dimension(150, 30));

    leftSecondPanel.add(codeLopLabel);
    leftSecondPanel.add(codeLopField);
    leftSecondPanel.add(Box.createHorizontalStrut(20));
    leftSecondPanel.add(quyCachLabel);
    leftSecondPanel.add(quyCachField);
    leftSecondPanel.add(maGaiLabel);
    leftSecondPanel.add(maGaiField);

    rightSecondPanel.add(maField);
    rightSecondPanel.add(btnSearch);

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
    label.setFont(new Font("Arial", Font.BOLD, 16));
  }

  private JPanel createTablePanel() {
    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.setPreferredSize(new Dimension(1542, 350));
    bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

    String[] columnNames = {"STT", "Code Lốp", "Quy Cách", "PR", "Mã Gai", "TT/TL", "Chỉ Số Tải", "Tốc Độ", "Thương Hiệu", "Vành", "BarCode", "Khối Lượng Cân(kg)", "Kết Quả", "TCTK(kg)", "Min(kg)", "Max(kg)", "Thời Gian Cân", "Ngày LH"};
    Object[][] data = {
            {"1", "PR", "18", "BS81", "152/48", "TT", "100", "A", "Brand1", "R15", "123456", "50", "OK", "45", "40", "60", "12:55", "02/08/2025"},
            {"2", "PR", "18", "BS81", "152/48", "TL", "100", "A", "Brand1", "R15", "123457", "50", "OK", "45", "40", "60", "12:55", "02/08/2025"},
            {"3", "PR", "18", "BS81", "152/48", "TT", "100", "A", "Brand1", "R15", "123458", "50", "OK", "45", "40", "60", "12:55", "02/08/2025"}
    };
    JTable table = new JTable(data, columnNames);
    JScrollPane scrollPane = new JScrollPane(table);

    bottomPanel.add(scrollPane, BorderLayout.CENTER);

    return bottomPanel;
  }
}