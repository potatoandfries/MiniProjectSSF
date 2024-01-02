package MiniProject2.MiniProject.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.stereotype.Service;

@Service
public class GoogleCalendarService {

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public void insertEvent(String calendarId, Event event, GoogleCredentials credentials) throws IOException, GeneralSecurityException {
        Calendar service = getCalendarService(credentials);
        service.events().insert(calendarId, event).execute();
    }

    private Calendar getCalendarService(GoogleCredentials credentials) throws IOException, GeneralSecurityException {
        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("Holiday Planner")
                .build();
    }
}
