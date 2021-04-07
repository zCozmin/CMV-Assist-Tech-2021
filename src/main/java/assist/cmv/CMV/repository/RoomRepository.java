package assist.cmv.CMV.repository;

import assist.cmv.CMV.model.Reservation;
import assist.cmv.CMV.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    boolean existsById(int id);
    boolean existsRoomByNfcTag(int nfcTag);
    boolean existsRoomById(int id);
    int countById(int id);
    int countByNfcTag(int NFCtag);


}
