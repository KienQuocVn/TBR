package Model;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @Column(name = "product_code", length = 50, nullable = false,unique = true)
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

    @Column(name = "speed_symbol", length = 10)
    private String speedSymbol;   // Tốc độ (Speed Symbol)

    @Column(name = "max_weight", precision = 10, scale = 3)
    // Khối lượng tối đa cho phép
    private BigDecimal maxWeight;

    @Column(name = "min_weight", precision = 10, scale = 3)
    // Khối lượng tối thiểu cho phép
    private BigDecimal minWeight;



    @Column(name = "deviation", precision = 10, scale = 3)
    // khối lượng chuẩn
    private BigDecimal deviation;

    @Column(name = "weigh_date")
    // Ngày giờ cân
    private LocalDateTime weighDate;


    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    // Danh sách bản ghi cân liên quan
    private List<WeighRecord> weighRecords;
}
