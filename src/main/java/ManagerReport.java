import javax.swing.*;
import java.awt.*;
import com.toedter.calendar.JDateChooser;

public class ManagerReport extends JPanel {
  public ManagerReport() {
    setLayout(new BorderLayout());
    add(createTopPanel(), BorderLayout.NORTH);
    add(createBottomPanel(), BorderLayout.CENTER);
  }

  private JPanel createTopPanel() {
    JPanel topPanel = new JPanel(new BorderLayout());

    // Panel chứa 4 label trên
    JPanel labelPanel = new JPanel(new GridLayout(1, 4, 10, 10));
    labelPanel.setPreferredSize(new Dimension(1542, 40));

    JLabel label1 = new JLabel("a", SwingConstants.CENTER);
    JLabel label2 = new JLabel("b", SwingConstants.CENTER);
    JLabel label3 = new JLabel("c", SwingConstants.CENTER);
    JLabel label4 = new JLabel("d", SwingConstants.CENTER);

    formatLabel(label1);
    formatLabel(label2);
    formatLabel(label3);
    formatLabel(label4);

    labelPanel.add(label1);
    labelPanel.add(label2);
    labelPanel.add(label3);
    labelPanel.add(label4);

    // Panel chứa 4 textfield dưới
    JPanel textFieldPanel = new JPanel(new GridLayout(1, 4, 10, 10));
    textFieldPanel.setPreferredSize(new Dimension(1542, 40));

    JTextField textField1 = new JTextField();
    JTextField textField2 = new JTextField();
    JTextField textField3 = new JTextField();
    JTextField textField4 = new JTextField();

    textFieldPanel.add(textField1);
    textFieldPanel.add(textField2);
    textFieldPanel.add(textField3);
    textFieldPanel.add(textField4);

    // Gộp label + textfield vào topPanel
    topPanel.add(labelPanel, BorderLayout.NORTH);
    topPanel.add(textFieldPanel, BorderLayout.SOUTH);

    return topPanel;
  }

  private void formatLabel(JLabel label) {
    label.setOpaque(true);
    label.setForeground(Color.BLACK);
    label.setFont(new Font("Arial", Font.BOLD, 16));
  }

  private JPanel createBottomPanel() {
    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.setPreferredSize(new Dimension(1542, 350));

    // Panel chứa JCalendar và nút Search
    JDateChooser dateFrom = new JDateChooser();
    JDateChooser dateTo = new JDateChooser();
    // Chỉnh kích thước JDateChooser
    dateFrom.setPreferredSize(new Dimension(170, 25));
    dateTo.setPreferredSize(new Dimension(170, 25));
    JButton btnSearch = new JButton("Search");
    JButton btnExport = new JButton("Export Excel");

    JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
    contentPanel.setBackground(Color.GRAY);

    contentPanel.add(new JLabel("Từ ngày:"));
    contentPanel.add(dateFrom);
    contentPanel.add(new JLabel("Đến ngày:"));
    contentPanel.add(dateTo);
    contentPanel.add(btnSearch);
    contentPanel.add(Box.createHorizontalStrut(850));
    contentPanel.add(btnExport);

    // Tạo bảng chính giữa
    String[] columnNames = {"Cột 1", "Cột 2", "Cột 3", "Cột 4"};
    Object[][] data = {
        {"A1", "B1", "C1", "D1"},
        {"A2", "B2", "C2", "D2"},
        {"A3", "B3", "C3", "D3"},
        {"A4", "B4", "C4", "D4"}
    };
    JTable table = new JTable(data, columnNames);
    JScrollPane scrollPane = new JScrollPane(table);

    // Thêm vào bottomPanel
    bottomPanel.add(contentPanel, BorderLayout.NORTH);
    bottomPanel.add(scrollPane, BorderLayout.CENTER);

    return bottomPanel;
  }
}
