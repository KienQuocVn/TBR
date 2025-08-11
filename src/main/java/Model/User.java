package Model;

import jakarta.persistence.*;
import lombok.*;

// Bảng Users để quản lý tài khoản đăng nhập
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer  id; // Mã người dùng (tự tăng)

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username; // Tên đăng nhập

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash; // Mật khẩu đã mã hóa

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName; // Họ tên đầy đủ

    @Column(name = "role", nullable = false, length = 50)
    private String role; // Vai trò (admin, user,...)

    @Column(name = "created_at", nullable = false)
    private java.time.LocalDateTime createdAt; // Ngày tạo tài khoản

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt; // Ngày cập nhật thông tin
}

