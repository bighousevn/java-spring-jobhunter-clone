package vn.bighousevn.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;

import jakarta.validation.Valid;
import vn.bighousevn.jobhunter.domain.Company;
import vn.bighousevn.jobhunter.domain.Job;
import vn.bighousevn.jobhunter.domain.Resume;
import vn.bighousevn.jobhunter.domain.User;
import vn.bighousevn.jobhunter.domain.response.ResultPaginationDTO;
import vn.bighousevn.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.bighousevn.jobhunter.domain.response.resume.ResFetchResumeDTO;
import vn.bighousevn.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.bighousevn.jobhunter.service.ResumeService;
import vn.bighousevn.jobhunter.service.UserService;
import vn.bighousevn.jobhunter.util.SecurityUtil;
import vn.bighousevn.jobhunter.util.annotation.ApiMessage;
import vn.bighousevn.jobhunter.util.error.IdInvalidException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {

    private final ResumeService resumeService;
    private final UserService userService;
    private final FilterSpecificationConverter filterSpecificationConverter;
    private final FilterBuilder filterBuilder;

    public ResumeController(ResumeService resumeService,
            UserService userService,
            FilterSpecificationConverter filterSpecificationConverter,
            FilterBuilder filterBuilder) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.filterSpecificationConverter = filterSpecificationConverter;
        this.filterBuilder = filterBuilder;
    }

    @PostMapping("/resumes")
    @ApiMessage("Create a resume")
    public ResponseEntity<ResCreateResumeDTO> handleCreateResume(@Valid @RequestBody Resume resume)
            throws IdInvalidException {

        if (this.resumeService.checkResumeExistByUserAndJob(resume) == false)
            throw new IdInvalidException("User id/Job id không tồn tại");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.resumeService.convertToResCreateResumeDTO(this.resumeService.handleCreateResume(resume)));
    }

    @PutMapping("/resumes")
    @ApiMessage("Update a resume")
    public ResponseEntity<ResUpdateResumeDTO> handleUpdateResume(@RequestBody Resume resume)
            throws IdInvalidException {

        Optional<Resume> optionalResume = this.resumeService.fetchById(resume.getId());
        if (!optionalResume.isPresent()) {
            throw new IdInvalidException("Resume với id = " + resume.getId() + " không tồn tại");
        }

        Resume resumeUpdate = optionalResume.get();
        resumeUpdate.setStatus(resume.getStatus());

        return ResponseEntity.ok().body(
                this.resumeService.convertToResUpdateResumeDTO(this.resumeService.handleUpdateResume(resumeUpdate)));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete a resume")
    public ResponseEntity<Void> handleDeleteResume(@PathVariable(name = "id") long id) throws IdInvalidException {

        Optional<Resume> optionalResume = this.resumeService.fetchById(id);
        if (!optionalResume.isPresent()) {
            throw new IdInvalidException("Resume với id = " + id + " không tồn tại");
        }

        this.resumeService.handleDeleteResume(id);
        return null;
    }

    @GetMapping("/resumes")
    @ApiMessage("Get all resume")
    public ResponseEntity<ResultPaginationDTO> getAllResumes(
            @Filter Specification<Resume> spec, Pageable pageable) {

        List<Long> jobId = null;

        // get email
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        // get user
        User user = this.userService.handleFindUserByUsername(email);
        if (user != null) {
            // get user company
            Company com = user.getCompany();
            if (com != null) {
                // get company jobs
                List<Job> comJobs = com.getJobs();
                if (comJobs != null && comJobs.size() > 0) {
                    jobId = comJobs.stream().map(item -> item.getId()).collect(Collectors.toList());
                }
            }
        }

        Specification<Resume> jobInSpec = filterSpecificationConverter.convert(filterBuilder.field("job")
                .in(filterBuilder.input(jobId)).get());

        Specification<Resume> finalSpec = jobInSpec.and(spec);

        return ResponseEntity.ok()
                .body(this.resumeService.fetchAllResume(finalSpec, pageable));
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Get a resume")
    public ResponseEntity<ResFetchResumeDTO> fetchResumeById(
            @PathVariable(name = "id") long id) throws IdInvalidException {

        Optional<Resume> optionalResume = this.resumeService.fetchById(id);
        if (!optionalResume.isPresent()) {
            throw new IdInvalidException("Resume với id = " + id + " không tồn tại");
        }

        return ResponseEntity.ok().body(this.resumeService.convertToResFetchResumeDTO(optionalResume.get()));
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("Get list resumes by user")
    public ResponseEntity<ResultPaginationDTO> fetchResumeByUser(Pageable pageable) {

        return ResponseEntity.ok().body(this.resumeService.fetchResumeByUser(pageable));
    }

}
