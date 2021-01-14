package guru.sfg.brewery.repositories.security;

import guru.sfg.brewery.domain.security.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by jt on 6/29/20.
 */
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String customer);
}
