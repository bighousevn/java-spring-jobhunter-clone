package vn.bighousevn.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.bighousevn.jobhunter.domain.Company;
import vn.bighousevn.jobhunter.domain.Job;
import vn.bighousevn.jobhunter.domain.Skill;
import vn.bighousevn.jobhunter.domain.response.ResultPaginationDTO;
import vn.bighousevn.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.bighousevn.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.bighousevn.jobhunter.repository.CompanyRepository;
import vn.bighousevn.jobhunter.repository.JobRepository;
import vn.bighousevn.jobhunter.repository.SkillRepository;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository,
            CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
    }

    public ResultPaginationDTO fetchAllJobs(Specification<Job> spec, Pageable pageable) {

        Page<Job> jobPage = this.jobRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(jobPage.getTotalPages());
        mt.setTotal(jobPage.getTotalElements());

        rs.setMeta(mt);

        rs.setResult(jobPage.getContent());

        return rs;
    }

    public Job handleCreateJob(Job j) {
        // check skills
        if (j.getSkills() != null) {
            List<Long> reqSkills = j.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            j.setSkills(dbSkills);
        }
        if (j.getCompany() != null) {
            Optional<Company> cOptional = this.companyRepository.findById(j.getId());
            cOptional.ifPresent(j::setCompany);
        }

        return this.jobRepository.save(j);
    }

    public Optional<Job> fetchJobById(long id) {
        return this.jobRepository.findById(id);
    }

    public Job handleUpdateJob(Job j, Job jobInDb) {
        // check skills
        if (j.getSkills() != null) {
            List<Long> reqSkills = j.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            jobInDb.setSkills(dbSkills);
        }

        if (j.getCompany() != null) {
            Optional<Company> cOptional = this.companyRepository.findById(j.getId());
            cOptional.ifPresent(jobInDb::setCompany);
        }

        // update correct info
        jobInDb.setName(j.getName());
        jobInDb.setSalary(j.getSalary());
        jobInDb.setQuantity(j.getQuantity());
        jobInDb.setLocation(j.getLocation());
        jobInDb.setLevel(j.getLevel());
        jobInDb.setStartDate(j.getStartDate());
        jobInDb.setEndDate(j.getEndDate());
        jobInDb.setActive(j.isActive());

        // update job
        return this.jobRepository.save(jobInDb);
    }

    public void handleDeleteJob(long id) {
        this.jobRepository.deleteById(id);
    }

    // Convert DTO

    public ResCreateJobDTO convertToResCreateJobDTO(Job job) {

        ResCreateJobDTO dto = new ResCreateJobDTO();
        dto.setId(job.getId());
        dto.setName(job.getName());
        dto.setSalary(job.getSalary());
        dto.setQuantity(job.getQuantity());
        dto.setLocation(job.getLocation());
        dto.setLevel(job.getLevel());
        dto.setStartDate(job.getStartDate());
        dto.setEndDate(job.getEndDate());
        dto.setActive(job.isActive());
        dto.setCreatedAt(job.getCreatedAt());
        dto.setCreatedBy(job.getCreatedBy());

        if (job.getSkills() != null) {
            List<String> skills = job.getSkills()
                    .stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }
        return dto;
    }

    public ResUpdateJobDTO convertToResUpdateJobDTO(Job job) {
        ResUpdateJobDTO dto = new ResUpdateJobDTO();

        dto.setId(job.getId());
        dto.setName(job.getName());
        dto.setSalary(job.getSalary());
        dto.setQuantity(job.getQuantity());
        dto.setLocation(job.getLocation());
        dto.setLevel(job.getLevel());
        dto.setStartDate(job.getStartDate());
        dto.setEndDate(job.getEndDate());
        dto.setActive(job.isActive());
        dto.setUpdatedAt(job.getUpdatedAt());
        dto.setUpdatedBy(job.getUpdatedBy());

        if (job.getSkills() != null) {
            List<String> skills = job.getSkills()
                    .stream().map(skill -> skill.getName())
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }
        return dto;
    }

}
