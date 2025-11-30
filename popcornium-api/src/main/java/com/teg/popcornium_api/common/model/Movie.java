package com.teg.popcornium_api.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "movie")
@Getter
@Setter
public class Movie extends AbstractEntity {

    @Column(name = "polish_title")
    private String polishTitle;

    @Column(name = "english_title")
    private String englishTitle;

    @Column(name = "production_year")
    private Integer productionYear;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "rating_count")
    private String ratingCount;

    @Column(name = "poster_url")
    private String posterUrl;

    @OneToMany(mappedBy = "movie")
    private Set<Description> descriptions;

    @OneToMany(mappedBy = "movie")
    private Set<Actor> actors;

    @OneToMany(mappedBy = "movie")
    private Set<Category> categories;
}
