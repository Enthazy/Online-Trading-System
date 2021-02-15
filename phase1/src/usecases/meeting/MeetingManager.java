package usecases.meeting;

import entities.Meeting;
import persistence.PersistenceInterface;
import persistence.exceptions.PersistenceException;
import usecases.config.ListensForConfig;
import usecases.meeting.exceptions.EditAgreedMeetingException;
import usecases.meeting.exceptions.TooManyEditionsException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

/**
 * A use case for Meeting, which deals with all operations relating to Meeting except instantiation.
 */
public class MeetingManager implements ListensForConfig {

    /**
     * Class dependencies
     */
    private PersistenceInterface gateway;

    /**
     * Instance variables
     */
    private int editThreshold; // set by configManager, see updateConfig



    /**
     * To create an instance of MeetingManager
     *
     * @param gateway to access the stored data
     */
    public MeetingManager(PersistenceInterface gateway) {
        this.gateway = gateway;
    }


    /**
     * Updates the maximum number of editions allowed
     *
     * @param config A map with the key value pairs of all the configurable options.
     */
    @Override
    public void updateConfig(Map<String, String> config) {
        this.editThreshold = parseInt(config.get("maxMeetingEdits"));
    }


    /**
     * To allow a user to edit a meeting
     *
     * @param userId    of the user who wants to make an edition
     * @param meetingId of the intended meeting
     * @param location  which the user wants to change to
     * @param time      which the user wants to change to
     * @return true iff the user wants to change the first meeting
     * @throws TooManyEditionsException   if user is trying to edit a meeting which he/she has edited maximum allowed times
     * @throws IOException                if there is an IO error
     * @throws EditAgreedMeetingException if user is trying to edit a already agreed meeting
     */
    public boolean userEditMeetingSuggestion(int userId, int meetingId, String location, LocalDate time) throws
            TooManyEditionsException, IOException, EditAgreedMeetingException {

        Meeting meeting = getMeeting(meetingId);

        // To check if the user has reached the edition limits
        if (meeting.getNumUserEditions(userId) > editThreshold) {
            throw new TooManyEditionsException();
        }

        // To check if the user is trying to edit an agreed meeting
        if (meeting.isAgree()) throw new EditAgreedMeetingException();

        // Starts making editions
        meeting.editLocation(location);

        // a user cannot change the time for the second meeting, as it is given by the system
        if (!meeting.isSecondMeeting()) {
            meeting.editTime(time);
        }

        meeting.userMakesEdition(userId);
        List<Meeting> meetings = new ArrayList<>();
        meetings.add(meeting);
        try {
            gateway.update(meetings, Meeting.class);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }

        return !meeting.isSecondMeeting();
    }


    /**
     * To let a user to agree to the meeting, instead of making further change
     *
     * @param meetingId of the meeting
     * @throws IOException if there is an IO error
     */
    public void toAgree(int meetingId) throws IOException {
        Meeting meeting = getMeeting(meetingId);
        meeting.doAgree();
        List<Meeting> meetings = new ArrayList<>();
        meetings.add(meeting);
        try {
            gateway.update(meetings, Meeting.class);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
    }


    /**
     * To let a user to confirm that the meeting took place in real life
     *
     * @param meetingId of the meeting
     * @param userId of the user
     * @throws IOException if there is an IO error
     */
    public void toMarkConducted(int meetingId, int userId) throws IOException {
        Meeting meeting = getMeeting(meetingId);
        meeting.setConducted(userId, true);
        List<Meeting> meetings = new ArrayList<>();
        meetings.add(meeting);
        try {
            gateway.update(meetings, Meeting.class);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
    }


    /**
     * Check if this meeting is said to be conducted by a user
     *
     * @param meetingId of the meeting
     * @param userId    of the user
     * @return true iff the user has confirmed to the meeting
     * @throws IOException if there is an IO error
     */
    public boolean checkIsConducted(int meetingId, int userId) throws IOException {
        Meeting meeting = getMeeting(meetingId);
        return meeting.getConducted(userId);
    }


    /**
     * Check if this meeting is agreed
     *
     * @param meetingId of the meeting
     * @return true iff this meeting is agreed
     * @throws IOException if there is an IO error
     */
    public boolean checkIsAgree(int meetingId) throws IOException {
        Meeting meeting = getMeeting(meetingId);
        return meeting.isAgree();
    }


    /**
     * To get the Date of the meeting
     *
     * @param meetingId of the meeting
     * @return the Date of the meeting
     * @throws IOException if there is an IO error
     */
    public LocalDate getMeetingDate(int meetingId) throws IOException {
        Meeting meeting = getMeeting(meetingId);
        return meeting.getTime();
    }


    /**
     * To get the location of the meeting
     *
     * @param meetingId of the meeting
     * @return the location of the meeting
     * @throws IOException if there is an IO error
     */
    public String getMeetingLocation(int meetingId) throws IOException {
        Meeting meeting = getMeeting(meetingId);
        return meeting.getLocation();
    }


    /**
     * To get the last user who made editions to the meeting
     *
     * @param meetingId of the meeting
     * @return the user id of the user
     * @throws IOException of there is an IO error
     */
    public int getCurrentSuggestionMaker(int meetingId) throws IOException {
        List<Integer> ids = new ArrayList<>();
        ids.add(meetingId);
        Meeting meeting = gateway.get(ids, Meeting.class).get(0);
        return meeting.getCurrentSugesstionMaker();
    }


    // A private method used as a helper method, which returns meeting object based on meeting id
    private Meeting getMeeting(int meetingId) throws IOException {
        List<Integer> ids = new ArrayList<>();
        ids.add(meetingId);
        Meeting meeting = gateway.get(ids, Meeting.class).get(0);
        return meeting;
    }


}
