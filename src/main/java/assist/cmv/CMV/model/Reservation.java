package assist.cmv.CMV.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="reservation")
public class Reservation {

    @Id
    @GeneratedValue
    private int id;

    private LocalDate startDate;
    private LocalDate endDate;
    private int userId;
    private String status;
    private boolean annulled;
    private long price;
    private int roomNumber;

    public boolean getAnnulled() {
        return annulled;
    }

}
