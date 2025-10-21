package Dao;

import Model.Product;
import Utils.JdbcHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // ====== INSERT ======
    public void insert(Product entity) {
        String sql = """
            INSERT INTO products 
            (product_code, specification, ply_rating, tread_code, type, load_index, 
             layer_count, brand, speed_symbol, max_weight, min_weight, 
             deviation, weigh_date) 
            OUTPUT INSERTED.id
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = JdbcHelper.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, entity.getProductCode());
            ps.setString(2, entity.getSpecification());
            ps.setObject(3, entity.getPlyRating(), Types.INTEGER);
            ps.setString(4, entity.getTreadCode());
            ps.setString(5, entity.getType());
            ps.setString(6, entity.getLoadIndex());
            ps.setObject(7, entity.getLayerCount(), Types.INTEGER);
            ps.setString(8, entity.getBrand());
            ps.setString(9, entity.getSpeedSymbol());

            ps.setBigDecimal(10, entity.getMaxWeight());
            ps.setBigDecimal(11, entity.getMinWeight());
            ps.setBigDecimal(12, entity.getDeviation());
            ps.setObject(13, entity.getWeighDate());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                entity.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi insert Product: " + e.getMessage(), e);
        }
    }

    // ====== UPDATE ======
    public void update(Product entity) {
        String sql = """
            UPDATE products 
            SET product_code=?, specification=?, ply_rating=?, tread_code=?, type=?, 
                load_index=?, layer_count=?, brand=?, speed_symbol=?, 
                max_weight=?, min_weight=?, deviation=?, weigh_date=? 
            WHERE id=?
        """;
        JdbcHelper.executeUpdate(sql,
                entity.getProductCode(),
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

    // ====== DELETE ======
    public void delete(int id) {
        String sql = "DELETE FROM products WHERE id=?";
        JdbcHelper.executeUpdate(sql, id);
    }

    // ====== SELECT ======
    public Product selectById(int id) {
        String sql = "SELECT * FROM products WHERE id=?";
        List<Product> list = selectBySql(sql, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public Product findByProductCode(String productCode) {
        String sql = "SELECT * FROM products WHERE product_code=?";
        List<Product> list = selectBySql(sql, productCode);
        return list.isEmpty() ? null : list.get(0);
    }

    public Product findByProductCodeExceptId(String productCode, int excludeId) {
        String sql = "SELECT * FROM products WHERE product_code=? AND id<>?";
        List<Product> list = selectBySql(sql, productCode, excludeId);
        return list.isEmpty() ? null : list.get(0);
    }

    public boolean existsByProductCode(String productCode) {
        String sql = "SELECT COUNT(*) FROM products WHERE product_code=?";
        try (ResultSet rs = JdbcHelper.executeQuery(sql, productCode)) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi kiểm tra product_code: " + e.getMessage(), e);
        }
        return false;
    }

    public List<Product> selectAll() {
        String sql = "SELECT * FROM products";
        return selectBySql(sql);
    }

    // ====== CORE MAPPING ======
    private List<Product> selectBySql(String sql, Object... args) {
        List<Product> list = new ArrayList<>();
        try (ResultSet rs = JdbcHelper.executeQuery(sql, args)) {
            while (rs.next()) {
                Product entity = Product.builder()
                        .id(rs.getInt("id"))
                        .productCode(rs.getString("product_code"))
                        .specification(rs.getString("specification"))
                        .plyRating(rs.getObject("ply_rating") != null ? rs.getInt("ply_rating") : null)
                        .treadCode(rs.getString("tread_code"))
                        .type(rs.getString("type"))
                        .loadIndex(rs.getString("load_index"))
                        .layerCount(rs.getObject("layer_count") != null ? rs.getInt("layer_count") : null)
                        .brand(rs.getString("brand"))
                        .speedSymbol(rs.getString("speed_symbol"))
                        .maxWeight(rs.getBigDecimal("max_weight"))
                        .minWeight(rs.getBigDecimal("min_weight"))
                        .deviation(rs.getBigDecimal("deviation"))
                        .weighDate(rs.getTimestamp("weigh_date") != null
                                ? rs.getTimestamp("weigh_date").toLocalDateTime()
                                : null)
                        .build();
                list.add(entity);
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi select Product: " + e.getMessage(), e);
        }
        return list;
    }

    // ====== EXTRA ======
    public List<String> getAllProductCodes() {
        String sql = "SELECT product_code FROM products ORDER BY product_code";
        List<String> list = new ArrayList<>();

        try (ResultSet rs = JdbcHelper.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getString("product_code"));
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy product_code: " + e.getMessage(), e);
        }

        return list;
    }


    public List<Object[]> getAllForTable() {
        String sql = """
    SELECT  p.min_weight, p.max_weight, p.deviation, p.weigh_date,
           p.product_code, p.specification, p.ply_rating, p.tread_code, p.type, p.load_index, p.speed_symbol, 
           p.brand, p.layer_count
    FROM products p 
    ORDER BY p.weigh_date DESC
""";


        List<Object[]> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try {
            ResultSet rs = JdbcHelper.executeQuery(sql);
            int stt = 1;
            while (rs.next()) {
                // Format ngày giờ
                String weighDate = "";
                Timestamp ts = rs.getTimestamp("weigh_date");
                if (ts != null) {
                    weighDate = ts.toLocalDateTime().format(formatter);
                }

                // Lấy và format BigDecimal về 2 số thập phân
                BigDecimal minWeight = rs.getBigDecimal("min_weight");
                BigDecimal deviation = rs.getBigDecimal("deviation");
                BigDecimal maxWeight = rs.getBigDecimal("max_weight");

                Object[] row = {
                        stt++,                               // STT
                        rs.getString("product_code"),        // Mã Lốp
                        rs.getString("specification"),       // Quy Cách
                        rs.getInt("ply_rating"),             // PR
                        rs.getString("tread_code"),          // Mã Gai
                        rs.getString("type"),                // TT/TL
                        rs.getString("load_index"),          // Chỉ Số Tải
                        rs.getString("speed_symbol"),        // Tốc Độ
                        rs.getString("brand"),               // Thương Hiệu
                        rs.getInt("layer_count"),            // Vành
                        minWeight != null ? minWeight.setScale(2, RoundingMode.HALF_UP) : null,       // Min (kg)
                        deviation != null ? deviation.setScale(2, RoundingMode.HALF_UP) : null,       // Chuẩn (kg)
                        maxWeight != null ? maxWeight.setScale(2, RoundingMode.HALF_UP) : null,       // Max (kg)
                        weighDate,                           // Ngày_Giờ
                };
                list.add(row);
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy dữ liệu weigh_records: " + e.getMessage(), e);
        }
        return list;
    }



    public Object[] getOneForTable(String productCode) {
        String sql = """
        SELECT p.min_weight, p.max_weight, p.deviation, p.weigh_date,
               p.product_code, p.specification, p.ply_rating, p.tread_code, p.type, p.load_index, p.speed_symbol, 
               p.brand, p.layer_count
        FROM products p 
        WHERE p.product_code = ?
    """;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try {
            ResultSet rs = JdbcHelper.executeQuery(sql, productCode);

            if (rs.next()) {
                // Format ngày giờ
                String weighDate = "";
                Timestamp ts = rs.getTimestamp("weigh_date");
                if (ts != null) {
                    weighDate = ts.toLocalDateTime().format(formatter);
                }

                // Lấy và format BigDecimal về 2 số thập phân
                BigDecimal minWeight = rs.getBigDecimal("min_weight");
                BigDecimal deviation = rs.getBigDecimal("deviation");
                BigDecimal maxWeight = rs.getBigDecimal("max_weight");

                Object[] row = {
                        1,                                    // STT luôn = 1 vì chỉ lấy 1 bản ghi
                        rs.getString("product_code"),        // Mã Lốp
                        rs.getString("specification"),       // Quy Cách
                        rs.getInt("ply_rating"),             // PR
                        rs.getString("tread_code"),          // Mã Gai
                        rs.getString("type"),                // TT/TL
                        rs.getString("load_index"),          // Chỉ Số Tải
                        rs.getString("speed_symbol"),        // Tốc Độ
                        rs.getString("brand"),               // Thương Hiệu
                        rs.getInt("layer_count"),            // Vành
                        minWeight != null ? minWeight.setScale(2, RoundingMode.HALF_UP) : null,   // Min (kg)
                        deviation != null ? deviation.setScale(2, RoundingMode.HALF_UP) : null,   // Chuẩn (kg)
                        maxWeight != null ? maxWeight.setScale(2, RoundingMode.HALF_UP) : null,   // Max (kg)
                        weighDate,                           // Ngày_Giờ
                };

                rs.getStatement().getConnection().close();
                return row; // trả về đúng 1 bản ghi
            }

            rs.getStatement().getConnection().close();
            return null; // nếu không tìm thấy
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy dữ liệu weigh_records: " + e.getMessage(), e);
        }
    }






}
