package vn.bighousevn.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import vn.bighousevn.jobhunter.domain.Skill;
import vn.bighousevn.jobhunter.domain.Subscriber;
import vn.bighousevn.jobhunter.repository.SubscriberRepository;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;

    public SubscriberService(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    public boolean isSameEmail(String email) {
        return this.subscriberRepository.existsByEmail(email);
    }

    public Subscriber findById(long id) {
        Optional<Subscriber> subsOptional = this.subscriberRepository.findById(id);
        if (subsOptional.isPresent())
            return subsOptional.get();
        return null;
    }

    public Subscriber handleCreateSubscriber(Subscriber s) {

        List<Long> reqList = s.getSkills()
                .stream().map(item -> item.getId())
                .collect(Collectors.toList());

        List<Skill> skillList = this.subscriberRepository.findByIdIn(reqList);
        s.setSkills(skillList);

        return this.subscriberRepository.save(s);

    }

    public Subscriber handleUpdateSubscriber(Subscriber subsDB, Subscriber s) {

        if (subsDB.getSkills() != null) {
            List<Long> reqList = s.getSkills().stream()
                    .map(item -> item.getId()).collect(Collectors.toList());

            List<Skill> skillList = this.subscriberRepository.findByIdIn(reqList);

            subsDB.setSkills(skillList);
        }
        return this.subscriberRepository.save(subsDB);
    }
}
