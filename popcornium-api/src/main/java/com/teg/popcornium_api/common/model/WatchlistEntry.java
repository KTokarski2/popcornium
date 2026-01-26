package com.teg.popcornium_api.common.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "watchlist", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "movie_id"}))
@Getter
@Setter
public class WatchlistEntry extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;
}
