package com.zbinterview.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
    public String home() {
        return """
                Drones API is running.
                Endpoints:
                GET  /api/drones/available
                GET  /api/drones/{serial}/battery
                GET  /api/drones/{serial}/medications
                POST /api/drones (register)
                POST /api/drones/{serial}/load (body: ["MED_1","MED_2"])
                H2 console: /h2-console
                """;
    }
}


