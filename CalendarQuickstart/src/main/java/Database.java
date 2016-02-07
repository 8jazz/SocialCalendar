/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alberto
 */
import com.google.api.services.calendar.Calendar.CalendarList.List;
import com.google.api.services.calendar.Calendar.Events;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;

import java.io.IOException;

public class Database
{

    private final com.google.api.services.calendar.Calendar g_service;
    private final CalendarHandler g_cal_handler;
    private final String CAL_ID = "9c35p8869rqo4e974pfff5ba2s@group.calendar.google.com";

    public Database() throws IOException
    {
        g_service = ApiConnect.singleton().getServiceCalendar();
        g_cal_handler = new CalendarHandler();
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

    public void insertUser(String email) throws IOException
    {
        g_cal_handler.createEvent(CAL_ID, null, email, "", "", true, "2016-01-01", "2016-01-01", "000+01:00", null, null, g_service);
        g_cal_handler.viewCalendarEvents(CAL_ID, g_service);
    }

    public void deleteUser(String email) throws IOException
    {
        String event_id = g_cal_handler.getIdFromSummary(email, CAL_ID, g_service);
        if (event_id == null)
            System.exit(1);
        g_cal_handler.deleteEvent(event_id, CAL_ID, g_service);
    }
    
    public void addFollowing()
    {
    }
    
    public void removeFollowing()
    {
    }
}
