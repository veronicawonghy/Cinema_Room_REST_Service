package cinema;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Cinema {

    private int totalRows;
    private int totalCols;

    private List<Seat> availableSeats = new ArrayList<>();

    @JsonIgnore
    private List<Purchase> purchaseList = new ArrayList<>();

    public Cinema(int rows, int cols){
        totalRows = rows;
        totalCols = cols;
        for (int i = 1; i <= rows; i++){
            for (int j = 1; j <= cols; j++){
                availableSeats.add(new Seat(i, j));
            }
        }
    }

    public int getTotalRows() {
        return totalRows;
    }

    public int getTotalCols() {
        return totalCols;
    }

    public List<Seat> getAvailableSeats() {
        return availableSeats;
    }

    public List<Purchase> getPurchaseList() {
        return purchaseList;
    }

    public Purchase purchaseSeat(Seat seat) {
        availableSeats.removeIf(e -> e.getRow() == seat.getRow() && e.getColumn() == seat.getColumn());
        UUID uuid = UUID.randomUUID();
        Purchase thePurchase = new Purchase(uuid.toString(),seat);
        purchaseList.add(thePurchase);
        return thePurchase;
    }

    public Seat returnSeat(String token) {
        Purchase theReturnedPurchase = purchaseList.stream().filter(e -> e.getToken().matches(token)).findAny().orElse(null);
        if (theReturnedPurchase != null){
            Seat theReturnedSeat = theReturnedPurchase.getTicket();
            availableSeats.add(theReturnedSeat);
            purchaseList.removeIf(e-> e.getToken().matches(token));
            return theReturnedSeat;
        }
        else {
            return null;
        }
    }

    public int getCurrentIncome() {
        int current_income = 0;
        for (Purchase p: purchaseList) {
            current_income += p.getTicket().getPrice();
        }
        return current_income;
    }

    public int getNumberOfAvailableSeats() {
        return availableSeats.size();
    }

    public int getNumberOfPurchasedTickets() {
        return purchaseList.size();
    }

}
