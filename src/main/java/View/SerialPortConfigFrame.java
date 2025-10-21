package View;

import Controller.SerialPortController;
import Utils.DialogHelper;
import com.fazecast.jSerialComm.SerialPort;
import Dao.DaoSerialPortConfig;
import Model.SerialPortConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SerialPortConfigFrame extends JFrame {
    public boolean isRunning = false;  // Biến flag kiểm tra khi nào dừng
    public SerialPort comPort;
    private SerialPortController serialPortController = new SerialPortController(this);
    JButton connectButton;
    JButton disconnectButton;
    JButton saveButton;
    JLabel statusLabel;
    JComboBox<String> comPortBox;
    JComboBox<String> baudRateBox;
    JComboBox<String> dataBitsBox;
    JComboBox<String> stopBitsBox;
    JComboBox<String> parityBitsBox;
    private DaoSerialPortConfig daoSerialPortConfig = new DaoSerialPortConfig();;
    private JButton mainFrameBtnConnect; // Tham chiếu đến btnConnect của MainFrame
    private ManagerCode managerCodeS; // Add a reference to HomePanel
    private boolean isConnected = false; // Biến kiểm tra trạng thái kết nối
    private boolean CheckCon2HS= false; // Biến kiểm tra trạng thái kết nối
    JLabel footerLabel;
    // Constructor của SerialPortConfigFrame
    public SerialPortConfigFrame(JButton btnConnect, ManagerCode managerCode,JLabel Label) {
        this.mainFrameBtnConnect = btnConnect; // Gán tham chiếu btnConnect
        this.managerCodeS = managerCode;
        this.footerLabel = Label;

        setTitle("Cài đặt");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Ngăn cửa sổ tự đóng
        setSize(420, 400);
        setLocationRelativeTo(null);
        ImageIcon logo = new ImageIcon(ClassLoader.getSystemResource("images/logo.png"));
        Image img = logo.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        logo = new ImageIcon(img);
        setIconImage(logo.getImage());


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!isConnected) {
                    dispose(); // Chỉ đóng khi chưa kết nối
                } else {
                    setVisible(false);
                }
            }
        });



        // Tạo panel chính
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Tiêu đề
        JLabel titleLabel = new JLabel("CẤU HÌNH SERIAL PORT", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel trung tâm chứa cấu hình
        JPanel configPanel = new JPanel();
        configPanel.setLayout(new GridBagLayout());
        configPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // COM PORT
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        configPanel.add(new JLabel("COM PORT:", JLabel.LEFT), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.9;
        comPortBox = new JComboBox<>();
        configPanel.add(comPortBox, gbc);

        // BAUD RATE
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        configPanel.add(new JLabel("BAUD RATE:", JLabel.LEFT), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.9;
        baudRateBox = new JComboBox<>(generateBaudRates());
        configPanel.add(baudRateBox, gbc);

        // DATA BITS
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.1;
        configPanel.add(new JLabel("DATA BITS:", JLabel.LEFT), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.9;
        dataBitsBox = new JComboBox<>(new String[]{"4", "5", "6", "7", "8"});
        configPanel.add(dataBitsBox, gbc);

        // STOP BITS
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.1;
        configPanel.add(new JLabel("STOP BITS:", JLabel.LEFT), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.9;
        stopBitsBox = new JComboBox<>(new String[]{"1", "1.5", "2"});
        configPanel.add(stopBitsBox, gbc);

        // PARITY BITS
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.1;
        configPanel.add(new JLabel("PARITY BITS:", JLabel.LEFT), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.9;
        parityBitsBox = new JComboBox<>(new String[]{"None", "Odd", "Even", "Mark", "Space"});
        configPanel.add(parityBitsBox, gbc);

        // Trạng thái
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.1;
        configPanel.add(new JLabel("Trạng thái:", JLabel.LEFT), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.9;
         statusLabel = new JLabel("Chưa kết nối");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 15));
        statusLabel.setForeground(Color.RED);
        configPanel.add(statusLabel, gbc);

        mainPanel.add(configPanel, BorderLayout.CENTER);

        // Khởi tạo HomeController và các nút

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3, 10, 10));

        connectButton = createButton("Kết Nối");
        connectButton.setPreferredSize(new Dimension(100, 35));

        connectButton.addActionListener(e -> {
            if (comPort == null || !comPort.isOpen()) {
                isRunning = true;

                daoSerialPortConfig.updateLatestStatus("ON");
                // Tạo luồng riêng để gọi sendAndReceiveDataTest
                new Thread(() -> {
                    try {
                       sendAndReceiveDataTest();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Lỗi khi kết nối thiết bị: " + ex.getMessage());
                    }
                }).start();
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Thiết bị đã được kết nối.");
            }
        });



        disconnectButton = createButton("Ngắt");
        disconnectButton.setPreferredSize(new Dimension(100, 35));

        disconnectButton.setEnabled(false);

        disconnectButton.addActionListener(e -> {
            SerialPortConfig config = daoSerialPortConfig.selectLatest();
            if (comPort != null && comPort.isOpen()) {
                isRunning = false;
                closeConnection(); // Gọi phương thức closeConnection để đóng cổng COM

                // Cập nhật trạng thái nút kết nối trên main frame
                mainFrameBtnConnect.setText("Chưa kết nối");
                mainFrameBtnConnect.setBackground(Color.RED);
                mainFrameBtnConnect.setBorder(BorderFactory.createLineBorder(Color.RED));
                statusLabel.setText("Chưa kết nối");
                managerCode.setSoftwareLabelText("0.00");
                statusLabel.setForeground(Color.RED);
                connectButton.setEnabled(true);
                disconnectButton.setEnabled(false);
                daoSerialPortConfig.updateLatestStatus("OFF");
                isConnected = false;
                CheckCon2HS = true;
                this.dispose();
            } else {
                System.out.println("Không có kết nối để ngắt.");
            }

            if(config.getStatus().equals("ON") && CheckCon2HS == false) {
                isRunning = false;
                closeConnection(); // Gọi phương thức closeConnection để đóng cổng COM

                // Cập nhật trạng thái nút kết nối trên main frame
                mainFrameBtnConnect.setText("Chưa kết nối");
                mainFrameBtnConnect.setBackground(Color.RED);
                mainFrameBtnConnect.setBorder(BorderFactory.createLineBorder(Color.RED));
                statusLabel.setText("Chưa kết nối");
                managerCode.setSoftwareLabelText("0.00");
                statusLabel.setForeground(Color.RED);
                connectButton.setEnabled(true);
                disconnectButton.setEnabled(false);
                daoSerialPortConfig.updateLatestStatus("OFF");
                isConnected = false;
                this.dispose();
            }

        });

        saveButton = createButton("Lưu");
        saveButton.setPreferredSize(new Dimension(100, 35));
        saveButton.addActionListener(serialPortController);

        buttonPanel.add(connectButton);
        buttonPanel.add(disconnectButton);
        buttonPanel.add(saveButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);

        // Danh sách các cổng COM khả dụng
        listAvailablePorts();
        // Tải cấu hình cổng COM
        loadSerialPortConfig();



    }

    // Tạo nút button với kiểu dáng đẹp
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180)));
        button.setFocusPainted(false);
        return button;
    }

    // Các phương thức getter cho các thành phần trong frame
    public JButton getConnectButton() { return connectButton; }
    public JButton getDisconnectButton() { return disconnectButton; }
    public JButton getSaveButton() { return saveButton; }
    public JComboBox<String> getComPortBox() { return comPortBox; }
    public JComboBox<String> getBaudRateBox() { return baudRateBox; }
    public JComboBox<String> getDataBitsBox() { return dataBitsBox; }
    public JComboBox<String> getStopBitsBox() { return stopBitsBox; }
    public JComboBox<String> getParityBitsBox() { return parityBitsBox; }

    // Hàm tạo danh sách baud rates
    private String[] generateBaudRates() {
        return new String[] {
                "75 bps", "110 bps", "150 bps", "300 bps", "600 bps", "1200 bps",
                "2400 bps", "4800 bps", "9600 bps", "14400 bps", "19200 bps",
                "38400 bps", "57600 bps", "115200 bps", "128000 bps"
        };
    }

    // Thêm phương thức loadSerialPortConfig
    public void loadSerialPortConfig() {
        // Lấy dữ liệu cấu hình (chỉ lấy 1 bản ghi đầu tiên nếu có)
        daoSerialPortConfig= new DaoSerialPortConfig();
        SerialPortConfig config = daoSerialPortConfig.selectLatest();

        if (config != null) {
            // Cập nhật các JComboBox với giá trị từ cấu hình

            comPortBox.setSelectedItem(config.getComPort());
            baudRateBox.setSelectedItem(config.getBaudRate() + " bps");
            dataBitsBox.setSelectedItem(String.valueOf(config.getDataBits()));
            stopBitsBox.setSelectedItem(String.valueOf(config.getStopBits()));
            parityBitsBox.setSelectedItem(config.getParityBits());
            if(config.getStatus().equals("ON")) {
                statusLabel.setText("Đã kết nối");
                statusLabel.setForeground(Color.GREEN);
                connectButton.setEnabled(false);
                disconnectButton.setEnabled(true);
            }
        }
    }

    public void listAvailablePorts() {
        DefaultComboBoxModel<String> comPortModel = new DefaultComboBoxModel<>();
        SerialPort[] availablePorts = SerialPort.getCommPorts();

        for (SerialPort port : availablePorts) {
            String portName = port.getSystemPortName();
            boolean isConnected = port.isOpen();
            String portDisplayName = portName + (isConnected ? " (Đang kết nối)" : " (Chưa kết nối)");
            comPortModel.addElement(portDisplayName);
        }

        comPortBox.setModel(comPortModel);
    }



    public void sendAndReceiveDataTest() {
        daoSerialPortConfig = new DaoSerialPortConfig();

        // Lấy tên cổng COM từ JComboBox
        String selectedPort = (String) comPortBox.getSelectedItem();
        if (selectedPort == null || selectedPort.isEmpty()) {
            System.out.println("Chưa chọn cổng COM.");
            return;
        }

        // Lấy các thông số cấu hình khác
        String selectedBaudRate = ((String) baudRateBox.getSelectedItem()).replace(" bps", "").trim();
        String selectedDataBits = (String) dataBitsBox.getSelectedItem();
        String selectedStopBits = (String) stopBitsBox.getSelectedItem();
        String selectedParity = (String) parityBitsBox.getSelectedItem();

        // Loại bỏ trạng thái khỏi tên cổng
        String portName = selectedPort.split(" ")[0];
        comPort = SerialPort.getCommPort(portName);

        if (comPort != null && comPort.isOpen()) {
            comPort.closePort();
        }

        if (comPort.openPort()) {
            isConnected = true;
            JOptionPane.showMessageDialog(this, "Kết nối thành công với  " + portName);
            mainFrameBtnConnect.setText("Đã kết nối");
            mainFrameBtnConnect.setBackground(Color.GREEN);
            mainFrameBtnConnect.setBorder(BorderFactory.createLineBorder(Color.GREEN));
            statusLabel.setText("Đã kết nối");
            statusLabel.setForeground(Color.GREEN);
            connectButton.setEnabled(false);
            disconnectButton.setEnabled(true);
            checkComPortStatus();

            comPort.setBaudRate(Integer.parseInt(selectedBaudRate));
            comPort.setNumDataBits(Integer.parseInt(selectedDataBits));
            comPort.setNumStopBits(Integer.parseInt(selectedStopBits));
            comPort.setParity(parseParity(selectedParity));
            comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 500, 0);

            // Khởi tạo vòng lặp liên tục gửi và nhận dữ liệu
            new Thread(() -> {
                while (true) {  // Vòng lặp liên tục
                    try (OutputStream output = comPort.getOutputStream(); InputStream input = comPort.getInputStream()) {
                        // Gửi lệnh "SI\r\n" để yêu cầu giá trị cân
                        sendCommand(output, "S\r\n");

                        // Nhận phản hồi từ thiết bị cân
                        String response = readResponse(input);
                        System.out.println("Nhận dữ liệu từ cân: " + response);

                        // Lọc và xử lý dữ liệu nhận được
                        String number = extractNumberForKLT(response);
                        managerCodeS.setSoftwareLabelText(number);  // Cập nhật giá trị lên giao diện

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


    private void checkComPortStatus() {
        if (comPort != null && comPort.isOpen()) {
            // Nếu cổng COM đã được mở
            connectButton.setEnabled(false);
            disconnectButton.setEnabled(true);
            System.out.println("Cổng " + comPort.getSystemPortName() + " đang mở.");
        } else {
            // Nếu không có cổng COM nào được mở
            connectButton.setEnabled(true);
            disconnectButton.setEnabled(false);
            System.out.println("Không có cổng COM nào đang mở.");
        }
    }

    private String extractNumberForKLT(String data) {
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

    // Phương thức để đóng cổng COM
    private void closeConnection() {
        if (comPort != null && comPort.isOpen()) {
            comPort.closePort();
            System.out.println("Cổng COM đã được đóng.");
        }
        comPort = null; // Đảm bảo giải phóng tài nguyên
    }










}
