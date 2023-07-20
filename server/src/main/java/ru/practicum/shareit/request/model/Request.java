package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.abstraction.model.EntityClass;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "requests")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Request implements EntityClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private LocalDateTime created;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User author;

    @OneToMany
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Item> items;

}
