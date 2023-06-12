package com.example.newfieldpasser.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
@Table(name = "district")
public class District {
    @Id
    @Column(name = "district_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int districtId;

    @Column(name = "district_name")
    private String districtName;

    @OneToMany(mappedBy = "district")
    private List<Board> postList;
}
