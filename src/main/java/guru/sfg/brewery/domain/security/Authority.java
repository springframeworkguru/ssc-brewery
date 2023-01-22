package guru.sfg.brewery.domain.security;

import lombok.*;

import jakarta.persistence.*;
import java.util.Set;

/**
 * Modified by Pierrot on 2023-01-22.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String role;

    @ManyToMany(mappedBy = "authorities")
    private Set<User> users;
}
