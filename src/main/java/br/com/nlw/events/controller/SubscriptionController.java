package br.com.nlw.events.controller;

import br.com.nlw.events.DTO.ErrorMessage;
import br.com.nlw.events.DTO.SubscriptionResponse;
import br.com.nlw.events.exceptions.EventNotFoundException;
import br.com.nlw.events.exceptions.SubscriptionConflictException;
import br.com.nlw.events.exceptions.UserIndicatorNotFoundException;
import br.com.nlw.events.model.Subscription;
import br.com.nlw.events.model.User;
import br.com.nlw.events.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subscription")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping({"/{prettyName}", "/{prettyName}/{userId}"})
    public ResponseEntity<?> createSubscription(@PathVariable String prettyName, @RequestBody User subscriber, @PathVariable(required = false) Integer userId){

        try {
            SubscriptionResponse res = subscriptionService.createNewSubscription(prettyName, subscriber, userId);
            if (res != null){
                return ResponseEntity.ok().body(res);
            }
        }catch (EventNotFoundException ex){
            return ResponseEntity.status(404).body(new ErrorMessage(ex.getMessage()));
        }catch (SubscriptionConflictException ex){
            return ResponseEntity.status(409).body(new ErrorMessage(ex.getMessage()));
        }catch (UserIndicatorNotFoundException ex){
            return ResponseEntity.status(404).body(new ErrorMessage(ex.getMessage()));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{prettyName}/ranking")
    public ResponseEntity<?> generateRankingByEvent(@PathVariable String prettyName){
        try{
            return ResponseEntity.ok(subscriptionService.getCompleteRanking(prettyName).subList(0,3));
        }catch (EventNotFoundException ex){
            return ResponseEntity.status(404).body(new ErrorMessage(ex.getMessage()));
        }
    }

    @GetMapping("/{prettyName}/ranking/{userId}")
    public ResponseEntity<?> generateRankingByEventAndUserId(@PathVariable String prettyName, @PathVariable Integer userId){
        try{
            return ResponseEntity.ok(subscriptionService.getRankingByUser(prettyName,userId));
        }catch (Exception ex){
            return ResponseEntity.status(404).body(new ErrorMessage(ex.getMessage()));
        }
    }
}
