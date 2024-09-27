package ma.entraide.impot.Repository;

import ma.entraide.impot.Entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface UserInfoRepo  extends JpaRepository<UserInfo, Integer> {
    Optional<UserInfo> findByName(String name);

    Optional<UserInfo> findByEmail(String email);
}
