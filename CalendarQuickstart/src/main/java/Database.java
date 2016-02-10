/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alberto
 */
import com.google.api.services.calendar.model.Calendar;

import java.io.IOException;


public class Database
{

    private final com.google.api.services.calendar.Calendar g_service;
    private final CalendarHandler g_cal_handler;
    private final String CAL_ID = "9c35p8869rqo4e974pfff5ba2s@group.calendar.google.com";
    private final String DATE = "2016-01-01";
    private final XmlParser g_parser;

    public Database() throws IOException
    {
        g_service = ApiConnect.singleton().getServiceCalendar();
        g_cal_handler = new CalendarHandler();
        g_parser = new XmlParser();
    }

    public void insertPublicCal(String cal_name, String user_id) throws IOException //TO TEST
    {
        // Create a new calendar
        com.google.api.services.calendar.model.Calendar calendar = new Calendar();
        calendar.setSummary("public");
        calendar.setTimeZone("Europe/Rome");

        // Insert the new calendar
        Calendar createdCalendar = g_service.calendars().insert(calendar).execute();

        System.out.println(createdCalendar.getId());
    }

    public void insertUser(String user, String name, String surname) throws IOException
    {
        String description = g_parser.createUserInfo(name,surname);
        
        g_cal_handler.createEvent(CAL_ID, null, user, "", description, true, "2016-01-01", "2016-01-01", "000+01:00", null, null, g_service);
        g_cal_handler.viewCalendarEvents(CAL_ID, g_service);
    }

    public void deleteUser(String user) throws IOException
    {
        String event_id = g_cal_handler.getIdFromSummary(user, CAL_ID, g_service);
        if (event_id == null)   //non esiste l'utente che si vuole eliminare
            System.exit(1);
        g_cal_handler.deleteEvent(event_id, CAL_ID, g_service);
    }

    public void addFollowing(String user, String following) throws IOException
    {
        //retrieve user's xml info
        String event_id = g_cal_handler.getIdFromSummary(user, CAL_ID, g_service);
        String event_desc = g_cal_handler.returnEvent(event_id, CAL_ID, g_service).getDescription();
        //add a following email in the folliwing xml tag
        event_desc = g_parser.addElement(event_desc, "following", "email", following);    
        //update the info
        g_cal_handler.updateEvent(event_id, CAL_ID, null, null, event_desc, null, null, null, null, null, g_service);

    }

    public String[] getFollowing(String user) throws IOException
    {
        //retrieve user's xml info
        String event_id = g_cal_handler.getIdFromSummary(user, CAL_ID, g_service);
        String xml = g_cal_handler.returnEvent(event_id, CAL_ID, g_service).getDescription();
        return g_parser.getElements(xml, "email");
    }

    public void removeFollowing(String user, String following) throws IOException
    {
        //retrieve user's xml info
        String event_id = g_cal_handler.getIdFromSummary(user, CAL_ID, g_service);
        String event_desc = g_cal_handler.returnEvent(event_id, CAL_ID, g_service).getDescription();  
        //add a following email in the folliwing xml tag
        event_desc = g_parser.removeElement(event_desc, "email", following);
        //update the info
        g_cal_handler.updateEvent(event_id, CAL_ID, null, null, event_desc, null, null, null, null, null, g_service);
    }

}
