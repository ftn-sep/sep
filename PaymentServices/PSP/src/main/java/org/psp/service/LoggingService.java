package org.psp.service;

import org.psp.model.LogLevel;
import org.psp.model.LogItem;
import org.psp.repository.LogObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoggingService {

    @Autowired
    private LogObjectRepository logObjectRepository;

    public void log(String message, String classFrom, String requestURI, LogLevel level, String object, String remoteAddr) {
        String[] classPlace = classFrom.split("\\.");
        String classPlaceRes = "";
        if (classPlace.length - 1 == -1)
            classPlaceRes = classPlace[0];
        else
            classPlaceRes = classPlace[classPlace.length - 1];
        LogItem logItem = new LogItem(message, classPlaceRes, requestURI, level, object, remoteAddr);
        logObjectRepository.save(logItem);
    }
}
