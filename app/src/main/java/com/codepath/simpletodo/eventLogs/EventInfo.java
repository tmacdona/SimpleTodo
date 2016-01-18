package com.codepath.simpletodo.eventLogs;


public class EventInfo {
    public long rowId;
    public String eventContactName;
    public String eventContactKey;
    public long eventContactID;  /*ID of SMSer, if available. Person who sends it*/
    public String eventContactAddress;  //phone number
    public long eventDate;  /*date of SMS. Time of day?*/
    public String eventNotes;

    public int done = 0; // not complete = 0;
    public int priority = 0;

    public int eventClass;
    //eventClass definition
    final public static int ALL_CLASS = 0;
    final public static int PHONE_CLASS = 1;
    final public static int SMS_CLASS = 2;
    final public static int EMAIL_CLASS = 3;
    final public static int MEETING_CLASS = 4; //in the meatspace
    final public static int FACEBOOK = 5;
    final public static int GOOGLE_HANGOUTS = 6;
    final public static int SKYPE = 7;

    public interface PRIORITY{
        int LOW = 0;
        int MED = 1;
        int HI = 3;
        int AUTO = 4;
    }

    public interface DONE{
        int YES = 1;
        int NO = 0;
    }


    public void clear(){
        eventContactName = null;
        eventDate = 0;
        eventContactAddress = null;
        eventContactID = -1;
    }

    final public static int OUTGOING_TYPE = 2;
    final public static int INCOMING_TYPE = 1;
    final public static int MISSED_DRAFT = 3;
    final public static int RECORD_UPDATE_MARKER = 4;  // Used in the keeping track of database updates


    //constructor
    public EventInfo(String name, String contactKey,
                     String phoneNumber, long date_ms, String notes, int done, int priority){

        this.eventContactName = name;
        //this.eventContactID = contactID;
        this.eventContactKey = contactKey;
        this.eventContactAddress = phoneNumber;
        this.eventDate = date_ms;
        this.eventNotes = notes;
        this.done = done;
        this.priority = priority;
    }

    public long getRowId() { return rowId; }

    public String getAddress() {
        return eventContactAddress;
    }
    public long getContactID() {
        return eventContactID;
    }
    public long getDate() {
        return eventDate;
    }
    public int getEventClass() {
        return eventClass;
    }
    public String getEventNotes() {
        return eventNotes;
    }
    public String getEventMessage() {
        return eventNotes;
    }

    public String getContactName() {
        return eventContactName;
    }
    public String getContactKey() {
        return eventContactKey;
    }
    public int getCompletness() {
        return done;
    }
    public int getPriority() {
        return priority;
    }
    // For phone calls
    public String getCallerName() {
        return eventContactName;
    }
    public long getCallDate() {
        return eventDate;
    }

    public void setRowId(long id) { rowId = id; }


    public void setAddress(String addy) {
        eventContactAddress = addy;
    }
    public void setContactID(long contactID) {
        eventContactID = contactID;
    }
    public void setDate(long date) {
        eventDate = date;
    }
    public void setEventClass(int event_class) {
        eventClass = event_class;
    }

    public void setContactName(String name) {
       eventContactName = name;
    }
    public void setContactKey(String key) {
        eventContactKey = key;
    }


}
