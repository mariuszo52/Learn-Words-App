package pl.languagelearn.application.user;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findUserByEmail(String email);
    Optional<User> findUserById(long id);
    Optional<User> findByConfirmationToken(String token);
}
