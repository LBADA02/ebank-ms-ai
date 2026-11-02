package net.lbada.ebankservice.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor @NoArgsConstructor @Data @Builder
public class Beneficiary {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id ;
    private String customerId; //Owner of this saved beneficiary
    private String fullName ;	//Custom name, e.g. My Sister
    private String destinationAccountId	; //UUID	Actual account identifier
    private LocalDateTime createdAt ;
    //updatedAt	LocalDateTime	Last modification
}
