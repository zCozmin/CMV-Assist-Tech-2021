package assist.cmv.CMV.service;

import assist.cmv.CMV.model.Reservation;
import assist.cmv.CMV.model.Room;
import assist.cmv.CMV.model.User;
import assist.cmv.CMV.repository.ReservationRepository;
import assist.cmv.CMV.repository.RoomRepository;
import assist.cmv.CMV.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private SendEmailService sendEmailService;

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private String isValidAdd(User user) {
        String response = "";
        Optional<User> optUser = Optional.ofNullable(user);
        if (optUser.isPresent()) {
            if (repository.findByRoleId(user.getRoleId()) == null)
                response += "There is no role <" + user.getRoleId() + "> available.\n";
            if (user.getName() != null && (user.getName().length() < 5 || user.getName().length() > 20))
                response += "The name length should be between 5 and 20 characters.\n";
            if (user.getEmail() != null && (user.getEmail().length() < 5 || user.getEmail().length() > 40 || !user.getEmail().contains("@") || !user.getEmail().contains(".") || repository.findByEmail(user.getEmail()) != null))
                response += "Invalid/duplicate email. (must contain '@', '.' and has a length between 5 and 40 characters.\n";
            if (user.getPhone() != null && (!user.getPhone().matches("[0-9]+") || user.getPhone().length() < 10 || user.getPhone().length() > 20 || repository.findByPhone(user.getPhone()) != null))
                response += "Invalid/duplicate phone number. (0712345678)\n";
            if (user.getCreditCard() != null && (!user.getCreditCard().matches("[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4}") || repository.findByCreditCard(user.getCreditCard()) != null))
                response += "Invalid/duplicate card number. (1234-5678-9123-4567)\n";
        }
        return response;
    }

    private String isValidUpdate(User user) {
        String response = "";
        Optional<User> optUser = Optional.ofNullable(user);
        if (optUser.isPresent()) {
            if (repository.findByRoleId(user.getRoleId()) == null)
                response += "There is no role <" + user.getRoleId() + "> available.\n";
            if (user.getName() != null && (user.getName().length() < 5 || user.getName().length() > 20))
                response += "The name length should be between 5 and 20 characters.\n";
            if (user.getEmail() != null && (user.getEmail().length() < 5 || user.getEmail().length() > 40 || !user.getEmail().contains("@") || !user.getEmail().contains(".") || (repository.findByEmail(user.getEmail()) != null) && repository.countByEmail(user.getEmail()) > 1))
                response += "Invalid/duplicate email. (must contain '@', '.' and has a length between 5 and 40 characters.\n";
            if (user.getPhone() != null && (!user.getPhone().matches("[0-9]+") || user.getPhone().length() < 10 || user.getPhone().length() > 20 || (repository.findByPhone(user.getPhone()) != null) && repository.countByPhone(user.getPhone()) > 1))
                response += "Invalid/duplicate phone number. (0712345678)\n";
            if (user.getCreditCard() != null && (!user.getCreditCard().matches("[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4}") || (repository.findByCreditCard(user.getCreditCard()) != null) && repository.countByCreditCard(user.getCreditCard()) > 1))
                response += "Invalid/duplicate card number. (1234-5678-9123-4567)\n";
        }
        return response;
    }

    public ResponseEntity saveUser(User user) {
        String response = isValidAdd(user);
        if (response == null || response.equals("")) {
            repository.save(user);
            return new ResponseEntity<>("User with id <" + user.getId() + "> has been added.", HttpStatus.OK);
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity getUsers() {
        System.out.println(repository.count());
        if (repository.count() == 0)
            return new ResponseEntity<>("There are no users yet.", HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(repository.findAllByRoleId(2), HttpStatus.OK);
    }

    public ResponseEntity getUserById(int id) {
        if (repository.count() >= 2 && repository.findById(id).orElse(null) != null)
            return new ResponseEntity<>(repository.findById(id), HttpStatus.OK);
        return new ResponseEntity<>("There is no registred user with id <" + id + ">.", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity getUserByName(String name) {
        if (repository.findUserByName(name) != null)
            return new ResponseEntity<>(repository.findUserByName(name), HttpStatus.OK);
        return new ResponseEntity<>("There is no registred user with name <" + name + ">.", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity deleteUser(int id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return new ResponseEntity<>("User with id <" + id + "> has been removed.", HttpStatus.OK);
        }
        return new ResponseEntity<>("Can't find user with id <" + id + ">.", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity updateUser(User user, int id) {
        User existingUser = repository.findById(id).orElse(null);
        String response = isValidUpdate(user);
        if (existingUser != null) {
            if (response == null || response.equals("")) {
                existingUser.setName(user.getName());
                existingUser.setEmail(user.getEmail());
                existingUser.setPhone(user.getPhone());
                existingUser.setRoleId(user.getRoleId());
                existingUser.setCreditCard(user.getCreditCard());
                repository.save(existingUser);
                return new ResponseEntity<>("User with id <" + id + "> has been updated.", HttpStatus.OK);
            }
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Can't find user with id <" + id + ">.", HttpStatus.BAD_REQUEST);
    }

    private List<String> getAllEmails(int id) {
        List<User> allUsers = repository.findAllByRoleId(id);
        List<String> allEmails = new ArrayList<>();
        for (User user : allUsers)
            allEmails.add(user.getEmail());
        allEmails.remove(0);
        return allEmails;
    }

    public ResponseEntity sendEmailToAllUsers(String message, String body, int id) {
        sendEmailService.sendEmail(getAllEmails(id).toArray(new String[0]), body, message);
        return new ResponseEntity<>(getAllEmails(id), HttpStatus.OK);
    }


    public ResponseEntity lock(int id, int nfcTag) {
        System.out.println("ID: " + id);
        System.out.println("nfcTag: "+nfcTag);
        User existsUser = repository.findById(id).orElse(null);
        if(existsUser!=null){
            List<Reservation> reservations = reservationRepository.findAll();
            for (Reservation r : reservations){
                if(r.getUserId()==id){
                    Room r1 = roomRepository.getOne(r.getRoomNumber());
                    if(nfcTag == r1.getNfcTag())
                        return new ResponseEntity<>("Succes!",HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>("Failed to match the NFCTag", HttpStatus.BAD_REQUEST);
    }
}