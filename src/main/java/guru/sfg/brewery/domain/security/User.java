package guru.sfg.brewery.domain.security;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.Set;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    String username;
    String password;

    @Singular
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST},
            fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")})
    Set<Role> roles;

    @Transient
    Set<Authority> authorities;

    @Builder.Default
    Boolean accountNonExpired = true;
    @Builder.Default
    Boolean accountNonLocked = true;
    @Builder.Default
    Boolean credentialsNonExpired = true;
    @Builder.Default
    Boolean enabled = true;

    public Set<Authority> getAuthorities() {
        return getRoles().stream()
                .map(Role::getAuthorities)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

}
