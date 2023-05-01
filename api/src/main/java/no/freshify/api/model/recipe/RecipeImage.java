package no.freshify.api.model.recipe;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RecipeImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Lob
    @Column
    private byte[] data;

    @Column
    private String contentType;

    @OneToOne(mappedBy = "image")
    private Recipe recipe;
}
