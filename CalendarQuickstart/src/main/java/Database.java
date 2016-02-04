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

    private com.google.api.services.calendar.Calendar g_service;

    public Database(com.google.api.services.calendar.Calendar service)
    {
        g_service = service;
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
        /*
        String emailAddress = "123456789000-abc123def456@developer.gserviceaccount.com";
JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
GoogleCredential credential = new GoogleCredential.Builder()
    .setTransport(httpTransport)
    .setJsonFactory(JSON_FACTORY)
    .setServiceAccountId(emailAddress)
    .setServiceAccountPrivateKeyFromP12File(new File("MyProject.p12"))
    .setServiceAccountScopes(Collections.singleton(SQLAdminScopes.SQLSERVICE_ADMIN))
    .build();*/
    }
}
