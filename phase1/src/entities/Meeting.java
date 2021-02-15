package entities;

import persistence.Persistable;

import java.time.LocalDate;
import java.util.*;


/**
 * Represents an Meeting.
 *
 * @author XiuYu Zhang
 * @version 2 July 2020
 */

public class Meeting implements Persistable {
    /**
     * The date of this meeting hold
     */
    private LocalDate time;

    /**
     * The location of this meeting hold
     */
    private String location;

    /**
     * The number of choices two users made
     */
    private Map<Integer, Integer> userToNumEdition = new HashMap<>();

    /**
     * Whether they reach a consensus
     */
    private boolean isAgree;

    /**
     * The key of this class
     */
    private int meetingId;

    /**
     * Whether this meeting is soft deleted or not
     */
    private boolean softDelete;

    /**
     * The map to save the number of changes made by user
     */
    private Map<Integer, Boolean> isConducted = new HashMap<>();

    /**
     * The id of one who make suggestion
     */
    private int currentSugesstionMaker;

    /**
     * The id of second meeting
     */
    private boolean secondMeeting;


    /**
     * Default constructor
     * Create a meeting with input information and set the number of two users's choices to 0,
     *
     * @param meetingId              the id of this meeting
     * @param time                   the date when this meeting begin
     * @param location               the location of this meeting made
     * @param currentSuggestionMaker the id of person who suggest this meeting
     */
    public Meeting(int meetingId, LocalDate time, String location, int currentSuggestionMaker) {
        this.meetingId = meetingId; // get an unique id, please implement this
        this.location = location;
        this.time = time;
        this.isAgree = false;
        this.softDelete = false;
        this.currentSugesstionMaker = currentSuggestionMaker;
        this.secondMeeting = false;
    }

    /**
     * constructor with out input id
     * Create a meeting with input information and set the number of two users's choices to 0,
     *
     * @param time                   the date when this meeting begin
     * @param location               the location of this meeting made
     * @param currentSuggestionMaker the id of person who suggest this meeting
     */
    public Meeting(LocalDate time, String location, int currentSuggestionMaker) {
        this.meetingId = 0; // set the default id to 0
        this.location = location;
        this.time = time;
        this.isAgree = false;
        this.softDelete = false;
        this.currentSugesstionMaker = currentSuggestionMaker;
        this.secondMeeting = false;
    }

    /**
     * get the time when this meeting hold
     *
     * @return the date of this meeting
     */
    public LocalDate getTime() {
        return time;
    }

    /**
     * get the location where this meeting hold
     *
     * @return the location of this meeting
     */
    public String getLocation() {
        return location;
    }

    /**
     * get how many times did two users make choices
     *
     * @param userId the user id
     * @return the user's id who make choices
     */
    public int getNumUserEditions(int userId) {
        if (userToNumEdition.containsKey(userId)) {
            return userToNumEdition.get(userId);
        } else {
            return 0;
        }
    }


    /**
     * change the time of this meeting
     *
     * @param time set new time to this meeting
     */
    public void editTime(LocalDate time) {
        this.time = time;
    }

    /**
     * change the location of this meeting
     *
     * @param location set new location to this meeting
     */
    public void editLocation(String location) {
        this.location = location;
    }

    /**
     * add one when the user made choice to the meeting
     *
     * @param userId set new user's id to this meeting who made suggestion
     */
    public void userMakesEdition(int userId) {
        setCurrentSugesstionMaker(userId);
        if (userToNumEdition.containsKey(userId)) {
            userToNumEdition.replace(userId, userToNumEdition.get(userId) + 1);
        } else {
            userToNumEdition.put(userId, 1);
        }
    }

    /**
     * set new suggestion maker's od
     *
     * @param userId suggestion maker's od
     */
    public void setCurrentSugesstionMaker(int userId) {
        currentSugesstionMaker = userId;
    }

    /**
     * get suggestion maker's od
     *
     * @return the id of one who make suggestion
     */
    public int getCurrentSugesstionMaker() {
        return currentSugesstionMaker;
    }

    /**
     * Both users agree on the input time and place, then change the agree from false to true
     */
    public void doAgree() {
        isAgree = true;
    }

    /**
     * Return whether this meeting is agreed or not
     *
     * @return whether this meeting is agreed or not
     */
    public boolean isAgree() {
        return isAgree;
    }

    /**
     * set the id to second meeting
     *
     * @param b second meeting's id
     */
    public void setSecondMeeting(boolean b) {
        secondMeeting = b;
    }

    /**
     * get the id to second meeting
     *
     * @return second meeting's id
     */
    public boolean isSecondMeeting() {
        return secondMeeting;
    }

    /**
     * A method to get the key to this item, which is the id
     *
     * @return the id of meeting
     */
    @Override
    public int getKey() {
        return meetingId;
    }

    /**
     * Set a new id to meetingId
     *
     * @param id set new id to this meeting
     */
    @Override
    public void setKey(int id) {
        this.meetingId = id;
    }

    /**
     * return "meetingId"
     *
     * @return the name of primary key
     */
    @Override
    public String getPrimaryKeyName() {
        return "meetingId";
    }

    /**
     * return an list of all property
     *
     * @return list of the names of attributes
     */
    @Override
    public List<String> getColumns() {
        List col = new ArrayList();
        col.add("time");
        col.add("location");
        col.add("userToNumEdition");
        col.add("isAgree");
        col.add("meetingId");
        col.add("currentSuggestionMaker");
        col.add("softDelete");
        col.add("secondMeeting");
        return col;
    }

    /**
     * return all names of attributes in this class with the values
     *
     * @return the names of attributes in this class with the values
     */
    @Override
    public String toString() {
        return "Meeting{" +
                "time=" + time +
                ", location='" + location + '\'' +
                ", userToNumEdition=" + userToNumEdition +
                ", isAgree=" + isAgree +
                ", meetingId=" + meetingId +
                ", currentSuggestionMaker=" + currentSugesstionMaker +
                ", softDelete=" + softDelete +
                ", secondMeeting=" + secondMeeting +
                '}';
    }

    /**
     * set the variable to soft delete
     *
     * @param softDelete boolean to soft Delete
     */
    public void setSoftDelete(boolean softDelete) {
        this.softDelete = softDelete;
    }

    /**
     * get the whether this meeting is soft deleted or not
     *
     * @return the attribute of softDelete
     */
    public boolean getSoftDelete() {
        return this.softDelete;
    }

    /**
     * set to conducted
     *
     * @param userId the id of user
     * @param set    boolean that set to the user
     */
    public void setConducted(int userId, boolean set) {
        isConducted.put(userId, set);
    }

    /**
     * get the conducted according to user's id
     * return false if that fails
     *
     * @param userId the id of user
     * @return get the conducted according to user's id, return false if that fails
     */
    public boolean getConducted(int userId) {
        if (isConducted.containsKey(userId)) {
            return isConducted.get(userId);
        } else {
            return false;
        }
    }

    /**
     * get whether this meeting is completed or not
     *
     * @return whether this meeting is complete or not
     */
    public boolean isComplete() {
        if (isConducted.size() < 2) {
            return false;
        }
        for (int i : isConducted.keySet()) {
            if (!isConducted.get(i)) {
                return false;
            }
        }
        return true;
    }
}
