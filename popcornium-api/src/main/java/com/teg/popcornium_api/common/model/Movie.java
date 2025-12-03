package com.teg.popcornium_api.common.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "movie")
@Getter
@Setter
public class Movie extends AbstractEntity {

    @Column(name = "polish_title")
    private String polishTitle;

    @Column(name = "original_title")
    private String originalTitle;

    @Column(name = "production_year")
    private Integer productionYear;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "rating_count")
    private String ratingCount;

    @Column(name = "poster_url")
    private String posterUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "director_id")
    private Director director;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MovieActor> movieActors = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MovieCategory> movieCategories = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Description> descriptions = new HashSet<>();
}
