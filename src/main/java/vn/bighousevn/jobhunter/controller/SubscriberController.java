package vn.bighousevn.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.bighousevn.jobhunter.domain.Subscriber;
import vn.bighousevn.jobhunter.service.SubscriberService;
import vn.bighousevn.jobhunter.util.annotation.ApiMessage;
import vn.bighousevn.jobhunter.util.error.IdInvalidException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("api/v1/")
public class SubscriberController {

    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    @ApiMessage("Create a subscriber")
    public ResponseEntity<Subscriber> handleCreateSubscriber(@RequestBody Subscriber s) throws IdInvalidException {

        if (this.subscriberService.isSameEmail(s.getEmail()))
            throw new IdInvalidException("Email " + s.getEmail() + " đã tồn tại");
        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.handleCreateSubscriber(s));
    }

    @PutMapping("/subscribers")
    public ResponseEntity<Subscriber> handleUpdateSubscriber(@RequestBody Subscriber s) throws IdInvalidException {

        // check id
        Subscriber subsDB = this.subscriberService.findById(s.getId());
        if (subsDB == null) {
            throw new IdInvalidException("Id " + s.getId() + " không tồn tại");
        }
        return ResponseEntity.ok().body(this.subscriberService.handleUpdateSubscriber(subsDB, s));

    }

}
