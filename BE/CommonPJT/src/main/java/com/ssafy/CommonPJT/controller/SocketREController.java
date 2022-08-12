package com.ssafy.CommonPJT.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Log4j2

public class SocketREController {
    @GetMapping("/reactserver")
    public String chatGET(){
        log.info("@SocketController, chat GET()");
        //return "socket";
        return null;
    }
}
