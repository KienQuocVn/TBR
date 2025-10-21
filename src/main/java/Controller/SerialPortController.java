package Controller;



import Dao.DaoSerialPortConfig;
import Model.SerialPortConfig;
import View.SerialPortConfigFrame;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SerialPortController implements ActionListener {
    private SerialPortConfigFrame serialPortConfigFrame;
    private DaoSerialPortConfig daoSerialPortConfig;

    public SerialPortController(SerialPortConfigFrame serialPortConfigFrame) {
        this.serialPortConfigFrame = serialPortConfigFrame;
        this.daoSerialPortConfig = new DaoSerialPortConfig();


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String src = e.getActionCommand();
        switch (src) {
            case "Kết Nối":

                break;
            case "Ngắt":

                break;
            case "Lưu":
                // Lấy dữ liệu từ các JComboBox và lưu vào cơ sở dữ liệu
                // Lấy dữ liệu từ JComboBox và loại bỏ phần trạng thái kết nối
                String comPort = (String) serialPortConfigFrame.getComPortBox().getSelectedItem();
                if (comPort.contains("(")) {
                    comPort = comPort.substring(0, comPort.indexOf("(")).trim();  // Lấy phần trước dấu "("
                }

                int baudRate = Integer.parseInt(((String) serialPortConfigFrame.getBaudRateBox().getSelectedItem()).split(" ")[0]);
                int dataBits = Integer.parseInt((String) serialPortConfigFrame.getDataBitsBox().getSelectedItem());
                int stopBits = Integer.parseInt((String) serialPortConfigFrame.getStopBitsBox().getSelectedItem());
                String parityBits = (String) serialPortConfigFrame.getParityBitsBox().getSelectedItem();

                SerialPortConfig config = SerialPortConfig.builder()
                        .comPort(comPort)
                        .baudRate(baudRate)
                        .dataBits(dataBits)
                        .stopBits(stopBits)
                        .parityBits(parityBits)
                        .status("OFF")
                        .build();

                daoSerialPortConfig.insert(config);
                JOptionPane.showMessageDialog(serialPortConfigFrame, "Cài đặt đã được lưu!");
                break;

        }
    }


}
