package assist.cmv.CMV.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class NFCRequest {
    private int id;
    private int nfcTag;
}
