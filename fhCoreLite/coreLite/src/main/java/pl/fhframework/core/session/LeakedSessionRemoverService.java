package pl.fhframework.core.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.fhframework.UserSession;

import java.util.Set;

@RestController
public class LeakedSessionRemoverService {
    @Autowired
    UserSessionRepository userSessionRepository;

    @GetMapping("/leakedsessions/sessionsCount")
    int sessionsCount() {
        return userSessionRepository.getNoOfSessions();
    }

    @GetMapping("/leakedsessions/printSessions")
    String printSessions() {
        Set<UserSession> userSessions = userSessionRepository.getAllUserSessions();
        StringBuilder sb = new StringBuilder();
        for (UserSession session : userSessions) {
            String prefix = (session.hasNotBeenUsedIn(1000 * 3600 * 12))? "!!!!" : "    ";
            String userName = UserSessionRepository.getUserLogin(session);
            String lastUpdate = milisToTime(session.getHowLongIsUnusedInMillis())+" ago.";
            sb.append(prefix+" " +userName+" -> " + lastUpdate +"\r\n");
        }
        return sb.toString();
    }


    private String milisToTime(long millisPeriod) {
        long tot_sek = millisPeriod / 1000;
        long houres = tot_sek / 3600;
        long minutes = tot_sek % 3600 / 60;
        long seconds = tot_sek % 60;
        long milis = millisPeriod % 1000;


        String result = houres + "h " + minutes + "m " + seconds + "." + milis + "s";
        return result;
    }

}
