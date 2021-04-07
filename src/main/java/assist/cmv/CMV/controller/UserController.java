package assist.cmv.CMV.controller;

import assist.cmv.CMV.model.User;
import assist.cmv.CMV.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private BCryptPasswordEncoder encoder;


    @PostMapping("/user/addUser")
    public ResponseEntity addUser(@RequestBody User user) {



        String pwd = user.getPassword();
        String encryptPassword = encoder.encode(pwd);
        user.setPassword(encryptPassword);
        return service.saveUser(user);

    }

    @PostMapping("/user/addUserPhone")
    public ResponseEntity addUserPhone(@RequestBody User user) {

        String pwd = user.getPassword();
        String encryptPassword = encoder.encode(pwd);
        user.setPassword(encryptPassword);
        user.setRoleId(2);
        return service.saveUser(user);

    }

    @PostMapping("/user/{id}/lock")
    public ResponseEntity lock(@RequestBody int nfcRequest, @PathVariable int id, @RequestHeader("Token") String token){
        System.out.println(token);
        System.out.println(id+ "  nfc" +nfcRequest);
        return service.lock(id,nfcRequest);
    }

    @GetMapping("/user/list")
    public ResponseEntity getUsers() {
        return service.getUsers();
    }

    @GetMapping("/user/id/{id}")
    public ResponseEntity findUserById(@PathVariable int id) {
        return service.getUserById(id);
    }

    @GetMapping("/user/{name}")
    public ResponseEntity findUserByName(@PathVariable String name) {
        return service.getUserByName(name);
    }

    @PutMapping("/user/update/{id}")
    public ResponseEntity updateUser(@RequestBody User user, @PathVariable int id) {
        return service.updateUser(user, id);
    }

    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity deleteUser(@PathVariable int id) {
        return service.deleteUser(id);
    }

    @GetMapping("/users/emails")
    public ResponseEntity sendEmails(String body, String message) {
        return service.sendEmailToAllUsers(body, message);
    }
}