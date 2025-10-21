package Dao;

import Model.Machine;
import Utils.JdbcHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MachineDAO {

    // Thêm mới một máy cân
    public void insert(Machine entity) {
        String sql = """
        INSERT INTO machines (model, location, installation_date)
        OUTPUT INSERTED.id
        VALUES (?, ?, ?)
    """;

        try (Connection con = JdbcHelper.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, entity.getModel());
            ps.setString(2, entity.getLocation());
            ps.setDate(3, entity.getInstallationDate() != null
                    ? java.sql.Date.valueOf(entity.getInstallationDate())
                    : null);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                entity.setId(generatedId); // gán lại id cho object
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi insert Machine: " + e.getMessage());
        }
    }


    // Cập nhật thông tin máy cân
    public void update(Machine entity) {
        String sql = """
            UPDATE machines 
            SET model=?, location=?, installation_date=? 
            WHERE id=?
        """;
        JdbcHelper.executeUpdate(sql,
                entity.getModel(),              // Model máy cân
                entity.getLocation(),           // Vị trí đặt máy
                entity.getInstallationDate(),   // Ngày lắp đặt
                entity.getId()                   // Mã máy cân
        );
    }

    // Xóa máy cân theo ID
    public void delete(int id) {
        String sql = "DELETE FROM machines WHERE id=?";
        JdbcHelper.executeUpdate(sql, id);
    }

    // Lấy thông tin máy cân theo ID
    public Machine selectById(int id) {
        String sql = "SELECT * FROM machines WHERE id=?";
        List<Machine> list = selectBySql(sql, id);
        return list.isEmpty() ? null : list.get(0);
    }

    // Lấy toàn bộ danh sách máy cân
    public List<Machine> selectAll() {
        String sql = "SELECT * FROM machines";
        return selectBySql(sql);
    }

    // Thực thi truy vấn SQL và ánh xạ dữ liệu sang đối tượng Machine
    private List<Machine> selectBySql(String sql, Object... args) {
        List<Machine> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.executeQuery(sql, args);
            while (rs.next()) {
                Machine entity = new Machine();
                entity.setId(rs.getInt("id")); // Mã máy cân
                entity.setModel(rs.getString("model")); // Model máy cân
                entity.setLocation(rs.getString("location")); // Vị trí đặt máy
                if (rs.getDate("installation_date") != null) {
                    entity.setInstallationDate(rs.getDate("installation_date").toLocalDate()); // Ngày lắp đặt
                }
                list.add(entity);
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}
