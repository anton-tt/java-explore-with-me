package ru.practicum.mainservice.category.model;

import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "categories", schema = "public")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    @Size(min = 1, max = 50, message = "Количество символов в названии должно быть в интервале от 1 до 50 символов.")
    private String name;

}