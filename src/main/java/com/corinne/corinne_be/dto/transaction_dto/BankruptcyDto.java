package com.corinne.corinne_be.dto.transaction_dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankruptcyDto implements Serializable {
    private static final long serialVersionUID = 6494678977089006639L;
    private String tiker;
    private Long userId;
    private int bankruptcyPrice;
}
