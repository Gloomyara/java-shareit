package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.abstraction.model.UserReference;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item implements UserReference {

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

    @OneToMany(fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Comment> comments;

    @Override
    public User getUserReference() {
        return owner;
    }

    @Override
    public void setUserReference(User user) {
        this.owner = user;
    }
}
