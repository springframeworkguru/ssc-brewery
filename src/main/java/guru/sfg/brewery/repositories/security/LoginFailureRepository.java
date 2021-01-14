package guru.sfg.brewery.repositories.security;

import guru.sfg.brewery.domain.security.LoginFailure;
import guru.sfg.brewery.domain.security.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by jt on 7/20/20.
 */
public interface LoginFailureRepository extends JpaRepository<LoginFailure, Integer> {

    List<LoginFailure> findAllByUserAndCreatedDateIsAfter(User user, Timestamp timestamp);
}
