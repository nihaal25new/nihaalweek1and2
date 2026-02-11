package problem8_parking_lot;

import java.util.Arrays;

public class ParkingLot {

    // Constants for spot status
    private static final int EMPTY = 0;
    private static final int OCCUPIED = 1;
    private static final int DELETED = 2;

    private static class Spot {
        String licensePlate;
        int status;

        Spot() {
            this.licensePlate = null;
            this.status = EMPTY;
        }
    }

    private Spot[] spots;
    private int capacity;

    public ParkingLot(int capacity) {
        this.capacity = capacity;
        spots = new Spot[capacity];
        for (int i = 0; i < capacity; i++) {
            spots[i] = new Spot();
        }
    }

    /**
     * Hash function for license plate
     */
    private int hash(String licensePlate) {
        return Math.abs(licensePlate.hashCode()) % capacity;
    }

    /**
     * Park a vehicle using linear probing
     */
    public String parkVehicle(String licensePlate) {
        int index = hash(licensePlate);
        int probes = 0;

        while (spots[index].status == OCCUPIED) {
            index = (index + 1) % capacity;
            probes++;
            if (probes >= capacity) return "Parking Lot Full";
        }

        spots[index].licensePlate = licensePlate;
        spots[index].status = OCCUPIED;

        return "Assigned spot #" + index + " (" + probes + " probes)";
    }

    /**
     * Vehicle exits
     */
    public String exitVehicle(String licensePlate) {
        int index = hash(licensePlate);
        int probes = 0;

        while (spots[index].status != EMPTY) {
            if (spots[index].status == OCCUPIED && licensePlate.equals(spots[index].licensePlate)) {
                spots[index].status = DELETED;
                spots[index].licensePlate = null;
                return "Spot #" + index + " freed, Duration: N/A, Fee: N/A";
            }
            index = (index + 1) % capacity;
            probes++;
            if (probes >= capacity) return "Vehicle not found";
        }

        return "Vehicle not found";
    }

    /**
     * Get parking statistics
     */
    public void getStatistics() {
        int occupied = 0;
        int totalProbes = 0;
        int peakHour = 14; // placeholder

        for (Spot spot : spots) {
            if (spot.status == OCCUPIED) occupied++;
        }

        System.out.println("Occupancy: " + (occupied * 100.0 / capacity) + "%");
        System.out.println("Avg Probes: N/A");
        System.out.println("Peak Hour: " + peakHour + "h");
    }

    /**
     * Demo / main method
     */
    public static void main(String[] args) {
        ParkingLot lot = new ParkingLot(500);

        System.out.println(lot.parkVehicle("ABC-1234"));
        System.out.println(lot.parkVehicle("ABC-1235"));
        System.out.println(lot.parkVehicle("XYZ-9999"));

        System.out.println(lot.exitVehicle("ABC-1234"));
        lot.getStatistics();
    }
}
