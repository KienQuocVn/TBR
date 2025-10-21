package View;

import Dao.MachineDAO;
import Dao.ProductDAO;
import Dao.WeighRecordDAO;
import Model.Machine;
import Model.Product;
import Model.User;
import Model.WeighRecord;
import org.apache.poi.ss.usermodel.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.border.Border;

public class ManagerMaLop extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    // ====== Khai báo biến instance ======
    private JTextField txtMaLop, txtQuyCach, txtPR, txtMaGai, txtTTTL, txtChiSoTai;
    private JTextField txtThuongHieu, txtVanh, txtTLMin, txtTLMax, txtTLChuan, txtTocDo;
    private JButton btnAdd, btnDelete, btnFix, btnLoad;

    public User currentUser;
    JLabel footerLabel;

    private boolean isAdding = true; // mặc định là thêm mới
    // ========== khai báo Dao ==============
    public ProductDAO productDAO = new ProductDAO();
    public WeighRecordDAO weighRecordDAO = new WeighRecordDAO();

    //====== khai báo ColumnName =============
    String[] columnNames = {
            "STT", "Mã Lốp", "Quy Cách", "PR", "Mã Gai", "TT/TL", "Chỉ Số Tải",
            "Tốc Độ", "Thương Hiệu", "Vành",
            "Khối Lượng Min (kg)", "Khối Lượng Chuẩn (kg)", "Khối Lượng Max (kg)",
            "Ngày_Giờ"
    };

    public ManagerMaLop(User account,JLabel Label) {
        this.currentUser = account;
        this.footerLabel = Label;

        setLayout(new BorderLayout(10, 10));

        // Panel chứa input và button (xếp dọc)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(createInfoPanel(), BorderLayout.NORTH);   // Input trên
        topPanel.add(createButtonPanel(), BorderLayout.CENTER); // Nút ngay dưới input

        // Thêm vào giao diện chính
        add(topPanel, BorderLayout.NORTH);   // Input + Button
        add(createTablePanel(), BorderLayout.CENTER); // Bảng JTable

        updateTable();
    }


    // Trong class ManagerLop
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new GridLayout(1, 4, 0, 0));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, Color.GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
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
        txtMaLop   = createTextField();
        txtQuyCach = createTextField();
        txtPR      = createTextField();

        return createLabelInputPanel(
                new String[]{"Mã Lốp", "Quy Cách", "PR"},
                new JTextField[]{txtMaLop, txtQuyCach, txtPR}
        );
    }

    // Cột 2
    private JPanel createLabelInputPanelColumn2() {
        txtMaGai = createTextField();
        txtTLMax = createTextField();
        txtTLMin = createTextField();

        return createLabelInputPanel(
                new String[]{"Mã Gai", "KL Max", "KL Min"},
                new JTextField[]{txtMaGai, txtTLMax, txtTLMin}
        );
    }

    // Cột 3
    private JPanel createLabelInputPanelColumn3() {
        txtChiSoTai = createTextField();
        txtTocDo    = createTextField();
        txtVanh     = createTextField();

        return createLabelInputPanel(
                new String[]{"Chỉ Số Tải", "Tốc Độ", "Vành"},
                new JTextField[]{txtChiSoTai, txtTocDo, txtVanh}
        );
    }

    // Cột 4
    private JPanel createLabelInputPanelColumn4() {
        txtThuongHieu = createTextField();
        txtTTTL       = createTextField();
        txtTLChuan    = createTextField();

        return createLabelInputPanel(
                new String[]{"Thương Hiệu", "TT/TL", "KL Chuẩn"},
                new JTextField[]{txtThuongHieu, txtTTTL, txtTLChuan}
        );
    }

    // Hàm tạo panel chung
    private JPanel createLabelInputPanel(String[] labels, JTextField[] fields) {
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
            fields[i].setFont(new Font("Arial", Font.BOLD, 20));
            fields[i].setPreferredSize(new Dimension(0, 35)); // cao 35, rộng tự co
            panel.add(fields[i], gbc);
        }
        return panel;
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


    // ===== Hàm tạo panel nút chức năng =====
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        Dimension btnSize = new Dimension(90, 35);
        Font btnFont = new Font("Arial", Font.BOLD, 15);

        btnAdd = new JButton("Thêm");
        btnAdd.setBackground(new Color(0, 153, 51));   // Xanh lá cây đậm
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setPreferredSize(btnSize);
        btnAdd.setFont(btnFont);
        btnAdd.addActionListener(e -> {
            isAdding = true; // Đặt cờ thêm
            saveProductAndWeighRecord();
        });

        btnDelete = new JButton("Xóa");
        btnDelete.setBackground(new Color(204, 0, 0)); // Đỏ đậm
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setPreferredSize(btnSize);
        btnDelete.setFont(btnFont);
        btnDelete.setEnabled(false); //  không cho bấm lúc đầu
        btnDelete.addActionListener(e -> RemoveSelectedProducts());

        btnFix = new JButton("Sửa");
        btnFix.setBackground(new Color(0, 102, 204));  // Xanh nước biển đậm
        btnFix.setForeground(Color.WHITE);
        btnFix.setPreferredSize(btnSize);
        btnFix.setFont(btnFont);
        btnFix.addActionListener(e -> {
            isAdding = false; // Đặt cờ sửa
            fixProductAndWeighRecord();
        });

        Dimension btnSizeExcel = new Dimension(130, 35);
        btnLoad = new JButton("Nhập EXCEL");
        btnLoad.setBackground(Color.WHITE);
        btnLoad.setForeground(Color.BLACK);
        btnLoad.setPreferredSize(btnSizeExcel);
        btnLoad.setFont(btnFont);
        btnLoad.addActionListener(e -> importFromExcelAdvanced());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnFix);
        buttonPanel.add(btnLoad);

        return buttonPanel;
    }

    private void importFromExcelAdvanced() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xls", "xlsx"));
        int result = fileChooser.showOpenDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = fileChooser.getSelectedFile();
        int successCount = 0;
        List<String> errorLines = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Bỏ header
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    // Lấy dữ liệu từng cột
                    String maLop       = getCellString(row.getCell(0));
                    String quyCach     = getCellString(row.getCell(1));
                    String prStr       = getCellString(row.getCell(2));
                    String maGai       = getCellString(row.getCell(3));
                    String tlMaxStr    = getCellString(row.getCell(4));
                    String tlMinStr    = getCellString(row.getCell(5));
                    String chiSoTai    = getCellString(row.getCell(6));
                    String tocDo       = getCellString(row.getCell(7));
                    String vanhStr     = getCellString(row.getCell(8));
                    String thuongHieu  = getCellString(row.getCell(9));
                    String tttl        = getCellString(row.getCell(10));
                    String tlChuanStr  = getCellString(row.getCell(11));

                    // Validate cơ bản
                    if (maLop.isEmpty()) throw new Exception("Mã Lốp trống");
                    if (!prStr.matches("\\d+")) throw new Exception("PR không hợp lệ");
                    if (!vanhStr.matches("\\d+")) throw new Exception("Vành không hợp lệ");
                    if (!tlMaxStr.matches("\\d+(\\.\\d+)?")) throw new Exception("KL Max không hợp lệ");
                    if (!tlMinStr.matches("\\d+(\\.\\d+)?")) throw new Exception("KL Min không hợp lệ");
                    if (!tlChuanStr.matches("\\d+(\\.\\d+)?")) throw new Exception("KL Chuẩn không hợp lệ");

                    int pr       = Integer.parseInt(prStr);
                    int vanh     = Integer.parseInt(vanhStr);
                    BigDecimal tlMax   = new BigDecimal(tlMaxStr);
                    BigDecimal tlMin   = new BigDecimal(tlMinStr);
                    BigDecimal tlChuan = new BigDecimal(tlChuanStr);

                    // Kiểm tra mã lốp trùng
                    if (productDAO.existsByProductCode(maLop)) {
                        errorLines.add("Dòng " + (i + 1) + ": Mã Lốp trùng (" + maLop + ")");
                        continue;
                    }

                    // Tạo Product
                    Product product = Product.builder()
                            .productCode(maLop)
                            .specification(quyCach)
                            .plyRating(pr)
                            .treadCode(maGai)
                            .type(tttl)
                            .loadIndex(chiSoTai)
                            .brand(thuongHieu)
                            .speedSymbol(tocDo)
                            .layerCount(vanh)
                            .maxWeight(tlMax)
                            .minWeight(tlMin)
                            .deviation(tlChuan)
                            .weighDate(LocalDateTime.now())
                            .build();

                    // Lưu vào DB
                    productDAO.insert(product);
                    successCount++;

                } catch (Exception exRow) {
                    errorLines.add("Dòng " + (i + 1) + ": " + exRow.getMessage());
                }
            }

            // Thông báo kết quả
            String msg = "Nhập Excel hoàn tất!\nThêm thành công: " + successCount + " dòng.";
            if (!errorLines.isEmpty()) {
                msg += "\nCác lỗi:\n" + String.join("\n", errorLines);
            }

            JOptionPane.showMessageDialog(null, msg);
            updateTable();
            footerLabel.setText("Tình trạng: vừa nhập Excel, thành công " + successCount + " dòng");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi nhập Excel: " + ex.getMessage());
        }
    }

    private String getCellString(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.STRING) return cell.getStringCellValue();
        if (cell.getCellType() == CellType.NUMERIC) return String.valueOf((int) cell.getNumericCellValue());
        return "";
    }


    // ===== Hàm tạo bảng JTable =====
    private JPanel createTablePanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setPreferredSize(new Dimension(1542, 350));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));



        // Khởi tạo table model không cho sửa
        tableModel = new DefaultTableModel( columnNames,0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tất cả cell đều không cho sửa
            }
        };
        table = new JTable(tableModel);

        // Áp dụng style
        styleTable2(table);

        // Ẩn cột ID (cột cuối cùng)
        int idColumnIndex = table.getColumnCount() - 1;
        table.getColumnModel().getColumn(idColumnIndex).setMinWidth(0);
        table.getColumnModel().getColumn(idColumnIndex).setMaxWidth(0);
        table.getColumnModel().getColumn(idColumnIndex).setPreferredWidth(0);

        // ================== Thêm xử lý chọn dòng ==================

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            private int lastSelectedRow = -1;

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int selectedRow = table.getSelectedRow();

                if (selectedRow != -1) {
                    if (selectedRow == lastSelectedRow) {
                        //  Nếu bấm lại cùng 1 dòng => reset form
                        clearInputFields();
                        table.clearSelection();
                        btnAdd.setEnabled(true);
                        btnLoad.setEnabled(true);
                        btnDelete.setEnabled(false);
                        btnFix.setEnabled(false);
                        lastSelectedRow = -1;
                    } else {
                        // Chọn dòng mới
                        //  Nếu chọn dòng mới => fill dữ liệu vào form
                        txtMaLop.setText(table.getValueAt(selectedRow, 1).toString());
                        txtQuyCach.setText(table.getValueAt(selectedRow, 2).toString());
                        txtPR.setText(table.getValueAt(selectedRow, 3).toString());
                        txtMaGai.setText(table.getValueAt(selectedRow, 4).toString());
                        txtTTTL.setText(table.getValueAt(selectedRow, 5).toString());
                        txtChiSoTai.setText(table.getValueAt(selectedRow, 6).toString());
                        txtTocDo.setText(table.getValueAt(selectedRow, 7).toString());
                        txtThuongHieu.setText(table.getValueAt(selectedRow, 8).toString());
                        txtVanh.setText(table.getValueAt(selectedRow, 9).toString());
                        txtTLMin.setText(table.getValueAt(selectedRow, 10).toString());
                        txtTLChuan.setText(table.getValueAt(selectedRow, 11).toString());
                        txtTLMax.setText(table.getValueAt(selectedRow, 12).toString());

                        // Bật nút Sửa + Xóa, tắt Thêm + Excel
                        btnAdd.setEnabled(false);
                        btnLoad.setEnabled(false);
                        if(currentUser.getRole().equals("Admin")) {
                            btnDelete.setEnabled(true);
                        }
                        else {
                            btnDelete.setEnabled(false);
                        }

                        btnFix.setEnabled(true);

                        lastSelectedRow = selectedRow;
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        return bottomPanel;
    }

    private void styleTable2(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(255, 153, 51));
        table.getTableHeader().setForeground(Color.BLACK);
        table.setGridColor(Color.GRAY);
        table.setSelectionBackground(new Color(204, 229, 255));
        table.setSelectionForeground(Color.BLACK);
// Thay đổi chế độ chọn
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(30);   // STT
        columnModel.getColumn(1).setPreferredWidth(100);  // Mã Lốp
        columnModel.getColumn(2).setPreferredWidth(150);  // Quy Cách
        columnModel.getColumn(3).setPreferredWidth(50);   // PR
        columnModel.getColumn(4).setPreferredWidth(100);  // Mã Gai
        columnModel.getColumn(5).setPreferredWidth(60);   // TT/TL
        columnModel.getColumn(6).setPreferredWidth(100);  // Chỉ Số Tải
        columnModel.getColumn(7).setPreferredWidth(60);   // Tốc Độ
        columnModel.getColumn(8).setPreferredWidth(120);  // Thương Hiệu
        columnModel.getColumn(9).setPreferredWidth(80);   // Vành
        columnModel.getColumn(10).setPreferredWidth(120); // Trọng Lượng Thấp Nhất
        columnModel.getColumn(11).setPreferredWidth(150); // Trọng Lượng Chuẩn
        columnModel.getColumn(12).setPreferredWidth(120); // Trọng Lượng Cao Nhất
        columnModel.getColumn(13).setPreferredWidth(110); // Ngày và Giờ


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

    private void clearInputFields() {
        txtMaLop.setText("");
        txtQuyCach.setText("");
        txtPR.setText("");
        txtMaGai.setText("");
        txtTTTL.setText("");
        txtChiSoTai.setText("");
        txtThuongHieu.setText("");
        txtVanh.setText("");
        txtTLMin.setText("");
        txtTLMax.setText("");
        txtTLChuan.setText("");
        txtTocDo.setText("");
    }

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


    private boolean validateInput() {
        // Mã Lốp
        String maLop = txtMaLop.getText().trim();
        if (maLop.isEmpty()) {
            showError("Mã Lốp không được để trống!", txtMaLop);
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

    // ==== Hàm thêm ====
    private void saveProductAndWeighRecord() {
        String maLop = txtMaLop.getText().trim();

// Validate chung
        if (!validateInput()) {
            return; // Dữ liệu nhập không hợp lệ
        }

// Kiểm tra mã lốp trùng (chỉ áp dụng khi thêm mới)
        if (isAdding && productDAO.existsByProductCode(maLop)) {
            showError("Mã Lốp đã tồn tại!", txtMaLop);
            return;
        }


        try {
            // 1. Tạo Product từ input
            Product product = Product.builder()
                    .productCode(txtMaLop.getText().trim())
                    .specification(txtQuyCach.getText().trim())
                    .plyRating(Integer.parseInt(txtPR.getText().trim()))
                    .treadCode(txtMaGai.getText().trim())
                    .type(txtTTTL.getText().trim())
                    .loadIndex(txtChiSoTai.getText().trim())
                    .brand(txtThuongHieu.getText().trim())
                    .speedSymbol(txtTocDo.getText().trim())
                    .layerCount(Integer.parseInt(txtVanh.getText().trim()))
                    .maxWeight(new BigDecimal(txtTLMax.getText().trim()))
                    .minWeight(new BigDecimal(txtTLMin.getText().trim()))
                    .deviation(new BigDecimal(txtTLChuan.getText().trim()))
                    .weighDate(LocalDateTime.now())
                    .build();

            // Lưu Product vào DB
            productDAO.insert(product);


            // 4. Thông báo
            JOptionPane.showMessageDialog(null, "Thêm thành công!");
            footerLabel.setText("Tình trạng: bạn vừa mới thêm thành công mã lốp " + product.getProductCode());
            // 5. Reset form và update table
            updateTable();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi lưu: " + ex.getMessage());
        }
    }

    //==== hàm xóa ====
    private void RemoveSelectedProducts() {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn ít nhất 1 dòng để xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Bạn có chắc chắn muốn xóa " + selectedRows.length + " dòng đã chọn không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int row = selectedRows[i];
                String productCode = table.getValueAt(row, 1).toString(); // Cột Mã Lốp

                Product product = productDAO.findByProductCode(productCode);
                if (product != null) {
                    // Xóa weigh_record trước
                    weighRecordDAO.deleteByProductId(product.getId());
                    // Xóa product
                    productDAO.delete(product.getId());
                }
            }

            JOptionPane.showMessageDialog(null, "Đã xóa thành công " + selectedRows.length + " dòng!");
            footerLabel.setText("Tình trạng: xóa " + selectedRows.length + " dòng thành công.");
            clearInputFields();
            updateTable();

            // Reset nút
            btnAdd.setEnabled(true);
            btnLoad.setEnabled(true);
            btnDelete.setEnabled(false);
            btnFix.setEnabled(false);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi xóa: " + ex.getMessage());
        }
    }


    //==== hàm sửa ====
    private void fixProductAndWeighRecord() {
        if (!validateInput()) {
            return; // nếu validate fail thì dừng lại
        }

        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn một sản phẩm để sửa!");
            return;
        }

        try {

            String maLopTable = (String) table.getValueAt(selectedRow, 1);

            // 1. Tìm product theo mã lốp hiện tại
            Product product = productDAO.findByProductCode(maLopTable);
            if (product == null) {
                JOptionPane.showMessageDialog(null, "Không tìm thấy Mã Lốp trong hệ thống!");
                return;
            }

            String maLop = txtMaLop.getText().trim();

            // 2. Kiểm tra trùng mã lốp với sản phẩm khác
            Product duplicate = productDAO.findByProductCodeExceptId(maLop, product.getId());
            if (duplicate != null) {
                JOptionPane.showMessageDialog(null, "Mã Lốp đã tồn tại ở sản phẩm khác!");
                return;
            }


            // 2. Cập nhật dữ liệu product từ input
            product.setProductCode(maLop);
            product.setSpecification(txtQuyCach.getText().trim());
            product.setPlyRating(Integer.parseInt(txtPR.getText().trim()));
            product.setTreadCode(txtMaGai.getText().trim());
            product.setType(txtTTTL.getText().trim());
            product.setLoadIndex(txtChiSoTai.getText().trim());
            product.setSpeedSymbol(txtTocDo.getText().trim());
            product.setBrand(txtThuongHieu.getText().trim());
            product.setLayerCount(Integer.parseInt(txtVanh.getText().trim()));
            product.setMinWeight(new BigDecimal(txtTLMin.getText().trim()));
            product.setDeviation(new BigDecimal(txtTLChuan.getText().trim()));
            product.setMaxWeight(new BigDecimal(txtTLMax.getText().trim()));
            product.setWeighDate(LocalDateTime.now()); // cập nhật thời gian sửa

            productDAO.update(product);



            JOptionPane.showMessageDialog(null, "Cập nhật thành công!");
            footerLabel.setText("Tình trạng: bạn vừa mới xóa thành công mã lốp " + product.getProductCode());
            // Reset form
            clearInputFields();
            updateTable();

            // Reset trạng thái nút
            btnAdd.setEnabled(true);
            btnLoad.setEnabled(true);
            btnDelete.setEnabled(false);
            btnFix.setEnabled(false);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi cập nhật: " + e.getMessage());
        }
    }


    // ===== Hàm updateTable =====
    private void updateTable() {
        try {
            // Lấy dữ liệu mới từ DB (trả về List<Object[]> để phù hợp JTable)
            List<Object[]> allRows = productDAO.getAllForTable();

            // Xóa dữ liệu cũ trong model
            tableModel.setRowCount(0);

            int stt = 1;
            for (Object[] row : allRows) {
                // Thêm STT vào đầu mỗi dòng
                Object[] newRow = new Object[columnNames.length];
                newRow[0] = stt++; // STT

                // Copy dữ liệu row vào (bắt đầu từ cột 1 trong columnNames)
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


    public void loadData() {
        updateTable();
    }

}
