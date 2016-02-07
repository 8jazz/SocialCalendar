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
    private Calendar g_cal_handler;

    public Database(com.google.api.services.calendar.Calendar service)
    {
        g_service = service;
        g_cal_handler = new Calendar();
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

    public void insertUser()
    {
        g_cal_handler.
    }
}
