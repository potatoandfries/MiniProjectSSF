package MiniProject2.MiniProject.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

import MiniProject2.MiniProject.model.HolidayDates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GoogleCalendarService {

    private static final Logger log = LoggerFactory.getLogger(GoogleCalendarService.class);

    @Value("${google.calendar.clientSecretPath}")
    private String clientSecretPath;

    @Autowired
    private HolidayService holidayService;

    private final String APPLICATION_NAME = "Calendar React App";
    private final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private GoogleClientSecrets clientSecrets;

    public String getAuthorizationUrl() throws IOException, GeneralSecurityException {
        Credential credential = getCredentials();

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, SCOPES)
                .setAccessType("offline")
                .build();

        GoogleAuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl();
        return authorizationUrl.setRedirectUri("http://localhost:8080/google-calendar-callback").build();
    }

    public void addHolidayToCalendar(List<HolidayDates> overlappingDates, String redirectUri)
            throws IOException, GeneralSecurityException {
        Credential credential = getCredentials();

        if (!overlappingDates.isEmpty()) {
            HolidayDates firstOverlap = overlappingDates.get(0);

            Event holidayEvent = new Event();
            holidayEvent.setSummary("Holiday");
            holidayEvent.setDescription("Description of the holiday event");

            Date startDate = Date.from(firstOverlap.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(firstOverlap.getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant());

            EventDateTime start = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(startDate, TimeZone.getTimeZone(ZoneId.systemDefault())))
                    .setTimeZone("UTC");

            EventDateTime end = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(endDate, TimeZone.getTimeZone(ZoneId.systemDefault())))
                    .setTimeZone("UTC");

            holidayEvent.setStart(start);
            holidayEvent.setEnd(end);

            EventReminder[] reminderOverrides = new EventReminder[]{
                    new EventReminder().setMethod("popup").setMinutes(10),
                    new EventReminder().setMethod("email").setMinutes(30)
            };
            Event.Reminders reminders = new Event.Reminders().setUseDefault(false).setOverrides(Arrays.asList(reminderOverrides));
            holidayEvent.setReminders(reminders);

            Calendar calendarService = getCalendarService(credential, redirectUri);
            calendarService.events().insert("primary", holidayEvent).execute();
        }
    }

    public void handleAuthorizationCallback(String code) throws IOException, GeneralSecurityException {
        Credential credential = getCredentials();

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, SCOPES)
                .setAccessType("offline")
                .build();

        TokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri("http://localhost:8080/google-calendar-callback").execute();
        flow.createAndStoreCredential(tokenResponse, "user");
    }

    private Credential getCredentials() throws IOException, GeneralSecurityException {
        try {
            InputStream in = getClass().getResourceAsStream("/client_secret.json");
    
            if (in == null) {
                log.error("Resource not found: client_secret.json");
                throw new IOException("Resource not found: client_secret.json");
            }
    
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(new com.google.api.client.util.store.FileDataStoreFactory(new java.io.File("tokens")))
                    .setAccessType("offline")
                    .build();

            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        } catch (IOException | GeneralSecurityException e) {
            log.error("Error getting credentials:", e);
            throw e;
        }
    }

    private Calendar getCalendarService(Credential credential, String redirectUri)
            throws IOException, GeneralSecurityException {
        return new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .setHttpRequestInitializer(request -> request.setInterceptor(credential))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
