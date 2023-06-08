package pl.languagelearn.application.time;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TimeProvider {
    public LocalDateTime getCurrentTime(){
        return LocalDateTime.now();
    }

}
