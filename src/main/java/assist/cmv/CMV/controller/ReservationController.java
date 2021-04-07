package assist.cmv.CMV.controller;

import assist.cmv.CMV.model.Reservation;
import assist.cmv.CMV.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "http://localhost:3000")
@RestController

public class ReservationController {

    @Autowired
    private ReservationService service;

    @PostMapping("/reservation/addReservation")
    public ResponseEntity addReservation(@RequestBody Reservation reservation) {
        return service.addReservation(reservation);
    }

    //for guest
    @GetMapping("/reservation/list/{id}")
    public ResponseEntity getReservationsByUserId(@PathVariable int id){
        return service.getReservationsByUserId(id);
    }

    //for admin
    @GetMapping("/reservation/list")
    public ResponseEntity getReservations() {
        return service.getReservations();
    }

    //for guest
    @PutMapping("/reservation/update/{id}")
    public ResponseEntity updateReservation(@RequestBody Reservation reservation, @PathVariable int id) {
        return service.updateReservation(reservation, id);
    }

    @PutMapping("/reservation/cancel/{id}")
    public ResponseEntity cancelReservation(@PathVariable int id){
        return service.cancelReservation(id);

    }

    @PutMapping("/reservation/checkin/{id}")
    public ResponseEntity performCheckIn(@PathVariable int id){
        return service.performCheckIn(id);
    }

    @PutMapping("/reservation/checkout/{id}")
    public ResponseEntity performCheckOut(@PathVariable int id){
        return service.performCheckOut(id);
    }

    @PutMapping("/reservation/checkinPhone/{id}")
    public ResponseEntity performCheckIn(@PathVariable int id, @RequestBody int nfcTag){
        return service.performCheckInPhone(id, nfcTag);
    }

    @PutMapping("/reservation/checkoutPhone/{id}")
    public ResponseEntity performCheckOut(@PathVariable int id, @RequestBody int nfcTag){
        return service.performCheckOutPhone(id, nfcTag);
    }

    @PutMapping("/reservation/lockPhone/{id}")
    public ResponseEntity performLockPhone(@PathVariable int id, @RequestBody int nfcTag){
        return service.performLockPhone(id, nfcTag);
    }

    @PutMapping("/reservation/unlockPhone/{id}")
    public ResponseEntity performUnlockPhone(@PathVariable int id, @RequestBody int nfcTag){
        return service.performUnlockPhone(id, nfcTag);
    }

    @GetMapping("/reservation/{id}")
    public ResponseEntity getReservationById(@PathVariable int id) {
        return service.getReservation(id);
    }


}
