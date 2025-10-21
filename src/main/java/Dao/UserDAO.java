package Dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Model.User;
import Utils.JdbcHelper;

public class UserDAO {

    public void insert(User entity) {
        String sql = """
            INSERT INTO Users (username, password_hash, full_name, role, created_at, updated_at) 
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        JdbcHelper.executeUpdate(sql,
                entity.getUsername(),
                entity.getPasswordHash(),
                entity.getFullName(),
                entity.getRole(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public void update(User entity) {
        String sql = """
            UPDATE Users 
            SET username=?, password_hash=?, full_name=?, role=?, created_at=?, updated_at=? 
            WHERE user_id=?
        """;
        JdbcHelper.executeUpdate(sql,
                entity.getUsername(),
                entity.getPasswordHash(),
                entity.getFullName(),
                entity.getRole(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getId()
        );
    }

    public void delete(int id) {
        String sql = "DELETE FROM Users WHERE user_id=?";
        JdbcHelper.executeUpdate(sql, id);
    }

    public User selectById(int id) {
        String sql = "SELECT * FROM Users WHERE user_id=?";
        List<User> list = selectBySql(sql, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<User> selectAll() {
        String sql = "SELECT * FROM Users";
        return selectBySql(sql);
    }

    public User login(String username, String password) {
        String sql = """
            SELECT *
            FROM Users 
            WHERE username = ? AND password_hash = ?
        """;
        return selectBySql(sql, username, password).stream().findFirst().orElse(null);
    }

    private List<User> selectBySql(String sql, Object... args) {
        List<User> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.executeQuery(sql, args);
            while (rs.next()) {
                User entity = new User();
                entity.setId(rs.getInt("user_id"));
                entity.setUsername(rs.getString("username"));
                entity.setPasswordHash(rs.getString("password_hash"));
                entity.setFullName(rs.getString("full_name"));
                entity.setRole(rs.getString("role"));
                entity.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                entity.setUpdatedAt(rs.getTimestamp("updated_at") != null
                        ? rs.getTimestamp("updated_at").toLocalDateTime()
                        : null);
                list.add(entity);
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}

