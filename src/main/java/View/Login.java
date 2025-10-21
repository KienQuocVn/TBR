package View;

import Dao.UserDAO;
import Model.User;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


public class Login extends JFrame {

    private static final EntityManagerFactory emf;

    static {
        Dotenv dotenv = Dotenv.configure().load();
        String dbUrl = dotenv.get("DB_URL");
        String dbUsername = dotenv.get("DB_USERNAME");
        String dbPassword = dotenv.get("DB_PASSWORD");

        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.connection.url", dbUrl);
        properties.put("hibernate.connection.username", dbUsername);
        properties.put("hibernate.connection.password", dbPassword);

        emf = Persistence.createEntityManagerFactory("CanBTR", properties);
    }



    JTextField loginUser;
    JPasswordField passwordUser;
    public Login() {
        setTitle("Login");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        // Panel chính
        JPanel panel = new JPanel(null);
        setContentPane(panel);

        ImageIcon logo = new ImageIcon(ClassLoader.getSystemResource("images/logo.png"));
        Image img = logo.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        logo = new ImageIcon(img);
        setIconImage(logo.getImage());

        JLabel jLabel1 = new JLabel("LOGIN");
        jLabel1.setFont(new Font("Cantarell", Font.BOLD, 36));
        jLabel1.setForeground(Color.BLACK);
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setBounds(150, 10, 200, 50);
        add(jLabel1);

        JLabel jLabel4 = new JLabel("Tài khoản:");
        jLabel4.setFont(new Font("SF Pro Display", Font.BOLD, 15));
        jLabel4.setForeground(Color.BLACK);
        jLabel4.setBounds(80, 80, 100, 20);
        add(jLabel4);

        loginUser = new JTextField("");
        loginUser.setBackground(new Color(220, 220, 220));
        loginUser.setForeground(Color.BLACK);
        loginUser.setFont(new Font("SF Pro Display", Font.PLAIN, 15));
        loginUser.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        loginUser.setBounds(180, 80, 200, 30);
        add(loginUser);

        JLabel jLabel8 = new JLabel("Mật khẩu:");
        jLabel8.setFont(new Font("SF Pro Display", Font.BOLD, 15));
        jLabel8.setForeground(Color.BLACK);
        jLabel8.setBounds(80, 120, 100, 20);
        add(jLabel8);

        passwordUser = new JPasswordField("");
        passwordUser.setBackground(new Color(220, 220, 220));
        passwordUser.setForeground(Color.BLACK);
        passwordUser.setFont(new Font("SF Pro Display", Font.PLAIN, 15));
        passwordUser.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        passwordUser.setBounds(180, 120, 200, 30);
        passwordUser.addActionListener(e -> LoginEnter());
        add(passwordUser);

        JButton btnLogin = new JButton("Đăng nhập");
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("SF Pro Display", Font.BOLD, 14));
        btnLogin.setBounds(150, 180, 200, 40);
        btnLogin.setBackground(new Color(0, 122, 204));
        btnLogin.setOpaque(true);
        btnLogin.setBorderPainted(false);

        btnLogin.addActionListener(e -> {
            LoginEnter();
        });


        add(btnLogin);

    }

    public void LoginEnter() {
        String username = loginUser.getText().trim();
        String password = new String(passwordUser.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Thực hiện đăng nhập
        try {
            UserDAO accountDAO = new UserDAO();

            User account = accountDAO.login(username, password);

            if (account != null) {
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công! Chào " + account.getUsername());
                this.dispose();
                SwingUtilities.invokeLater(() -> new MainFrame(account)); // truyền account vào MainFrame
            } else {
                JOptionPane.showMessageDialog(this, "Sai tên đăng nhập hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống! Vui lòng thử lại sau.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void initializeData()  {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Kiểm tra nếu tài khoản "admin" đã tồn tại
            Long count = (Long) em.createQuery("SELECT COUNT(a) FROM User a WHERE a.username = :username")
                    .setParameter("username", "admin")
                    .getSingleResult();

            if (count == 0) {
                User account1 = User.builder()
                        .username("admin")
                        .passwordHash("12345")
                        .fullName("Admin")
                        .role("Admin")
                        .createdAt(LocalDateTime.now())
                        .build();

                em.persist(account1);
            }

            // Kiểm tra nếu tài khoản "NV" đã tồn tại
            Long count2 = (Long) em.createQuery("SELECT COUNT(a) FROM User a WHERE a.username = :username")
                    .setParameter("username", "nv")
                    .getSingleResult();

            if (count2 == 0) {

                User account2 = User.builder()
                        .username("nv")
                        .passwordHash("123")
                        .fullName("nv")
                        .role("User")
                        .createdAt(LocalDateTime.now())
                        .build();

                em.persist(account2);
            }



            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to initialize default data.");
        }
    }


    public static void main(String[] args) {
        initializeData();
        SwingUtilities.invokeLater(() -> {
            Login loginFrame = new Login();
            loginFrame.setVisible(true);
        });
    }

}
