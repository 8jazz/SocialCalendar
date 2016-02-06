/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alberto
 */
import com.google.api.client.util.DateTime;

import com.google.api.services.calendar.model.*;

import java.io.IOException;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;
public class Calendar
{
    public static com.google.api.services.calendar.Calendar g_service;

    public Calendar() throws IOException
    {
        g_service = ApiConnect.singleton().getCalendar();
    }

    public void inserisciEvento() throws IOException
    {
        // Refer to the Java quickstart on how to setup the environment:
        // https://developers.google.com/google-apps/calendar/quickstart/java
        // Change the scope to CalendarScopes.CALENDAR and delete any stored
        // credentials.

        Event event = new Event()
                .setSummary("Google I/O 2015")
                .setLocation("800 Howard St., San Francisco, CA 94103")
                .setDescription("A chance to hear more about Google's developer products.");

        DateTime startDateTime = new DateTime("2016-02-06T09:00:00-07:00");
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Europe/Rome");
        event.setStart(start);

        DateTime endDateTime = new DateTime("2016-02-06T17:00:00-07:00");
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Europe/Rome");
        event.setEnd(end);

        String[] recurrence = new String[]
        {
            "RRULE:FREQ=DAILY;COUNT=2"
        };
        event.setRecurrence(Arrays.asList(recurrence));

        EventAttendee[] attendees = new EventAttendee[]
        {
            //new EventAttendee().setEmail("lpage@example.com"),
            //new EventAttendee().setEmail("sbrin@example.com"),
        };
        event.setAttendees(Arrays.asList(attendees));

        EventReminder[] reminderOverrides = new EventReminder[]
        {
            //new EventReminder().setMethod("email").setMinutes(24 * 60),
            //new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        String calendarId = "primary";
        event = g_service.events().insert(calendarId, event).execute();
        System.out.printf("Event created: %s\n", event.getHtmlLink());
    }

    public void leggi() throws IOException
    {
        // List the next 10 events from the primary calendar.
        
        //DateTime now = new DateTime(System.currentTimeMillis());
        DateTime now = new DateTime(Date.valueOf("2016-01-01"));
        
        
        Events events = g_service.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        System.out.println(g_service.calendarList().list().getFields());
        if (items.size() == 0)
        {
            System.out.println("No upcoming events found.");
        } else
        {
            System.out.println("Upcoming events");
            for (Event event : items)
            {
                DateTime start = event.getStart().getDateTime();
                if (start == null)
                {
                    start = event.getStart().getDate();
                }
                System.out.printf("%s (%s)\n", event.getSummary(), start);
            }
        }
    }

}
