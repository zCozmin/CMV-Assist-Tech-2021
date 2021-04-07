package assist.cmv.CMV.repository;

import assist.cmv.CMV.model.Reservation;
import assist.cmv.CMV.model.Role;
import assist.cmv.CMV.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsById(int id);

    User findByEmail(String email);

    @Query("SELECT r FROM Role r where r.id=?1")
    Role findByRoleId(int id);

    @Query("SELECT u FROM User u where u.name=?1")
    User findUserByName(String name);

    int countById(int id);

    User findByCreditCard(String creditCard);

    User findByPhone(String phone);

    int countByCreditCard(String creditCard);

    int countByPhone(String phone);

    int countByEmail(String email);
}