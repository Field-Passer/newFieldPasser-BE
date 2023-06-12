package com.example.newfieldpasser.parameter;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class WishBoardId implements Serializable {
    private String member;
    private Long board;
}
