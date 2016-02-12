
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alberto
 */
public class ApiConnect
{
    private static ApiConnect ref;
    
    //Application name.
    private final String APPLICATION_NAME = "EventTribe";
    //Directory to store user credentials for this application.
    private final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/calendar-java-quickstart");
    //Global instance of the {@link FileDataStoreFactory}.
    private FileDataStoreFactory DATA_STORE_FACTORY;
    //Global instance of the JSON factory.
    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    //Global instance of the HTTP transport.
    private HttpTransport HTTP_TRANSPORT;
    //Global instance of the scopes required by this quickstart.
    private final List<String> SCOPES = Arrays.asList(CalendarScopes.CALENDAR);

    public static com.google.api.services.calendar.Calendar g_service;
    
    private ApiConnect()
    {
        try
        {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (GeneralSecurityException | IOException t)
        {
            System.exit(1);
        }
    }
    
    public static ApiConnect singleton()
    {
        if (ref == null)
            ref = new ApiConnect();
        return ref;
    }
    
    public com.google.api.services.calendar.Calendar getServiceCalendar() throws IOException
    {
        Credential credential = null;
        try
        {
            credential = serviceCredential();
        } catch (GeneralSecurityException ex)
        {
        }

        return new com.google.api.services.calendar.Calendar.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    
    public com.google.api.services.calendar.Calendar getCalendar() throws IOException
    {
        Credential credential = authorize();
        

        return new com.google.api.services.calendar.Calendar.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    
     private GoogleCredential serviceCredential() throws GeneralSecurityException, IOException
    {   
        String emailAddress = "social-quartet@pelagic-fin-121115.iam.gserviceaccount.com";
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

    private Credential authorize() throws IOException
    {
        // Load client secrets.
        InputStream in = CalendarQuickstart.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

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
}
