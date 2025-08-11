package Dao;

import Model.Machine;
import Model.Product;
import Model.WeighRecord;
import Utils.JdbcHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WeighRecordDAO {

    // Thêm mới bản ghi cân
    public void insert(WeighRecord entity) {
        String sql = """
            INSERT INTO weigh_records 
            (product_id, barcode, max_weight, min_weight, actual_weight, deviation, result, weigh_date, machine_id) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        JdbcHelper.executeUpdate(sql,
                entity.getProduct() != null ? entity.getProduct().getId() : null, // ID sản phẩm
                entity.getBarcode(),         // Mã barcode
                entity.getMaxWeight(),       // Khối lượng tối đa
                entity.getMinWeight(),       // Khối lượng tối thiểu
                entity.getActualWeight(),    // Khối lượng thực tế
                entity.getDeviation(),       // Sai lệch
                entity.getResult(),          // Kết quả
                entity.getWeighDate(),       // Ngày giờ cân
                entity.getMachine() != null ? entity.getMachine().getId() : null // ID máy cân
        );
    }

    // Cập nhật bản ghi cân
    public void update(WeighRecord entity) {
        String sql = """
            UPDATE weigh_records 
            SET product_id=?, barcode=?, max_weight=?, min_weight=?, actual_weight=?, deviation=?, result=?, weigh_date=?, machine_id=? 
            WHERE id=?
        """;
        JdbcHelper.executeUpdate(sql,
                entity.getProduct() != null ? entity.getProduct().getId() : null,
                entity.getBarcode(),
                entity.getMaxWeight(),
                entity.getMinWeight(),
                entity.getActualWeight(),
                entity.getDeviation(),
                entity.getResult(),
                entity.getWeighDate(),
                entity.getMachine() != null ? entity.getMachine().getId() : null,
                entity.getId()
        );
    }

    // Xóa bản ghi cân theo ID
    public void delete(int id) {
        String sql = "DELETE FROM weigh_records WHERE id=?";
        JdbcHelper.executeUpdate(sql, id);
    }

    // Lấy bản ghi cân theo ID
    public WeighRecord selectById(int id) {
        String sql = "SELECT * FROM weigh_records WHERE id=?";
        List<WeighRecord> list = selectBySql(sql, id);
        return list.isEmpty() ? null : list.get(0);
    }

    // Lấy tất cả bản ghi cân
    public List<WeighRecord> selectAll() {
        String sql = "SELECT * FROM weigh_records";
        return selectBySql(sql);
    }


    public List<WeighRecord> selectAllWithDetails() {
        String sql = """
        SELECT wr.*, 
               p.id AS p_id, p.product_code, p.specification, p.ply_rating, p.tread_code, p.type, p.load_index, p.layer_count, p.brand,
               m.id AS m_id, m.model AS m_model, m.location, m.installation_date
        FROM weigh_records wr
        LEFT JOIN products p ON wr.product_id = p.id
        LEFT JOIN machines m ON wr.machine_id = m.id
    """;
        return selectWithDetailsBySql(sql);
    }

    private List<WeighRecord> selectWithDetailsBySql(String sql, Object... args) {
        List<WeighRecord> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.executeQuery(sql, args);
            while (rs.next()) {
                WeighRecord wr = new WeighRecord();
                wr.setId(rs.getInt("id"));
                wr.setBarcode(rs.getString("barcode"));
                wr.setMaxWeight(rs.getBigDecimal("max_weight"));
                wr.setMinWeight(rs.getBigDecimal("min_weight"));
                wr.setActualWeight(rs.getBigDecimal("actual_weight"));
                wr.setDeviation(rs.getBigDecimal("deviation"));
                wr.setResult(rs.getString("result"));
                if (rs.getTimestamp("weigh_date") != null) {
                    wr.setWeighDate(rs.getTimestamp("weigh_date").toLocalDateTime());
                }

                // Product
                if (rs.getObject("p_id") != null) {
                    Product p = new Product();
                    p.setId(rs.getInt("p_id"));
                    p.setProductCode(rs.getString("product_code"));
                    p.setSpecification(rs.getString("specification"));
                    p.setPlyRating(rs.getInt("ply_rating"));
                    p.setTreadCode(rs.getString("tread_code"));
                    p.setType(rs.getString("type"));
                    p.setLoadIndex(rs.getString("load_index"));
                    p.setLayerCount(rs.getInt("layer_count"));
                    p.setBrand(rs.getString("brand"));
                    wr.setProduct(p);
                }

                // Machine
                if (rs.getObject("m_id") != null) {
                    Machine m = new Machine();
                    m.setId(rs.getInt("m_id"));
                    m.setModel(rs.getString("m_model"));
                    m.setLocation(rs.getString("location"));
                    if (rs.getDate("installation_date") != null) {
                        m.setInstallationDate(rs.getDate("installation_date").toLocalDate());
                    }
                    wr.setMachine(m);
                }

                list.add(wr);
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }




    // Thực thi truy vấn SQL và ánh xạ dữ liệu sang đối tượng WeighRecord
    private List<WeighRecord> selectBySql(String sql, Object... args) {
        List<WeighRecord> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.executeQuery(sql, args);
            while (rs.next()) {
                WeighRecord entity = new WeighRecord();
                entity.setId(rs.getInt("id"));
                // Ở đây chỉ set ID cho Product và Machine, không load toàn bộ object để tránh join phức tạp
                if (rs.getObject("product_id") != null) {
                    entity.setProduct(new Model.Product());
                    entity.getProduct().setId(rs.getInt("product_id"));
                }
                entity.setBarcode(rs.getString("barcode"));
                entity.setMaxWeight(rs.getBigDecimal("max_weight"));
                entity.setMinWeight(rs.getBigDecimal("min_weight"));
                entity.setActualWeight(rs.getBigDecimal("actual_weight"));
                entity.setDeviation(rs.getBigDecimal("deviation"));
                entity.setResult(rs.getString("result"));
                if (rs.getTimestamp("weigh_date") != null) {
                    entity.setWeighDate(rs.getTimestamp("weigh_date").toLocalDateTime());
                }
                if (rs.getObject("machine_id") != null) {
                    entity.setMachine(new Model.Machine());
                    entity.getMachine().setId(rs.getInt("machine_id"));
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
