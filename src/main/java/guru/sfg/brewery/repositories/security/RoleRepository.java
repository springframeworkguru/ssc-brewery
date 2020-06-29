package guru.sfg.brewery.repositories.security;

import guru.sfg.brewery.domain.security.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jt on 6/29/20.
 */
public interface RoleRepository extends JpaRepository<Role, Integer> {
}
