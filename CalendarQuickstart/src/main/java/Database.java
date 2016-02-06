/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alberto
 */

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Calendar;
import java.io.File;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class Database
{
    //Global instance of the scopes required by this quickstart.
    private final List<String> SCOPES = Arrays.asList(CalendarScopes.CALENDAR);
    private com.google.api.services.calendar.Calendar g_service;

    public Database(com.google.api.services.calendar.Calendar service)
    {
        g_service = service;
    }

    
    private GoogleCredential stocazzo() throws GeneralSecurityException, IOException
    {   
        String emailAddress = "social-quartet@pelagic-fin-121115.iam.gserviceaccount.com";
        JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(emailAddress)
                .setServiceAccountPrivateKeyFromP12File(new File("src/main/resources/MyProject.p12"))
                .setServiceAccountScopes(SCOPES)
                .build();
        
        return credential;
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
        
    }
}
