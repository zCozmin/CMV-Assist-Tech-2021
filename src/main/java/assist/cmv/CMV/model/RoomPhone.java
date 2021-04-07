package assist.cmv.CMV.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomPhone {
    private int id, nfcTag;
    private boolean status;
}
