package vn.bighousevn.jobhunter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.bighousevn.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/")
public class HelloController {

    @GetMapping
    public String getHelloWorld() throws IdInvalidException{
        return "Hello World";
    }
}
