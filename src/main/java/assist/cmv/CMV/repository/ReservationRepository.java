package assist.cmv.CMV.repository;

import assist.cmv.CMV.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    @Query("SELECT u FROM Reservation u where u.userId=?1")
    List<Reservation> findByUserId(int id);



}
