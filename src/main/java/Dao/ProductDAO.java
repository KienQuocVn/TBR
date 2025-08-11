package Dao;

import Model.Product;
import Utils.JdbcHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ProductDAO {

    public void insert(Product entity) {
        String sql = """
            INSERT INTO products 
            (product_code, specification, ply_rating, tread_code, type, load_index, layer_count, brand) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        JdbcHelper.executeUpdate(sql,
                entity.getProductCode(),
                entity.getSpecification(),
                entity.getPlyRating(),
                entity.getTreadCode(),
                entity.getType(),
                entity.getLoadIndex(),
                entity.getLayerCount(),
                entity.getBrand()
        );
    }

    public void update(Product entity) {
        String sql = """
            UPDATE products 
            SET product_code=?, specification=?, ply_rating=?, tread_code=?, type=?, load_index=?, layer_count=?, brand=? 
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
                entity.getId()
        );
    }

    public void delete(int id) {
        String sql = "DELETE FROM products WHERE id=?";
        JdbcHelper.executeUpdate(sql, id);
    }

    public Product selectById(int id) {
        String sql = "SELECT * FROM products WHERE id=?";
        List<Product> list = selectBySql(sql, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<Product> selectAll() {
        String sql = "SELECT * FROM products";
        return selectBySql(sql);
    }

    private List<Product> selectBySql(String sql, Object... args) {
        List<Product> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.executeQuery(sql, args);
            while (rs.next()) {
                Product entity = new Product();
                entity.setId(rs.getInt("id"));
                entity.setProductCode(rs.getString("product_code"));
                entity.setSpecification(rs.getString("specification"));
                entity.setPlyRating(rs.getInt("ply_rating"));
                entity.setTreadCode(rs.getString("tread_code"));
                entity.setType(rs.getString("type"));
                entity.setLoadIndex(rs.getString("load_index"));
                entity.setLayerCount(rs.getInt("layer_count"));
                entity.setBrand(rs.getString("brand"));
                list.add(entity);
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}

