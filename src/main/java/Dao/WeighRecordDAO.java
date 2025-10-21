package Dao;

import Model.Machine;
import Model.Product;
import Model.WeighRecord;
import Utils.JdbcHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class WeighRecordDAO {

    // ===== INSERT =====
    public void insert(WeighRecord entity) {
        String sql = """
        INSERT INTO weigh_records (
            product_id, barcode, actual_weight, result, machine_id,
            specification, ply_rating, tread_code, type, load_index,
            layer_count, brand, speed_symbol, max_weight, min_weight,
            deviation, weigh_date
        )
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;
        JdbcHelper.executeUpdate(sql,
                entity.getProduct() != null ? entity.getProduct().getId() : null,
                entity.getBarcode(),
                entity.getActualWeight(),
                entity.getResult(),
                entity.getMachine() != null ? entity.getMachine().getId() : null,
                entity.getSpecification(),
                entity.getPlyRating(),
                entity.getTreadCode(),
                entity.getType(),
                entity.getLoadIndex(),
                entity.getLayerCount(),
                entity.getBrand(),
                entity.getSpeedSymbol(),
                entity.getMaxWeight(),
                entity.getMinWeight(),
                entity.getDeviation(),
                entity.getWeighDate()
        );
    }

    // ===== UPDATE =====
    public void update(WeighRecord entity) {
        String sql = """
        UPDATE weigh_records
        SET product_id = ?, barcode = ?, actual_weight = ?, result = ?, machine_id = ?,
            specification = ?, ply_rating = ?, tread_code = ?, type = ?, load_index = ?,
            layer_count = ?, brand = ?, speed_symbol = ?, max_weight = ?, min_weight = ?,
            deviation = ?, weigh_date = ?
        WHERE id = ?
    """;
        JdbcHelper.executeUpdate(sql,
                entity.getProduct() != null ? entity.getProduct().getId() : null,
                entity.getBarcode(),
                entity.getActualWeight(),
                entity.getResult(),
                entity.getMachine() != null ? entity.getMachine().getId() : null,
                entity.getSpecification(),
                entity.getPlyRating(),
                entity.getTreadCode(),
                entity.getType(),
                entity.getLoadIndex(),
                entity.getLayerCount(),
                entity.getBrand(),
                entity.getSpeedSymbol(),
                entity.getMaxWeight(),
                entity.getMinWeight(),
                entity.getDeviation(),
                entity.getWeighDate(),
                entity.getId()
        );
    }

    // ===== DELETE =====
    public void delete(int id) {
        String sql = "DELETE FROM weigh_records WHERE id = ?";
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

    // Xóa theo product_id
    public void deleteByProductId(int productId) {
        String sql = "DELETE FROM weigh_records WHERE product_id = ?";
        JdbcHelper.executeUpdate(sql, productId);
    }

    // Ánh xạ dữ liệu ResultSet -> WeighRecord
    private List<WeighRecord> selectBySql(String sql, Object... args) {
        List<WeighRecord> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.executeQuery(sql, args);
            while (rs.next()) {
                WeighRecord entity = new WeighRecord();
                entity.setId(rs.getInt("id"));

                if (rs.getObject("product_id") != null) {
                    entity.setProduct(new Model.Product());
                    entity.getProduct().setId(rs.getInt("product_id"));
                }

                entity.setBarcode(rs.getString("barcode"));
                entity.setActualWeight(rs.getBigDecimal("actual_weight"));
                entity.setResult(rs.getString("result"));

                if (rs.getObject("machine_id") != null) {
                    entity.setMachine(new Model.Machine());
                    entity.getMachine().setId(rs.getInt("machine_id"));
                }

                entity.setSpecification(rs.getString("specification"));
                entity.setPlyRating(rs.getInt("ply_rating"));
                entity.setTreadCode(rs.getString("tread_code"));
                entity.setType(rs.getString("type"));
                entity.setLoadIndex(rs.getString("load_index"));
                entity.setLayerCount(rs.getInt("layer_count"));
                entity.setBrand(rs.getString("brand"));
                entity.setSpeedSymbol(rs.getString("speed_symbol"));
                entity.setMaxWeight(rs.getBigDecimal("max_weight"));
                entity.setMinWeight(rs.getBigDecimal("min_weight"));
                entity.setDeviation(rs.getBigDecimal("deviation"));

                Timestamp ts = rs.getTimestamp("weigh_date");
                if (ts != null) {
                    entity.setWeighDate(ts.toLocalDateTime());
                }

                list.add(entity);
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }




    public List<Object[]> searchProductsWithDate(
            java.sql.Date fromDate,
            java.sql.Date toDate,
            String codeLop,
            String quyCach,
            String maGai
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT wr.id AS wr_id, wr.min_weight, wr.max_weight, wr.actual_weight, wr.deviation, wr.weigh_date, wr.result,
               p.product_code, wr.specification, wr.ply_rating, wr.tread_code, wr.type, wr.load_index, wr.speed_symbol, 
               wr.brand, wr.layer_count, wr.barcode
        FROM weigh_records wr
        LEFT JOIN products p ON wr.product_id = p.id
        WHERE CAST(wr.weigh_date AS DATE) BETWEEN ? AND ?

    """);

        List<Object> params = new ArrayList<>();
        params.add(fromDate);
        params.add(toDate);

        if (codeLop != null && !codeLop.trim().isEmpty()) {
            sql.append(" AND p.product_code LIKE ? ");
            params.add("%" + codeLop.trim() + "%");
        }
        if (quyCach != null && !quyCach.trim().isEmpty()) {
            sql.append(" AND wr.specification LIKE ? ");
            params.add("%" + quyCach.trim() + "%");
        }
        if (maGai != null && !maGai.trim().isEmpty()) {
            sql.append(" AND wr.tread_code LIKE ? ");
            params.add("%" + maGai.trim() + "%");
        }

        sql.append(" ORDER BY wr.weigh_date DESC ");

        List<Object[]> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (ResultSet rs = JdbcHelper.executeQuery(sql.toString(), params.toArray())) {
            int stt = 1;
            while (rs.next()) {
                String weighDate = "";
                Timestamp ts = rs.getTimestamp("weigh_date");
                if (ts != null) {
                    weighDate = ts.toLocalDateTime().format(formatter);
                }

                BigDecimal minWeight = rs.getBigDecimal("min_weight");
                BigDecimal deviation = rs.getBigDecimal("deviation");
                BigDecimal maxWeight = rs.getBigDecimal("max_weight");
                BigDecimal actualWeight = rs.getBigDecimal("actual_weight");

                Object[] row = {
                        stt++,                                // STT
                        rs.getString("product_code"),         // Mã Lốp
                        rs.getString("specification"),        // Quy Cách
                        rs.getInt("ply_rating"),              // PR
                        rs.getString("tread_code"),           // Mã Gai
                        rs.getString("type"),                 // TT/TL
                        rs.getString("load_index"),           // Chỉ Số Tải
                        rs.getString("speed_symbol"),         // Tốc Độ
                        rs.getString("brand"),                // Thương Hiệu
                        rs.getInt("layer_count"),             // Vành
                        rs.getString("barcode"),              // Mã vạch
                        actualWeight != null ? actualWeight.setScale(2, RoundingMode.HALF_UP) : null,
                        rs.getString("result"),
                        deviation != null ? deviation.setScale(2, RoundingMode.HALF_UP) : null,
                        minWeight != null ? minWeight.setScale(2, RoundingMode.HALF_UP) : null,
                        maxWeight != null ? maxWeight.setScale(2, RoundingMode.HALF_UP) : null,
                        weighDate,

                };
                list.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm kiếm weigh_records: " + e.getMessage(), e);
        }
        return list;
    }




    public List<Object[]> searchProductsWithDate(java.sql.Date fromDate, java.sql.Date toDate) {
        StringBuilder sql = new StringBuilder("""
        SELECT wr.id AS wr_id, wr.min_weight, wr.max_weight, wr.actual_weight, wr.deviation, wr.weigh_date, wr.result,
               p.product_code, wr.specification, wr.ply_rating, wr.tread_code, wr.type, wr.load_index, wr.speed_symbol, 
               wr.brand, wr.layer_count, wr.barcode
        FROM weigh_records wr
        LEFT JOIN products p ON wr.product_id = p.id
        WHERE CAST(wr.weigh_date AS DATE) BETWEEN ? AND ?
        ORDER BY wr.weigh_date DESC
    """);

        List<Object> params = new ArrayList<>();
        params.add(fromDate);
        params.add(toDate);

        List<Object[]> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (ResultSet rs = JdbcHelper.executeQuery(sql.toString(), params.toArray())) {
            int stt = 1;
            while (rs.next()) {
                // Format thời gian
                String weighDate = "";
                Timestamp ts = rs.getTimestamp("weigh_date");
                if (ts != null) {
                    weighDate = ts.toLocalDateTime().format(formatter);
                }

                // Format số liệu
                BigDecimal minWeight = formatDecimal(rs.getBigDecimal("min_weight"));
                BigDecimal deviation = formatDecimal(rs.getBigDecimal("deviation"));
                BigDecimal maxWeight = formatDecimal(rs.getBigDecimal("max_weight"));
                BigDecimal actualWeight = formatDecimal(rs.getBigDecimal("actual_weight"));

                // Tạo row theo đúng thứ tự cột của JTable
                Object[] row = {
                        stt++,                              // STT
                        rs.getString("product_code"),       // Mã lốp
                        rs.getString("specification"),      // Quy cách
                        actualWeight,                       // KL cân (kg)
                        rs.getString("barcode"),            // Mã vạch
                        rs.getString("result"),             // Kết quả
                        rs.getInt("ply_rating"),            // PR
                        rs.getString("tread_code"),         // Mã gai
                        rs.getString("type"),               // TT/TL
                        rs.getString("speed_symbol"),       // Tốc độ
                        rs.getString("load_index"),         // Chỉ số tải
                        rs.getString("brand"),              // Thương hiệu
                        rs.getInt("layer_count"),           // Vành
                        deviation,                          // TCTK (kg)
                        minWeight,                          // Min (kg)
                        maxWeight,                          // Max (kg)
                        weighDate,                          // Thời gian cân
                        rs.getInt("wr_id")                  // ID (ẩn trong bảng)
                };
                list.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm kiếm weigh_records: " + e.getMessage(), e);
        }
        return list;
    }

    // Hàm tiện ích format BigDecimal
    private BigDecimal formatDecimal(BigDecimal value) {
        return (value != null) ? value.setScale(2, RoundingMode.HALF_UP) : null;
    }




    public List<Object[]> findAllWeighRecords() {
        String sql = """
        SELECT wr.id AS wr_id, wr.actual_weight, wr.barcode, wr.result,
               p.product_code, wr.specification, wr.ply_rating, wr.tread_code, 
               wr.type, wr.speed_symbol, wr.load_index, wr.brand, wr.layer_count,
               wr.deviation, wr.min_weight, wr.max_weight, wr.weigh_date
        FROM weigh_records wr
        INNER JOIN products p ON wr.product_id = p.id
        ORDER BY wr.id DESC
    """;

        List<Object[]> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (ResultSet rs = JdbcHelper.executeQuery(sql)) {
            int stt = 1;
            while (rs.next()) {
                String weighDate = "";
                Timestamp ts = rs.getTimestamp("weigh_date");
                if (ts != null) {
                    weighDate = ts.toLocalDateTime().format(formatter);
                }

                BigDecimal actualWeight = rs.getBigDecimal("actual_weight");
                BigDecimal deviation = rs.getBigDecimal("deviation");
                BigDecimal maxWeight = rs.getBigDecimal("max_weight");
                BigDecimal minWeight = rs.getBigDecimal("min_weight");
                Object[] row = {
                        stt++,                                  // STT
                        rs.getString("product_code"),           // Mã lốp
                        rs.getString("specification"),          // Quy cách
                        actualWeight != null ? actualWeight.setScale(2, RoundingMode.HALF_UP) : null, // KL cân
                        rs.getString("barcode"),                // Mã vạch
                        rs.getString("result"),                 // Kết quả
                        rs.getInt("ply_rating"),                // PR
                        rs.getString("tread_code"),             // Mã gai
                        rs.getString("type"),                   // TT/TL
                        rs.getString("speed_symbol"),           // Tốc độ
                        rs.getString("load_index"),             // Chỉ số tải
                        rs.getString("brand"),                  // Thương hiệu
                        rs.getInt("layer_count"),               // Vành
                        deviation != null ? deviation.setScale(2, RoundingMode.HALF_UP) : null, // TCTK(kg)
                        minWeight != null ? minWeight.setScale(2, RoundingMode.HALF_UP) : null, // Min(kg)
                        maxWeight != null ? maxWeight.setScale(2, RoundingMode.HALF_UP) : null, // Max(kg)
                        weighDate,                              // Thời gian cân
                        rs.getInt("wr_id"),              // id
                };
                list.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy tất cả weigh_records: " + e.getMessage(), e);
        }
        return list;
    }







}
