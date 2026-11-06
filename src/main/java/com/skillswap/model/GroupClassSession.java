package com.skillswap.model;

import java.util.*;

/**
 * Group Class: One teacher, multiple learner seats, credit price per seat.
 */
public class GroupClassSession extends Session {
    private int totalSeats;
    private int bookedSeats;
    private int pricePerSeat;
    private List<String> enrolledLearnerIds = new ArrayList<>();

    public GroupClassSession() {
        super();
        this.totalSeats = 10;
        this.bookedSeats = 0;
        this.pricePerSeat = 10;
        this.durationMinutes = 90;
    }

    public GroupClassSession(String id, String teacherId, String skillTopic, String description,
                             String scheduledTime, int totalSeats, int pricePerSeat) {
        super(id, teacherId, "GROUP_SESSION", skillTopic, description, scheduledTime, 90, pricePerSeat);
        this.totalSeats = totalSeats;
        this.bookedSeats = 0;
        this.pricePerSeat = pricePerSeat;
    }

    @Override
    public String getSessionType() {
        return "GROUP_CLASS";
    }

    @Override
    public int calculateEscrow() {
        return pricePerSeat;
    }

    @Override
    public void validateSessionCreation() throws IllegalArgumentException {
        if (totalSeats < 2) {
            throw new IllegalArgumentException("Group classes must have at least 2 seats.");
        }
    }

    public boolean enrollLearner(String learnerId) {
        if (enrolledLearnerIds.contains(learnerId)) {
            return false;
        }
        if (bookedSeats >= totalSeats) {
            throw new IllegalStateException("Group class is fully booked.");
        }
        enrolledLearnerIds.add(learnerId);
        bookedSeats++;
        return true;
    }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
    public int getBookedSeats() { return bookedSeats; }
    public void setBookedSeats(int bookedSeats) { this.bookedSeats = bookedSeats; }
    public int getPricePerSeat() { return pricePerSeat; }
    public void setPricePerSeat(int pricePerSeat) { this.pricePerSeat = pricePerSeat; }
    public List<String> getEnrolledLearnerIds() { return enrolledLearnerIds; }
    public void setEnrolledLearnerIds(List<String> enrolledLearnerIds) { this.enrolledLearnerIds = enrolledLearnerIds; }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = super.toMap();
        m.put("totalSeats", totalSeats);
        m.put("bookedSeats", bookedSeats);
        m.put("pricePerSeat", pricePerSeat);
        m.put("availableSeats", Math.max(0, totalSeats - bookedSeats));
        m.put("enrolledLearnerIds", new ArrayList<>(enrolledLearnerIds));
        return m;
    }
}

