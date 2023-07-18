package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.abstraction.model.EntityClass;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item implements EntityClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User owner;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Request request;

    @OneToMany
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Comment> comments;

}
