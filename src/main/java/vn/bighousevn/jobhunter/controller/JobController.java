package vn.bighousevn.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.bighousevn.jobhunter.domain.Job;
import vn.bighousevn.jobhunter.domain.response.ResultPaginationDTO;
import vn.bighousevn.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.bighousevn.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.bighousevn.jobhunter.service.JobService;
import vn.bighousevn.jobhunter.util.annotation.ApiMessage;
import vn.bighousevn.jobhunter.util.error.IdInvalidException;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/jobs")
    @ApiMessage("Fetch all jobs")
    public ResponseEntity<ResultPaginationDTO> handleGetAllJobs(
            @Filter Specification<Job> spec, Pageable pageable) {

        ResultPaginationDTO jobList = this.jobService.fetchAllJobs(spec, pageable);
        return ResponseEntity.ok(jobList);
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<Job> handleGetJobById(@PathVariable(name = "id") long id) throws IdInvalidException {

        Optional<Job> optionalJob = this.jobService.fetchJobById(id);
        if (optionalJob.isPresent()) {
            throw new IdInvalidException("Id khong ton tai");
        }
        return ResponseEntity.ok().body(optionalJob.get());

    }

    @PostMapping("/jobs")
    @ApiMessage("Create a job")
    public ResponseEntity<ResCreateJobDTO> handleCreateJob(@Valid @RequestBody Job job) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.jobService.convertToResCreateJobDTO(this.jobService.handleCreateJob(job)));
    }

    @PutMapping("/jobs/{id}")
    @ApiMessage("Update a job")
    public ResponseEntity<ResUpdateJobDTO> handleUpdateJob(@Valid @RequestBody Job job) throws IdInvalidException {
        Optional<Job> optionalJob = this.jobService.fetchJobById(job.getId());
        if (optionalJob.isPresent()) {
            throw new IdInvalidException("Id khong ton tai");
        }
        return ResponseEntity.ok()
                .body(this.jobService.convertToResUpdateJobDTO(this.jobService.handleUpdateJob(optionalJob.get())));
    }

    @DeleteMapping("/jobs")
    @ApiMessage("Delete a job")
    public ResponseEntity<Void> handleDeleteJob(@PathVariable(name = "id") long id) throws IdInvalidException {
        Optional<Job> optionalJob = this.jobService.fetchJobById(id);
        if (optionalJob.isPresent()) {
            throw new IdInvalidException("Id khong ton tai");
        }
        this.jobService.handleDeleteJob(id);
        return ResponseEntity.ok().body(null);
    }

}
