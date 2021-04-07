package assist.cmv.CMV.controller;

import assist.cmv.CMV.model.AuthRequest;
import assist.cmv.CMV.repository.UserRepository;
import assist.cmv.CMV.service.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class WebController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @PostMapping("/authenticate")
    public ResponseEntity generateToken(@RequestBody AuthRequest authRequest){
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        }catch(Exception ex){
            return new ResponseEntity<>("Bad credentials", HttpStatus.BAD_REQUEST);
        }

        HashMap<String, Object> hmap = new HashMap<>();
        hmap.put("token", jwtUtil.generateToken(authRequest.getEmail()));
        hmap.put("user",userRepository.findByEmail(authRequest.getEmail()));
        return new ResponseEntity<>(hmap, HttpStatus.OK);

    }
}
