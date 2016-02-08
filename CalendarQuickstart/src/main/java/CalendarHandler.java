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
import com.google.api.services.calendar.model.AclRule.Scope;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CalendarHandler
{

    private com.google.api.services.calendar.Calendar g_service;

    public CalendarHandler() throws IOException
    {
        g_service = ApiConnect.singleton().getCalendar();
    }

    //ritorna la lista dei calendari
    private List<CalendarListEntry> viewCalendarsName(com.google.api.services.calendar.Calendar services) throws IOException
    {
        // Iterate through entries in calendar list
        String pageToken = null;
        List<CalendarListEntry> total = new ArrayList<>();
        do
        {
            CalendarList calendar_list = services.calendarList().list().setPageToken(pageToken).execute();
            List<CalendarListEntry> items = calendar_list.getItems();

            for (CalendarListEntry calendar_list_entry : items)
            {
                System.out.println(calendar_list_entry.getSummary()); //da togliere.................................
                System.out.println(calendar_list_entry.getId());
                total.add(calendar_list_entry);
            }
            pageToken = calendar_list.getNextPageToken();
        } while (pageToken != null);
        return total;
    }

    public void viewCalendarEvents(String id, com.google.api.services.calendar.Calendar service) throws IOException
    {
        // List the next 50 events from the "ID" calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list(id)
                //.setMaxResults(50)
                //.setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        if (items.isEmpty())
            System.out.println("No upcoming events found on this calendar.");
        else
            for (Event event : items)
            {
                DateTime start = event.getStart().getDateTime();
                if (start == null)
                    start = event.getStart().getDate();
                System.out.printf("%s (%s)\n", event.getSummary(), start);
            }
    }

    //rimuove calendario se e solo se è diverso dal primario.
    public static void removeCalendar(String id, com.google.api.services.calendar.Calendar service) throws IOException
    {
        service.calendars().delete(id).execute();
    }

    public void createCalendar(String name, com.google.api.services.calendar.Calendar service) throws IOException
    {
        // Create a new calendar
        com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();
        calendar.setSummary(name);
        // Insert the new calendar
        com.google.api.services.calendar.model.Calendar createdCalendar = service.calendars().insert(calendar).execute();

        System.out.println(createdCalendar.getId()); //da togliere.......................................
    }

    //modifica ciò che viene passato per argomento e non è nullo. se è nullo lascia tutto com'era prima.
    /*
    queste variabili DEVONO avere questi valori. la validità non viene controllata.
    scope_type = "default" || "user" || "group" || "domain"
    scope_value = email address of a user
    role = "none" || "freeBusyReader" || "reader" || "writer" || "owner" 
     */
    public void updateCalendar(String id, String summary, String description, String location, String timeZone, String scope_type, String scope_value, String role, com.google.api.services.calendar.Calendar service) throws IOException
    {
        com.google.api.services.calendar.model.Calendar calendar
                = service.calendars().get(id).execute();
        // Make a change if it's possible
        if (summary != null)
            calendar.setSummary(summary);
        if (description != null)
            calendar.setDescription(description);
        if (location != null)
            calendar.setLocation(location);
        if (timeZone != null)
            calendar.setTimeZone(timeZone);

        // Create access rule with associated scope
        AclRule rule = new AclRule();
        Scope scope = new Scope();
        scope.setType(scope_type).setValue(scope_value);
        rule.setScope(scope).setRole(role);

        // Insert new access rule
        AclRule createdRule = service.acl().insert(id, rule).execute();

        System.out.println(createdRule.getId());
        System.out.println(calendar.values());
    }

    public void createEvent(String calendar_id, String event_id, String summary, String location, String description, boolean all_day, String date_time_start, String date_time_end, String time_zone, String[] recurrence, EventAttendee[] attendees, com.google.api.services.calendar.Calendar service) throws IOException
    {

        Event event = new Event()
                .setSummary(summary)
                .setLocation(location)
                .setDescription(description);
        if (event_id != null || !"".equals(event_id))
            event.setId(event_id);

        DateTime startDateTime = new DateTime(date_time_start);
        EventDateTime start = new EventDateTime()
                .setTimeZone(time_zone);
        if (all_day)
            start.setDate(startDateTime);
        else
            start.setDateTime(startDateTime);
        event.setStart(start);

        DateTime endDateTime = new DateTime(date_time_end);
        EventDateTime end = new EventDateTime()
                .setTimeZone(time_zone);
        if (all_day)
            end.setDate(endDateTime);
        else
            end.setDateTime(endDateTime);
        event.setEnd(end);

        if (recurrence != null)
            event.setRecurrence(Arrays.asList(recurrence));
        if (attendees != null)
            event.setAttendees(Arrays.asList(attendees));

        /*EventReminder[] reminderOverrides = new EventReminder[]
        {
            new EventReminder().setMethod("email").setMinutes(24 * 60),
            new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);*/
        event = service.events().insert(calendar_id, event).execute();

        System.out.println(event.getId());
        System.out.printf("Event created: %s\n", event.getHtmlLink());
    }

    //crea un array di attendees dato come argomento un arrai di String che contengono le emails degli attendees
    public EventAttendee[] createEventAttendees(String[] emails) throws IOException
    {

        EventAttendee[] attendees = new EventAttendee[emails.length];
        for (int i = 0; i < emails.length; i++)
            attendees[i] = new EventAttendee().setEmail(emails[i]);
        return attendees;
    }

    //cancella l'evento
    public void deleteEvent(String event_id, String calendar_id, com.google.api.services.calendar.Calendar service) throws IOException
    {
        service.events().delete(calendar_id, event_id).execute();
    }

    //date id calendario e id evento ritorna l'oggetto Evento
    public Event returnEvent(String event_id, String calendar_id, com.google.api.services.calendar.Calendar service) throws IOException
    {
        return service.events().get(calendar_id, event_id).execute();
    }

    //aggiorna SOLO ciò che non è nullo. NB se si cambia la data di inizio / fine BISOGNA mettere anche time_zone ASSOLUTAMENTE!!!
    public void updateEvent(String event_id, String calendar_id, String summary, String location, String description, String date_time_start, String date_time_end, String time_zone, String[] recurrence, EventAttendee[] attendees, com.google.api.services.calendar.Calendar service) throws IOException
    {
        // Refer to the Java quickstart on how to setup the environment:
        // https://developers.google.com/google-apps/calendar/quickstart/java
        // Change the scope to CalendarScopes.CALENDAR and delete any stored
        // credentials.
        Event event = returnEvent(event_id, calendar_id, service);
        if (summary != null)
            event.setSummary(summary);
        if (location != null)
            event.setLocation(location);
        if (description != null)
            event.setDescription(description);
        if (date_time_start != null)
        /*NB: deve essere not null time_zone*/ {
            DateTime startDateTime = new DateTime(date_time_start);
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone(time_zone);
            event.setStart(start);
        }
        if (date_time_end != null)
        /*NB deve essere not null time_zone*/ {
            DateTime endDateTime = new DateTime(date_time_end);
            EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone(time_zone);
            event.setEnd(end);
        }
        if (recurrence != null)
            event.setRecurrence(Arrays.asList(recurrence));
        if (attendees != null)
            event.setAttendees(Arrays.asList(attendees));

        /*EventReminder[] reminderOverrides = new EventReminder[]
        {
            new EventReminder().setMethod("email").setMinutes(24 * 60),
            new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);*/
        
        service.events().update(calendar_id, event.getId(), event).execute();
        System.out.println(event.getId());
        System.out.printf("Event updated: %s\n", event.getHtmlLink());
    }

    public String getIdFromSummary(String summary, String cal_id, com.google.api.services.calendar.Calendar service) throws IOException
    {
        String pageToken = null;
        do
        {
            Events events = service.events().list(cal_id).setPageToken(pageToken).execute();
            List<Event> items = events.getItems();
            for (Event event : items)
                if (event.getSummary().equals(summary))
                    return event.getId();
            pageToken = events.getNextPageToken();
        } while (pageToken != null);
        return null;
    }
}
