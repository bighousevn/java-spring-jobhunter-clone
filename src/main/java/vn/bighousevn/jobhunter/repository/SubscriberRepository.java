package vn.bighousevn.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.bighousevn.jobhunter.domain.Skill;
import vn.bighousevn.jobhunter.domain.Subscriber;
import java.util.List;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long>, JpaSpecificationExecutor<Subscriber> {
    boolean existsByEmail(String email);

    List<Skill> findByIdIn(List<Long> id);
}
