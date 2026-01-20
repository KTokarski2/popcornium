package com.teg.popcornium_api.common.model;


import com.teg.popcornium_api.common.model.types.UserRating;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "movie_rating", uniqueConstraints = @UniqueConstraint(columnNames = {"movie_id", "user_id"}))
public class MovieRating extends AbstractEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRating rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
