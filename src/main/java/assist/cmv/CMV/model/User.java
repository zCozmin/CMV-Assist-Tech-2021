package assist.cmv.CMV.model;


import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="user")

public class User {

    @Id
    @GeneratedValue
    private  int id;


    private  int roleId;
    private  String name;
    private  String password;
    private  String email;
    private  String phone;
    private  String creditCard;

    public String getPassword() {
        return password;
    }

    public void setPassword(String encryptPassword) {
        this.password=encryptPassword;
    }

}
