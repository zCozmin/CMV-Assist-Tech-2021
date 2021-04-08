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

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;
    private final LocalDate localDate = LocalDate.now();

    private void setData(Reservation reservation) {
        reservation.setAnnulled(false);
        reservation.setStatus("new");
    }

    private String isValid(Reservation reservation) {
        List<Reservation> reservations = reservationRepository.findAll();

        Period period = Period.between(reservation.getStartDate(), reservation.getEndDate());
        StringBuilder messageResponse = new StringBuilder();
        if (reservation.getStartDate().isBefore(localDate))
            messageResponse.append("Start date is in the past!\n");
        if (reservation.getEndDate().isBefore(reservation.getStartDate()) || reservation.getStartDate().equals(reservation.getEndDate()))
            messageResponse.append("Start date is bigger than end date!\n");
        if (!roomRepository.existsById(reservation.getRoomNumber())) {
            messageResponse.append("Room with id <").append(reservation.getRoomNumber()).append("> doesn't exist.\n");
        } else {
            for (Reservation r : reservations) {
                if (r.getRoomNumber() == reservation.getRoomNumber()) {
                    LocalDate start = r.getStartDate();
                    LocalDate end = r.getEndDate();
                    LocalDate s1 = reservation.getStartDate();
                    LocalDate e1 = reservation.getEndDate();
                    if (((s1.equals(start) || e1.equals(end) || (s1.isAfter(start) && s1.isBefore(end)) || (e1.isAfter(start) && e1.isBefore(end)))
                            || (s1.isBefore(start) && e1.isAfter(end))) && r.getAnnulled()==false)
                        messageResponse.append("This room is already reserved between these dates!\n");
                }
            }
            int roomprice = roomRepository.getOne(reservation.getRoomNumber()).getPrice();
            if (period.getDays() > 30 || period.getMonths() >= 1 || period.getYears() >= 1)
                messageResponse.append("Can't reserve a room for a period longer than 30 days! \n");
            reservation.setPrice(period.getDays() * roomprice);
        }
        if (!userRepository.existsById(reservation.getUserId()))
            messageResponse.append("There is no registred suser with id <").append(reservation.getUserId()).append(">.\n");
        return messageResponse.toString();
    }

    public ResponseEntity addReservation(Reservation reservation) {
        String responseError = isValid(reservation);
        if (responseError.equals("")) {
            setData(reservation);
            reservationRepository.save(reservation);
            return new ResponseEntity<>("Reservation with id <" + reservation.getId() + "> has been added.", HttpStatus.OK);
        }
        return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
    }


    public ResponseEntity getReservation(int id) {
        Reservation existingReservation = reservationRepository.findById(id).orElse(null);
        if (existingReservation == null)
            return new ResponseEntity<>("Reservation with id <" + id + "> doesn't exist.", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(existingReservation, HttpStatus.OK);

    }

    public ResponseEntity getReservations() {
        List<Reservation> lst = reservationRepository.findAll();
        if (lst.size() != 0) {
            for (Reservation r : lst) {
                if (localDate.isAfter(r.getEndDate()) && !r.getStatus().equals("check-out")) {
                    r.setStatus("auto check-out");
                    r.setAnnulled(true);
                    reservationRepository.save(r);
                }
            }
            return new ResponseEntity<>(reservationRepository.findAll(), HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(reservationRepository.findAll(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity getReservationsByUserId(int id) {
        if (!userRepository.existsById(id))
            return new ResponseEntity<>("User with id <" + id + "> was not found.", HttpStatus.BAD_REQUEST);
        List<Reservation> lst = reservationRepository.findByUserId(id);
        if (lst.size() != 0)
            return new ResponseEntity<>(reservationRepository.findByUserId(id), HttpStatus.OK);
        else
            return new ResponseEntity<>("There are no reservations available for user with id <" + id + ">.", HttpStatus.BAD_REQUEST);
    }


    public ResponseEntity updateReservation(Reservation reservation, int id) {
        Reservation existingReservation = reservationRepository.findById(id).orElse(null);
        if (existingReservation == null) {
            return new ResponseEntity<>("Reservation with id <" + id + "> doesn't exist.", HttpStatus.BAD_REQUEST);
        }
        String responseError = isValid(reservation);
        if (responseError == null) {

            Room existingRoom = roomRepository.findById(existingReservation.getRoomNumber()).orElse(null);
            roomRepository.save(existingRoom);
            existingReservation.setAnnulled(reservation.getAnnulled());
            existingReservation.setEndDate(reservation.getEndDate());
            existingReservation.setStatus(reservation.getStatus());
            existingReservation.setStartDate(reservation.getStartDate());
            existingReservation.setUserId(reservation.getUserId());
            existingReservation.setRoomNumber(reservation.getRoomNumber());
            existingReservation.setPrice(reservation.getPrice());
            existingRoom = roomRepository.findById(reservation.getRoomNumber()).orElse(null);
            roomRepository.save(existingRoom);

            reservationRepository.save(existingReservation);
            return new ResponseEntity<>(existingReservation, HttpStatus.OK);

        }
        return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
    }


    public ResponseEntity cancelReservation(int id) {
        Reservation existingReservation = reservationRepository.findById(id).orElse(null);
        if (existingReservation == null) {
            return new ResponseEntity<>("Reservation with id <" + id + "> does not exist.", HttpStatus.BAD_REQUEST);
        }
        if (existingReservation.getStartDate().isBefore(localDate) || existingReservation.getStartDate().equals(localDate)) {
            return new ResponseEntity<>("Reservation with id <" + id + "> has already started (can't be canceled).", HttpStatus.BAD_REQUEST);
        }
        if(existingReservation.getAnnulled()==true){
            return new ResponseEntity<>("Reservation with id <" + id + "> has already been canceled (can't be canceled).", HttpStatus.BAD_REQUEST);

        }

        existingReservation.setAnnulled(true);
        reservationRepository.save(existingReservation);
        return new ResponseEntity<>("Reservation with id <" + id + " succesfully canceled.", HttpStatus.OK);
    }

    public ResponseEntity performCheckIn(int id) {
        Reservation existingReservation = reservationRepository.findById(id).orElse(null);
        if (existingReservation != null) {
            LocalDate start = existingReservation.getStartDate();
            LocalDate end = existingReservation.getEndDate();
            if (localDate.isEqual(end) || localDate.isAfter(end) || localDate.isBefore(start))
                return new ResponseEntity<>("Could not perform check-in!", HttpStatus.BAD_REQUEST);
            if (existingReservation.getStatus().equals("check-in"))
                return new ResponseEntity<>("Check-in already performed!", HttpStatus.BAD_REQUEST);
            if (existingReservation.getStatus().equals("check-out"))
                return new ResponseEntity<>("Check-out has been performed!", HttpStatus.BAD_REQUEST);
        }else{
            return new ResponseEntity<>("Reservation with id <" + id + " does not exist.", HttpStatus.BAD_REQUEST);
        }

        existingReservation.setStatus("check-in");
        reservationRepository.save(existingReservation);
        return new ResponseEntity<>("Check-in performed!", HttpStatus.OK);
    }

    public ResponseEntity performCheckOut(int id) {
        Reservation exReservation = reservationRepository.findById(id).orElse(null);
        if (!(exReservation.getStatus().equals("check-in") || exReservation.getStatus().equals("locked") || exReservation.getStatus().equals("unlocked")))
            return new ResponseEntity<>("You need to perform check-in first!", HttpStatus.BAD_REQUEST);
        if (localDate.isAfter(exReservation.getEndDate()))
            return new ResponseEntity<>("Reservation expired!", HttpStatus.BAD_REQUEST);
        if (localDate.isBefore(exReservation.getStartDate()))
            return new ResponseEntity<>("Reservation did not start yet!", HttpStatus.BAD_REQUEST);

        exReservation.setStatus("check-out");
        reservationRepository.save(exReservation);
        return new ResponseEntity<>("Check-out performed!", HttpStatus.OK);
    }


    public ResponseEntity performCheckInPhone(int id, int nfcTag) {
        boolean ok = true;
        System.out.println(id + " " + nfcTag);
        StringBuilder errorMessage = new StringBuilder();
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            List<Reservation> allReservations = reservationRepository.findAll();
            for (Reservation reservation : allReservations) {
                Room room = roomRepository.findById(reservation.getRoomNumber()).orElse(null);
                    if (reservation.getUserId() == id && room.getNfcTag() == nfcTag) {
                        ok = true;
                        LocalDate start = reservation.getStartDate();
                        LocalDate end = reservation.getEndDate();
                        if (localDate.isEqual(end) || localDate.isAfter(end) || localDate.isBefore(start)) {
                            ok = false;
                            errorMessage.append("Could not perform check-in!");
                        }
                        if (reservation.getStatus().equals("check-in")) {
                            ok = false;
                            errorMessage.append("Check-in already performed!");
                        }
                        if (reservation.getStatus().equals("check-out")) {
                            ok = false;
                            errorMessage.append("Check-out has been performed!");
                        }
                        if (ok) {
                            reservation.setStatus("check-in");
                            reservationRepository.save(reservation);
                            return new ResponseEntity<>("Check-in performed!", HttpStatus.OK);
                        }
                    }

            }
        } else
            return new ResponseEntity<>("User with id <" + id + "> not found.", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorMessage.toString(), HttpStatus.BAD_REQUEST);
    }


    public ResponseEntity performCheckOutPhone(int id, int nfcTag) {
        boolean ok = true;
        StringBuilder errorMessage = new StringBuilder();
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            List<Reservation> allReservations = reservationRepository.findAll();
            for (Reservation reservation : allReservations) {
                ok = true;
                Room room = roomRepository.findById(reservation.getRoomNumber()).orElse(null);

                    if (reservation.getUserId() == id && room.getNfcTag() == nfcTag) {
                        if (reservation.getStatus().equals("new") || reservation.getStatus().equals("unlocked")) {
                            ok = false;
                            errorMessage.append("Can not perform check-out");
                        }
                        if (reservation.getStatus().equals("check-out")) {
                            ok = false;
                            errorMessage.append("Check out is already performed.");
                        }
                        if (localDate.isAfter(reservation.getEndDate())) {
                            ok = false;
                            errorMessage.append("Reservation expired!");
                        }
                        if (localDate.isBefore(reservation.getStartDate())) {
                            ok = false;
                            errorMessage.append("Reservation did not start yet!");
                        }
                        if (ok) {
                            reservation.setStatus("check-out");
                            reservationRepository.save(reservation);
                            return new ResponseEntity<>("Check-out performed!", HttpStatus.OK);
                        }
                    }

            }
        } else
            return new ResponseEntity<>("User with id <" + id + "> not found.", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorMessage.toString(), HttpStatus.BAD_REQUEST);
    }


    public ResponseEntity performLockPhone(int id, int nfcTag) {
        boolean ok = true;
        StringBuilder errorMessage = new StringBuilder();
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            List<Reservation> allReservations = reservationRepository.findAll();
            for (Reservation reservation : allReservations) {
                Room room = roomRepository.findById(reservation.getRoomNumber()).orElse(null);
                if (reservation.getUserId() == id && room.getNfcTag() == nfcTag) {
                    ok = true;
                    if (reservation.getStatus().equals("locked")) {
                        ok = false;
                        errorMessage.append("Door is already locked.");
                    }
                    if (reservation.getStatus().equals("check-out")) {
                        ok = false;
                        errorMessage.append("Reservation checked-out.");
                    }
                    if (reservation.getStatus().equals("new")) {
                        ok = false;
                        errorMessage.append("Reservation did not start yet");
                    }
                    if (ok) {
                        reservation.setStatus("locked");
                        reservationRepository.save(reservation);
                        return new ResponseEntity<>("Door locked", HttpStatus.OK);
                    }
                }
            }
        } else
            return new ResponseEntity<>("User with id <" + id + "> not found.", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorMessage.toString(), HttpStatus.BAD_REQUEST);
    }


    public ResponseEntity performUnlockPhone(int id, int nfcTag) {
        boolean ok = true;
        StringBuilder errorMessage = new StringBuilder();
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            List<Reservation> allReservations = reservationRepository.findAll();
            for (Reservation reservation : allReservations) {
                ok = true;
                Room room = roomRepository.findById(reservation.getRoomNumber()).orElse(null);
                if (reservation.getUserId() == id && room.getNfcTag() == nfcTag) {
                    if (reservation.getStatus().equals("unlocked")) {
                        ok = false;
                        errorMessage.append("Door is already unlocked.");
                    }
                    if (reservation.getStatus().equals("check-out")) {
                        ok = false;
                        errorMessage.append("Reservation checked-out.");
                    }
                    if (reservation.getStatus().equals("new")) {
                        ok = false;
                        errorMessage.append("Reservation did not start yet.");
                    }
                    if (ok) {
                        reservation.setStatus("unlocked");
                        reservationRepository.save(reservation);
                        return new ResponseEntity<>("Door unlocked.", HttpStatus.OK);
                    }
                }
            }
        } else
            return new ResponseEntity<>("User with id <" + id + "> not found.", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorMessage.toString(), HttpStatus.BAD_REQUEST);
    }

}
