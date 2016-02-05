
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.util.DateTime;

import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import com.google.api.services.calendar.model.AclRule.Scope;

public class CalendarQuickstart
{

    /**
     * Application name.
     */
    private static final String APPLICATION_NAME
            = "Google Calendar API Java Quickstart";

    /**
     * Directory to store user credentials for this application.
     */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/calendar-java-quickstart");

    /**
     * Global instance of the {@link FileDataStoreFactory}.
     */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY
            = JacksonFactory.getDefaultInstance();

    /**
     * Global instance of the HTTP transport.
     */
    private static HttpTransport HTTP_TRANSPORT;

    /**
     * Global instance of the scopes required by this quickstart.
     */
    private static final List<String> SCOPES
            = Arrays.asList(CalendarScopes.CALENDAR);

    static
    {
        try
        {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t)
        {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException
    {
        // Load client secrets.
        InputStream in
                = CalendarQuickstart.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets
                = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow
                = new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Calendar client service.
     *
     * @return an authorized Calendar client service
     * @throws IOException
     */
    public static com.google.api.services.calendar.Calendar getCalendarService() throws IOException {
        Credential credential = authorize();
        return new com.google.api.services.calendar.Calendar.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

            
    public static void main(String[] args) throws IOException
    {
        // Build a new authorized API client service.
        // Note: Do not confuse this class with the
        //   com.google.api.services.calendar.model.Calendar class.
        com.google.api.services.calendar.Calendar service
                = getCalendarService(); // da mettere globale!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        
        List<CalendarListEntry> ciao = viewCalendarsName(service);
        System.out.println();
        System.out.println();
        viewCalendarEvents("primary", service);
        System.out.println();
        System.out.println();
        //con questo for stampa tutti gli eventi: per ogni calendario in ordine di data.
        for (CalendarListEntry calendar_list_entry : ciao) {
            viewCalendarEvents(calendar_list_entry.getId() , service);
        }
        //updateCalendar("v45mue9uf7gk2a3dpl9shft3q0@group.calendar.google.com", null, null, "Bassano", null, "user", "albertoscalco11@gmail.com", "reader", service);
        EventAttendee[] amicimiei = createEventAttendees(new String[]{"albertoscalco11@gmail.com"});
        createEvent ("v45mue9uf7gk2a3dpl9shft3q0@group.calendar.google.com", "Questa e una prova", "Bassano", "description prova", "2016-01-05T09:00:00", "2016-01-05T10:00:00", "000+01:00", new String[] {"RRULE:FREQ=DAILY;COUNT=2"}, amicimiei, service);
    }
    
    //ritorna la lista dei calendari
    private static List<CalendarListEntry> viewCalendarsName (com.google.api.services.calendar.Calendar services) throws IOException{
        // Iterate through entries in calendar list
        String pageToken = null;
        List<CalendarListEntry> total = new ArrayList<>();
        do {
          CalendarList calendar_list = services.calendarList().list().setPageToken(pageToken).execute();
          List<CalendarListEntry> items = calendar_list.getItems();

          for (CalendarListEntry calendar_list_entry : items) {
            System.out.println(calendar_list_entry.getSummary()); //da togliere.................................
            System.out.println(calendar_list_entry.getId());
            total.add(calendar_list_entry);
          }
          pageToken = calendar_list.getNextPageToken();
        } while (pageToken != null);
        return total;
    }
    
    //ViewCalendarEvents visualizza tutti gli eventi dei calendari contenuti nella lista passata come argomento
    private static void viewCalendarEvents(String id, com.google.api.services.calendar.Calendar service) throws IOException {
        // List the next 50 events from the "ID" calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list(id)
                //.setMaxResults(50)
                //.setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        if (items.isEmpty()) {
            System.out.println("No upcoming events found on this calendar.");
        } else {
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
    
    //rimuove calendario se e solo se è diverso dal primario.
    private static void removeCalendar (String id, com.google.api.services.calendar.Calendar service) throws IOException {
        service.calendars().delete(id).execute();
    }   
    
    private static void createCalendar (String name, com.google.api.services.calendar.Calendar service) throws IOException {
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
    private static void updateCalendar (String id, String summary, String description, String location, String timeZone, String scope_type, String scope_value, String role, com.google.api.services.calendar.Calendar service) throws IOException {
        com.google.api.services.calendar.model.Calendar calendar =
        service.calendars().get(id).execute();
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
    
    private static void createEvent (String calendar_id, String summary, String location, String description, String date_time_start, String date_time_end, String time_zone, String[] recurrence, EventAttendee[] attendees, com.google.api.services.calendar.Calendar service) throws IOException {

        Event event = new Event()
                .setSummary(summary)
                .setLocation(location)
                .setDescription(description);

        DateTime startDateTime = new DateTime(date_time_start);
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone(time_zone);
        event.setStart(start);

        DateTime endDateTime = new DateTime(date_time_end);
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone(time_zone);
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
    private static EventAttendee[] createEventAttendees (String[] emails)  throws IOException {
    
        EventAttendee[] attendees = new EventAttendee[emails.length];
        int i = 0;
        while (emails[i] != null){
            attendees[i] = new EventAttendee().setEmail(emails[i]);
        }
        return attendees;
    }
    
    //cancella l'evento
    private static void deleteEvent(String event_id, String calendar_id, com.google.api.services.calendar.Calendar service)  throws IOException {
        service.events().delete(calendar_id, event_id).execute();
    }
    
    //date id calendario e id evento ritorna l'oggetto Evento
     private static Event returnEvent(String event_id, String calendar_id, com.google.api.services.calendar.Calendar service)  throws IOException {
        return service.events().get(calendar_id, event_id).execute();
    }
    
    //aggiorna SOLO ciò che non è nullo. NB se si cambia la data di inizio / fine BISOGNA mettere anche time_zone ASSOLUTAMENTE!!!
    private static void updateEvent (String event_id, String calendar_id, String summary, String location, String description, String date_time_start, String date_time_end, String time_zone, String[] recurrence, EventAttendee[] attendees, com.google.api.services.calendar.Calendar service) throws IOException {
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
        if (date_time_start != null) /*NB: deve essere not null time_zone*/ {
            DateTime startDateTime = new DateTime(date_time_start);
            EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone(time_zone);
            event.setStart(start);
        }
        if (date_time_end != null) /*NB deve essere not null time_zone*/ {
            DateTime endDateTime = new DateTime(date_time_end);
            EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone(time_zone);
            event.setEnd(end);
        }
        if(recurrence != null)
            event.setRecurrence(Arrays.asList(recurrence));
        if(attendees != null)
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
        System.out.println(event.getId());
        System.out.printf("Event updated: %s\n", event.getHtmlLink());
    }
}