package guru.sfg.brewery.domain.security;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    String role;

    @ManyToMany(mappedBy = "authorities")
    Set<User> users;
}
