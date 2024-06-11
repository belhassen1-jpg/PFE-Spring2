package com.example.pidev.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChefDepartement implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chef_id", unique = true, nullable = false)
    private Long chefId;
    private String specialisation;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "emp_id", referencedColumnName = "emp_id")
    private Employe employe;

    @OneToOne(mappedBy = "chefDepartement")
    @JsonIgnore
    private Departement departementDirige;
}
