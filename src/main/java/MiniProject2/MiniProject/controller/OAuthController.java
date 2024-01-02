package MiniProject2.MiniProject.controller;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import MiniProject2.MiniProject.model.HolidayDates;
import MiniProject2.MiniProject.service.GoogleCalendarService;
import MiniProject2.MiniProject.service.HolidayService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.FileReader;
import java.util.List;

@Controller
public class OAuthController {

    @Autowired
    private HolidayService holidaySvc;

    @Autowired
    private GoogleCalendarService googleCalendarService;

    private static final String CREDENTIALS_PATH = "path_to_credentials.json";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    @GetMapping("/login")
    public String login(Model model) {
        try {
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new FileReader(CREDENTIALS_PATH));
            String clientId = clientSecrets.getDetails().getClientId();
            String googleAuthUrl = "https://accounts.google.com/o/oauth2/auth?" +
                    "scope=https%3A//www.googleapis.com/auth/calendar&" +
                    "access_type=offline&" +
                    "include_granted_scopes=true&" +
                    "response_type=code&" +
                    "state=state_parameter_passthrough_value&" +
                    "redirect_uri=http://localhost:8080/login/google/callback&" +
                    "client_id=" + clientId;

            return "redirect:" + googleAuthUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error";
        }
    }

    @GetMapping("/login/google/callback")
    public String googleCallback(@RequestParam String code, Model model) {
        try {
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new FileReader(CREDENTIALS_PATH));

            TokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(),
                    JSON_FACTORY,
                    "https://oauth2.googleapis.com/token",
                    clientSecrets.getDetails().getClientId(),
                    clientSecrets.getDetails().getClientSecret(),
                    code,
                    "http://localhost:8080/login/google/callback")
                    .execute();

            String accessToken = tokenResponse.getAccessToken();
            String refreshToken = tokenResponse.getRefreshToken();
            

            return "redirect:/checker";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error";
        }
    }

    @GetMapping("/addHolidayToGoogle")
    public String addHolidayToGoogle(Model model) {
        try {
            // Retrieve the stored user credentials (tokens)
            GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(tokens.getAccessToken(), expirationTime))
            .createScoped(Arrays.asList("https://www.googleapis.com/auth/calendar"))
            .toBuilder()
            .setRefreshToken(tokens.getRefreshToken())
            .build();

            
            List<HolidayDates> overlappingDates = holidaySvc.findOverlappingDatesForAllUsers();

            
            for (HolidayDates date : overlappingDates) {
                Event event = new Event()
                        .setSummary("Overlapping Holiday")
                        .setStart(new EventDateTime().setDate(new com.google.api.client.util.DateTime(date.getStartDate().toString())))
                        .setEnd(new EventDateTime().setDate(new com.google.api.client.util.DateTime(date.getEndDate().toString())));

                GoogleCalendarService.insertEvent("primary", event, credentials);
            }

            return "redirect:/successPage"; // Redirect to a success page
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error"; 
        }
    }
}

