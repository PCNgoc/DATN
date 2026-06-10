package com.gxsneaker.gxsneaker.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "KICH_THUOC")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KichThuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 20)
    private String ma;

    @Column(nullable = false)
    private Integer size;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
