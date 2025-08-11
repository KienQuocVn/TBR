package Model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "weigh_records")
public class WeighRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Mã bản ghi cân
    private Integer  id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    // Sản phẩm được cân
    private Product product;

    @Column(name = "barcode", length = 100)
    // Mã barcode
    private String barcode;

    @Column(name = "max_weight", precision = 10, scale = 3)
    // Khối lượng tối đa cho phép
    private BigDecimal maxWeight;

    @Column(name = "min_weight", precision = 10, scale = 3)
    // Khối lượng tối thiểu cho phép
    private BigDecimal minWeight;

    @Column(name = "actual_weight", precision = 10, scale = 3)
    // Khối lượng thực tế
    private BigDecimal actualWeight;

    @Column(name = "deviation", precision = 10, scale = 3)
    // Sai lệch
    private BigDecimal deviation;

    @Column(name = "result", length = 10)
    // Kết quả cân (OK/NG)
    private String result;

    @Column(name = "weigh_date")
    // Ngày giờ cân
    private LocalDateTime weighDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id")
    // Máy cân sử dụng
    private Machine machine;
}
