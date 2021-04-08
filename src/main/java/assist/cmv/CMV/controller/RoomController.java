package assist.cmv.CMV.controller;

import assist.cmv.CMV.model.Room;
import assist.cmv.CMV.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class RoomController {

    @Autowired
    private RoomService service;


    @PostMapping("/room/addRoom")
    public ResponseEntity addRoom(@RequestBody Room room) {
        return service.addRoom(room);
    }

    @GetMapping("/room/list")
    public ResponseEntity getRooms() {
        return service.getRooms();
    }

    @PutMapping("/room/clean/{id}")
    public ResponseEntity clean(@PathVariable int id){
        return  service.cleanRoom(id);
    }


    @GetMapping("/room/listPhone")
    public ResponseEntity getRoomsPhone(@RequestHeader("Token") String token) {
        return service.getRoomsPhone(token);
    }

    @GetMapping("/room/{id}")
    public ResponseEntity getRoomById(@PathVariable int id) {
        return service.getRoom(id);
    }

    @GetMapping("/room/available/{startDate}/{endDate}")
    public ResponseEntity getAllAvailableRoomsByTwoDates(@PathVariable String startDate, @PathVariable String endDate) {
        return service.getAvailableRoomsByStartDateAndEndDate(LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")), LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }
    @PutMapping("/room/update/{id}")
    public ResponseEntity updateRoom(@RequestBody Room room, @PathVariable int id) {
        return service.updateRoom(room, id);
    }

    @DeleteMapping("/room/delete/{id}")
    public ResponseEntity deleteRoom(@PathVariable int id) {
        return service.deleteRoom(id);
    }




}
