package grocery.repository;

import grocery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select x from User x where x.username like :username and x.password like :password")
    User findByUserNameAndPassword(@Param("username") String username, @Param("password") String password);
}
