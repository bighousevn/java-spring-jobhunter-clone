package vn.bighousevn.jobhunter.repository;

import vn.bighousevn.jobhunter.domain.Company;
import vn.bighousevn.jobhunter.domain.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    User findByEmail(String email);

    boolean existsByEmail(String email);

    User findByRefreshTokenAndEmail(String refresh_token, String email);

    List<User> findByCompany(Company company);
}
