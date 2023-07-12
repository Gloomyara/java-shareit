package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.abstraction.model.UserReference;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking implements UserReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time")
    private LocalDateTime start;

    @Column(name = "end_time")
    private LocalDateTime end;

    @OneToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User booker;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Override
    public User getUserReference() {
        return booker;
    }

    @Override
    public void setUserReference(User user) {
        this.booker = user;
    }
}
