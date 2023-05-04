package no.freshify.api.model.recipe;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer estimatedTime;

    @Column
    private String name;

    @Column(length = 500)
    private String description;

    @Column(length = 2000)
    private String steps;

    @ManyToMany
    @JoinTable(name = "recipe_category_association",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<RecipeCategory> categories;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    private Set<RecipeIngredient> recipeIngredients;

    @ManyToMany
    @JoinTable(name = "recipe_allergen_association",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "allergen_id"))
    private Set<Allergen> allergens;

    @Column
    private String image;
}