/* Author : Joslyn Sasha Henry
 * Problem : LobbyDay schedule app
 * System : Windows 7 Professional, Android Studio 1.4 build #AI-141.2288178 using JRE 1.8.0_65
 * Compiler : target Android SDK version 23 (Marshmallow)
 *     minimum version 15 (Android 4.0.3–4.0.4 Ice Cream Sandwich)

 * Date: 25 Oct, 2015, modified 27 Jan, 2016
 *
 * This class represents an appointment.
 */
package com.sasha_henry.lobbyday;
/**
 *
 * @author J. Sasha Henry
 */
import java.util.*;
import java.text.*;

public class Appointment implements Comparable<Appointment> {
    private int id;
    private Long startTime;
    private String startTimeString;
    private int district;
    private Chamber chamber;
    private String repFirstName;
    private String repLastName;
    private int team;
    private String location;
    private Boolean isScheduled = false;

    enum Chamber {HOUSE, SENATE};

    public Appointment(int id, Long startTimeLong, Chamber chamber, int district, String repFirstName,
                       String repLastName, int team, String location){
        this.id = id;
        this.startTime = startTimeLong;
        this.chamber = chamber;
        this.district = district;
        this.repFirstName = repFirstName;
        this.repLastName = repLastName;
        this.location = location;
        this.team = team;
        startTimeString = Appointment.longTimeToShortString(startTime);
    }

    public Appointment(int id, Long startTimeLong, Chamber chamber, int district, String repFirstName,
                       String repLastName, int team, String location, Boolean isScheduled){
        this(id, startTimeLong, chamber, district, repFirstName,repLastName, team, location);
        this.isScheduled = isScheduled;
    }

    public Appointment(int id, Calendar startTimeCal, Chamber chamber, int district, String repFirstName,
                       String repLastName, int team, String location){
        this(id, startTimeCal.getTimeInMillis(), chamber, district, repFirstName,
                repLastName, team, location);
    }

    public Appointment(Long startTimeLong) {
        this.startTime = startTimeLong;
        district = -1;
        startTimeString = Appointment.longTimeToShortString(this.startTime);
    }

    public Calendar getStartTime(){
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(startTime);
        return cal;
    }
    public int getId() {return id; }
    public Long getStartTimeLong() { return startTime; }
    public Chamber getChamber(){ return chamber; }
    public int getDistrict(){ return district; }
    public String getRepFirstName(){ return repFirstName; }
    public String getRepLastName() { return repLastName; }
    public String getLocation() { return location; }
    public int getTeam() { return team; }
    public Boolean getIsScheduled() { return isScheduled; }
    public String getStartTimeString() { return startTimeString; }

    public void setStartTime(Calendar startTime){
        this.startTime = startTime.getTimeInMillis();
        startTimeString = Appointment.longTimeToShortString(this.startTime);
    }
    public void setChamber(Chamber chamber){ this.chamber = chamber; }
    public void setDistrict(int district){ this.district = district; }
    public void setRepFirstName(String repFirstName){ this.repFirstName = repFirstName; }
    public void setRepLastName(String repLastName) { this.repLastName = repLastName; }
    public void setLocation(String location) {this.location = location; }
    public void setTeam(int team) {this.team = team; }
    public void setIsScheduled(Boolean isScheduled) {this.isScheduled = isScheduled; }

    public static String longTimeToShortString(Long time){
        //a 	Am/pm marker 	Text 	PM
        //h 	Hour in am/pm (1-12) 	Number 	12
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(time);

        return sdf.format(cal.getTime());
    }

    public String toString(){
        //a 	Am/pm marker 	Text 	PM
        //h 	Hour in am/pm (1-12) 	Number 	12
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a, MMM dd, yyyy");
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(startTime);
        String sch = "";

        if (getLocation()==null) { return sdf.format(cal.getTime()); }
        else {
            if (!getIsScheduled()) sch = "not ";
            return "Appointment:" + sdf.format(cal.getTime()) + " with " +
                    getTitle(getChamber()) + " " + getRepLastName() + ", " +
                    getRepFirstName() + " (" + getDistrict() + "), team " +
                    getTeam() + " at " + getLocation() + " (" + sch + "scheduled)";
        }
    }

    public static String getTitle (Chamber chamber) {
        if (chamber == Chamber.HOUSE) {return "Rep.";}
        if (chamber == Chamber.SENATE) {return "Sen.";}
        else return "";
    }

    //enable default sorting by time
    @Override
    public int compareTo(Appointment other) {
        Long otherTime = other.getStartTimeLong();
        //ascending order
        return this.startTime.compareTo(otherTime);
    } //end compareTo

    public static final Comparator<Appointment> DistrictComparator
            = new Comparator<Appointment>(){
        @Override
        public int compare(Appointment o1, Appointment o2) {
            // subtracting ints can lead to overflow if one int is negative
            // so cast to Integer and use compareTo()
            Integer o1Dist = o1.getDistrict();
            Integer o2Dist = o2.getDistrict();
            return o1Dist.compareTo(o2Dist);
        }
    };

    public static final Comparator<Appointment> TeamComparator
            = new Comparator<Appointment>(){
        @Override
        public int compare(Appointment o1, Appointment o2) {
            // subtracting ints can lead to overflow if one int is negative
            // so cast to Integer and use compareTo()
            Integer o1Team = o1.getTeam();
            Integer o2Team = o2.getTeam();
            return o1Team.compareTo(o2Team);
        }
    };

    public static final Comparator<Appointment> NameComparator
            = new Comparator<Appointment>() {
        @Override
        public int compare(Appointment o1, Appointment o2) {
            String o1name = o1.getRepLastName() + "" + o1.getRepFirstName();
            return o1.getRepLastName().compareTo(o2.getRepLastName());
        }
    };

    //for future implementation of save to file or upload to website
    public String toJSON(){
        StringBuilder b = new StringBuilder();
        b.append("{");
        b.append("\n");
        //int id;
        b.append("\"id\":");
        b.append(getId());
        b.append(",\n");
        //Long startTime;
        b.append("\"startTime\":");
        b.append(getStartTimeLong());
        b.append(",\n");
        //int district;
        b.append("\"district\":");
        b.append(getDistrict());
        b.append(",\n");
        //Chamber chamber;
        b.append("\"chamber\":\"");
        b.append(chamber.toString());
        b.append("\"");
        b.append(",\n");
        //String repFirstName;
        b.append("\"repFirstName\":\"");
        b.append(getRepFirstName());
        b.append("\"");
        b.append(",\n");
        //String repLastName
        b.append("\"repLastName\":\"");
        b.append(getRepLastName());
        b.append("\"");
        b.append(",\n");
        //int team
        b.append("\"team\":");
        b.append(getDistrict());
        b.append(",\n");
        //String location;
        b.append("\"location\":\"");
        b.append(getLocation());
        b.append("\"");
        b.append(",\n");
        //Boolean isScheduled;
        b.append("\"isScheduled\":");
        b.append(getIsScheduled());
        b.append("\n");
        b.append("}");

        return b.toString();
    }//end toJSON

}
