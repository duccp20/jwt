package com.example.jwt.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;


import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    /*- Natural id có thể dùng để tìm entity mà không cần dùng đến khóa chính.
- Natural id hỗ trợ các thao tác như tìm kiếm, phân trang, sắp xếp một cách hiệu quả.
- Giúp tăng performance khi search vì không cần join với bảng khóa chính.
    */
    @NaturalId
    private RoleName name;
}
