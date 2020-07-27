package guru.sfg.brewery.repositories.security;

import guru.sfg.brewery.domain.security.Role;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(@Nullable String customer);

}
