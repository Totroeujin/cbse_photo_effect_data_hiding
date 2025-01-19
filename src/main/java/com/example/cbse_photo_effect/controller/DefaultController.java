package com.example.cbse_photo_effect.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefaultController {

    @GetMapping("/")
    public String home() {
        return "Welcome to the CBSE Photo Effect API! Use the following endpoints:\n" +
                "1. POST /api/embed - To embed data into an image.\n" +
                "2. POST /api/embed/extract - To extract data from an image.";
    }

    @RequestMapping(value = "/**", method = {RequestMethod.POST, RequestMethod.GET})
    public String handleUnknownRoutes() {
        return "Error: The requested endpoint or method is not allowed. Please check the API documentation.";
    }
    // Sample data embedding:
    /*

    2025/Jan/19
    01:53 hour
    totroseah@gmail.com
    Seah Eu Jin
    017-4533228

    * */
}

