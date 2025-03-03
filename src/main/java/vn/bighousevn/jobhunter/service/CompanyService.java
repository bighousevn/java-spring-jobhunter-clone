package vn.bighousevn.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.bighousevn.jobhunter.domain.Company;
import vn.bighousevn.jobhunter.domain.User;
import vn.bighousevn.jobhunter.domain.response.ResultPaginationDTO;
import vn.bighousevn.jobhunter.repository.CompanyRepository;
import vn.bighousevn.jobhunter.repository.UserRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO fetchAllCompanies(Specification<Company> spec, Pageable pageable) {
        Page<Company> pCompany = this.companyRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pCompany.getTotalPages());
        mt.setTotal(pCompany.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pCompany.getContent());
        return rs;
    }

    public Company updateCompanyById(Company company) {
        Optional<Company> OptionalCompany = this.companyRepository.findById(company.getId());

        if (OptionalCompany.isPresent()) {
            Company c = OptionalCompany.get();
            c.setName(company.getName());
            c.setAddress(company.getAddress());
            c.setDescription(company.getDescription());
            c.setLogo(company.getLogo());

            return this.companyRepository.save(c);
        }

        return null;
    }

    public void handleDeleteCompanyById(long id) {
        Optional<Company> optionalCom = this.findById(id);
        if (optionalCom.isPresent()) {
            Company company = optionalCom.get();
            List<User> users = this.userRepository.findByCompany(company);
            this.userRepository.deleteAll(users);
        }
        this.companyRepository.deleteById(id);
    }

    public Optional<Company> findById(long id) {
        return this.companyRepository.findById(id);
    }
}
