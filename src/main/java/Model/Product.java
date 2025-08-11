package Model;


import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Mã sản phẩm
    private Integer  id;

    @Column(name = "product_code", length = 50, nullable = false)
    // Mã lớp lốp
    private String productCode;

    @Column(name = "specification", length = 50)
    // Quy cách
    private String specification;

    @Column(name = "ply_rating")
    // Chỉ số PR (Ply Rating)
    private Integer plyRating;

    @Column(name = "tread_code", length = 50)
    // Mã gai
    private String treadCode;

    @Column(name = "type", length = 10)
    // Loại lốp (TT/TL)
    private String type;

    @Column(name = "load_index", length = 30)
    // Chỉ số tải
    private String loadIndex;

    @Column(name = "layer_count")
    // Số lớp
    private Integer layerCount;

    @Column(name = "brand", length = 100)
    // Thương hiệu
    private String brand;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    // Danh sách bản ghi cân liên quan
    private List<WeighRecord> weighRecords;
}
