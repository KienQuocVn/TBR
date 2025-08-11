package Model;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "machines")
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Mã máy cân
    private Integer  id;

    @Column(name = "model", length = 100)
    // Model máy cân
    private String model;

    @Column(name = "location", length = 100)
    // Vị trí đặt máy
    private String location;

    @Column(name = "installation_date")
    // Ngày lắp đặt
    private LocalDate installationDate;

    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL, orphanRemoval = true)
    // Danh sách bản ghi cân của máy này
    private List<WeighRecord> weighRecords;
}
