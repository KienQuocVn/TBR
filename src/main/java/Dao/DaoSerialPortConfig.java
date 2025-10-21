package Dao;


import Utils.JdbcHelper;
import Model.SerialPortConfig;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DaoSerialPortConfig {

    public void insert(SerialPortConfig entity) {
        String sql = "INSERT INTO SerialPortConfig (comPort, baudRate, dataBits, stopBits, parityBits, status) VALUES (?, ?, ?, ?, ?,?)";
        JdbcHelper.executeUpdate(sql, entity.getComPort(), entity.getBaudRate(), entity.getDataBits(), entity.getStopBits(), entity.getParityBits(),entity.getStatus());
    }



    public void delete(Integer id) {
        String sql = "DELETE FROM SerialPortConfig WHERE Id = ?";
        JdbcHelper.executeUpdate(sql, id);
    }

    public void updateLatestStatus(String newStatus) {
        SerialPortConfig latestConfig = selectLatest(); // Lấy bản ghi mới nhất

        if (latestConfig != null) {
            String sql = "UPDATE SerialPortConfig SET status = ? WHERE Id = ?";
            JdbcHelper.executeUpdate(sql, newStatus, latestConfig.getId()); // Cập nhật trong DB
        } else {
            System.out.println("Không tìm thấy cài đặt SerialPortConfig mới nhất.");
        }
    }


    public List<SerialPortConfig> selectAll() {
        String sql = "SELECT * FROM SerialPortConfig";
        return selectBySql(sql);
    }

    public SerialPortConfig selectLatest() {
        String sql = "SELECT TOP 1 * FROM SerialPortConfig ORDER BY Id DESC"; // Lấy cài đặt mới nhất
        List<SerialPortConfig> list = selectBySql(sql);
        return list.isEmpty() ? null : list.get(0);
    }

    public SerialPortConfig selectById(Integer id) {
        String sql = "SELECT * FROM SerialPortConfig WHERE Id = ?";
        List<SerialPortConfig> list = selectBySql(sql, id);
        return list.isEmpty() ? null : list.get(0);
    }

    private List<SerialPortConfig> selectBySql(String sql, Object... args) {
        List<SerialPortConfig> list = new ArrayList<>();
        try (ResultSet rs = JdbcHelper.executeQuery(sql, args)) {
            while (rs.next()) {
                SerialPortConfig entity = SerialPortConfig.builder()
                        .Id(rs.getInt("Id"))
                        .comPort(rs.getString("comPort"))
                        .baudRate(rs.getInt("baudRate"))
                        .dataBits(rs.getInt("dataBits"))
                        .stopBits(rs.getInt("stopBits"))
                        .parityBits(rs.getString("parityBits"))
                        .status(rs.getString("status"))
                        .build();
                list.add(entity);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while querying SerialPortConfig: " + e.getMessage(), e);
        }
        return list;
    }
}