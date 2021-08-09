package cinema;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
public class CinemaController {

    private Cinema theCinema = new Cinema(9,9);

    @GetMapping("/seats")
    public Map<String, ?> getSeats() {
        return Map.of(
                "total_rows", theCinema.getTotalRows(),
                "total_columns", theCinema.getTotalCols(),
                "available_seats", theCinema.getAvailableSeats()
        );
    }

    @PostMapping("/purchase")
    public ResponseEntity<?> postPurchase(@RequestBody Seat seat) {
        System.out.println("post request = Purchase");
        if(seat.getColumn() < 0 || seat.getColumn() > theCinema.getTotalCols() || seat.getRow() < 0 || seat.getRow() > theCinema.getTotalRows()  ){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new Error("The number of a row or a column is out of bounds!"));
        }
        else if(theCinema.getAvailableSeats().stream().filter(e -> e.getRow() == seat.getRow() && e.getColumn() == seat.getColumn()).findAny().orElse(null) == null){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new Error("The ticket has been already purchased!"));
        }
        else {
            Purchase thePurchase = theCinema.purchaseSeat(seat);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(thePurchase);
        }
    }

    @PostMapping("/return")
    public ResponseEntity<?> postReturn(@RequestBody Purchase purchase){
        String token = purchase.getToken();
        Seat seat = theCinema.returnSeat(token);
        if (seat == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new Error("Wrong token!"));
        }
        else return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of("returned_ticket",seat));
    }

    @PostMapping("/stats")
    public ResponseEntity<?> postStats(@RequestParam(value = "password", required = false) String password){
        if ("super_secret".equals(password)){
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Map.of(
                            "current_income", theCinema.getCurrentIncome(),
                            "number_of_available_seats", theCinema.getNumberOfAvailableSeats(),
                            "number_of_purchased_tickets", theCinema.getNumberOfPurchasedTickets()
                    ));
        }
        else {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new Error("The password is wrong!"));
        }

    }
}
