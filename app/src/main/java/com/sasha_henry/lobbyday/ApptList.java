/* Author : Joslyn Sasha Henry
 * Problem : LobbyDay schedule app
 * System : Windows 7 Professional, Android Studio 1.4 build #AI-141.2288178 using JRE 1.8.0_65
 * Compiler : target Android SDK version 23 (Marshmallow)
 *     minimum version 15 (Android 4.0.3–4.0.4 Ice Cream Sandwich)

 * Date: 25 Oct, 2015, modified 27 Jan, 2016
 *
 * This class represents a list of appointments.
 */

package com.sasha_henry.lobbyday;
/**
 *
 * @author J. Sasha Henry
 */
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Collections;
import java.util.HashMap;

public class ApptList {
    private static final Boolean isTestOutput = false;

    private ArrayList<Appointment> appointments;
    private int year;
    private int month;
    private int day;
    private HashMap<Integer, ArrayList<Appointment>> districtMap;
    private HashMap<Integer, ArrayList<Appointment>> teamMap;

    public ApptList(int year, int month, int day) {
        appointments = new ArrayList<>();
        this.year = year;
        this.month = month;
        this.day = day;
        districtMap = new HashMap<>();
        teamMap = new HashMap<>();
    }

    public ApptList(int year, int month, int day, ArrayList<Appointment> appointments) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.appointments = appointments;
        districtMap = new HashMap<>();
        teamMap = new HashMap<>();
        initializeDistrictMap();
        initializeTeamMap();
    }

    ArrayList<Appointment> getAppointments() {
        return appointments;
    }

    Boolean addAppointment(Appointment appt) {
        return appointments.add(appt);  //returns true for successful insertion
    }

    //retrieve an appointment by position
    Appointment getAppointment(int position) {
        return appointments.get(position);
    }

    public void clearApppointmentList() {
        appointments.clear();
        teamMap.clear();
        districtMap.clear();
    }

    public int getApptCount() {
        return appointments.size();
    }

    void printAppointments() {
        for (Appointment appt: appointments) {
            System.out.println(appt.toString());
        }
    } //end printAppointments

    Calendar setTime(int hour24, int minute) {
        SimpleDateFormat sdf12 = new SimpleDateFormat("h:mm a, MMM dd, yyyy");

        Calendar calendar = new GregorianCalendar(year, month, day, hour24, minute);
        if(isTestOutput) System.out.println("time " + sdf12.format(calendar.getTime()));
        return calendar;
    } //end setTime

    void sortApptsByTime() {
        Collections.sort(appointments);
    }

    void sortApptsByName() {
        Collections.sort(appointments, Appointment.NameComparator);
    } //end sortApptsByName

    void sortApptsByDistrict() {
        Collections.sort(appointments, Appointment.DistrictComparator);
    }

    void sortApptsByTeam() {
        Collections.sort(appointments, Appointment.TeamComparator);
    }

    ArrayList<Appointment> getAppointmentsByTeam(int team) {
        return teamMap.get(team);
    }

    void resetTeam(Appointment appt) {
        //TODO implement reset (includes teamMap update)
        System.out.println("You cannot do this operation at this time.");
    }

    void resetDistrict(Appointment appt) {
        //TODO implement reset (includes districtMap update)
        System.out.println("You cannot do this operation at this time.");
    }

    ArrayList<Appointment> getAppointmentsByDistrict(int district) {
        return districtMap.get(district);
    }

    void initializeDistrictMap() {
        ArrayList<Appointment> tempList;
        Integer district;

        for (Appointment appt: getAppointments()) {
            district = (Integer) appt.getDistrict();
            if (isTestOutput) {
                System.out.println("adding " + appt.getRepLastName()
                        + " to district map under key " + district);
            }
            if (districtMap.get(district) == null) {
                districtMap.put(district, new ArrayList<Appointment>());
            }
            tempList = districtMap.get(district);
            tempList.add(appt);
        }
    }

    void initializeTeamMap() {
        ArrayList<Appointment> tempList;
        Integer team;

        for (Appointment appt: getAppointments()) {
            team = (Integer) appt.getTeam();
            if (isTestOutput) {
                System.out.println("adding " + appt.getRepLastName()
                        + " to team map under key " + team);
            }
            if (teamMap.get(team) == null) {
                teamMap.put(team, new ArrayList<Appointment>());
            }
            tempList = teamMap.get(team);
            tempList.add(appt);
        }
    }

    //manually insert 20 sample Appointments
    void fillTestData() {
        //Appointment(int id, Calendar startTime, Chamber chamber, int district, String repFirstName, String repLastName, int team, String location){
        if(isTestOutput) System.out.println("filling test data");
        appointments.add(new Appointment(1, setTime(9, 0), Appointment.Chamber.HOUSE, 27, "Jill", "Fey", 6, "LEG 122D"));
        appointments.add(new Appointment(2, setTime(9, 0), Appointment.Chamber.HOUSE, 27, "Jake", "Fey", 6, "LEG 122D"));
        appointments.add(new Appointment(3, setTime(9, 0), Appointment.Chamber.HOUSE, 8, "Larry", "Haler", 5, "LEG 122D"));
        appointments.add(new Appointment(4, setTime(9, 0), Appointment.Chamber.HOUSE, 10, "Norma", "Smith", 8, "JLOB 435"));
        appointments.add(new Appointment(5, setTime(9, 15), Appointment.Chamber.HOUSE, 43, "Brady", "Walkinshaw", 1, "JLOB 328"));
        appointments.add(new Appointment(6, setTime(9, 30), Appointment.Chamber.SENATE, 44, "Steve", "Hobbs", 2, "JAC 239"));
        appointments.add(new Appointment(7, setTime(9, 30), Appointment.Chamber.HOUSE, 9, "Joe", "Schmick", 5, "LEG 426B"));
        appointments.add(new Appointment(8, setTime(9, 45), Appointment.Chamber.HOUSE, 43, "Frank", "Chopp", 1, "LEG 339C"));
        appointments.add(new Appointment(9, setTime(9, 45), Appointment.Chamber.HOUSE, 45, "Roger", "Goodman", 3, "LEG 436B"));
        appointments.add(new Appointment(10, setTime(9, 45), Appointment.Chamber.SENATE, 21, "Marko", "Liias", 4, "JAC 226"));
        appointments.add(new Appointment(11, setTime(11, 30), Appointment.Chamber.SENATE, 45, "Andy", "Hill", 3, "JAC 303"));
        appointments.add(new Appointment(12, setTime(12, 0), Appointment.Chamber.HOUSE, 23, "Drew", "Hansen", 6, "JLOB 369"));
        appointments.add(new Appointment(13, setTime(12, 15), Appointment.Chamber.SENATE, 1, "Rosemary", "McAuliffe", 1, "LEG 403"));
        appointments.add(new Appointment(14, setTime(12, 15), Appointment.Chamber.HOUSE, 3, "Timm", "Ormsby", 3, "LEG 122H"));
        appointments.add(new Appointment(15, setTime(12, 45), Appointment.Chamber.SENATE, 10, "Barbara", "Bailey", 8, "INB 109B"));
        appointments.add(new Appointment(16, setTime(13, 0), Appointment.Chamber.SENATE, 37, "Pramila", "Jayapal", 4, "JAC 213"));
        appointments.add(new Appointment(17, setTime(13, 0), Appointment.Chamber.HOUSE, 27, "Laurie", "Jinkins", 6, "JLOB 311"));
        appointments.add(new Appointment(18, setTime(15, 0), Appointment.Chamber.HOUSE, 39, "Dan", "Kristiansen", 1, "LEG 335C"));
        appointments.add(new Appointment(19, setTime(15, 0), Appointment.Chamber.HOUSE, 13, "Matt", "Manweller", 2, "JLOB 470"));
        appointments.add(new Appointment(20, setTime(15, 15), Appointment.Chamber.SENATE, 46, "David", "Frockt", 3, "LEG 402"));
        appointments.add(new Appointment(21, setTime(15, 15), Appointment.Chamber.HOUSE, 46, "Gerry", "Pollet", 3, "LEG 132C"));
        initializeDistrictMap();
        initializeTeamMap();
    }

}  //end class ApptList