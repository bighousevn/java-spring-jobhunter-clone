package vn.bighousevn.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.bighousevn.jobhunter.domain.Skill;
import vn.bighousevn.jobhunter.domain.response.ResultPaginationDTO;
import vn.bighousevn.jobhunter.repository.SkillRepository;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill handleCreateSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public Skill handleUpdateSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public ResultPaginationDTO fetchAllSkills(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> skillPage = this.skillRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(skillPage.getTotalPages());
        mt.setTotal(skillPage.getNumberOfElements());

        rs.setMeta(mt);
        rs.setResult(skillPage.getContent());
        return rs;
    }

    public void handleDeleteSkillById(Skill currentSkill) {
        // delete job (inside job_skill table)

        currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));

        // delte subscriber inside subsriber_skill table)

        currentSkill.getSubscribers().forEach(item -> item.getSkills().remove(currentSkill));
        // delete skill
        this.skillRepository.delete(currentSkill);
    }

    public Skill fetchSkillById(long id) {
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        if (skillOptional.isPresent())
            return skillOptional.get();

        return null;
    }

    public boolean findSkill(String name) {
        return this.skillRepository.existsByName(name);
    }
}
