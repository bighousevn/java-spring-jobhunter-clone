package vn.bighousevn.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.bighousevn.jobhunter.domain.Permission;
import vn.bighousevn.jobhunter.domain.Role;
import vn.bighousevn.jobhunter.domain.response.ResultPaginationDTO;
import vn.bighousevn.jobhunter.repository.RoleRepository;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public boolean existByName(String name) {
        return this.roleRepository.existsByName(name);
    }

    public Role handleCreateRole(Role r) {

        if (r.getPermissions() != null) {
            List<Long> reqPermissions = r.getPermissions()
                    .stream().map(item -> item.getId()).collect(Collectors.toList());

            List<Permission> pDBList = this.roleRepository.findByIdIn(reqPermissions);
            r.setPermissions(pDBList);

        }
        return this.roleRepository.save(r);

    }

    public Role fetchById(long id) {
        Optional<Role> r = this.roleRepository.findById(id);
        if (r.isPresent())
            return r.get();
        return null;
    }

    public Role handleUpdateRole(Role r) {
        Role rDB = fetchById(r.getId());

        if (r.getPermissions() != null) {
            List<Long> reqPermisisons = r.getPermissions()
                    .stream().map(item -> item.getId()).collect(Collectors.toList());

            List<Permission> pDBList = this.roleRepository.findByIdIn(reqPermisisons);
            rDB.setPermissions(pDBList);
        }

        rDB.setName(r.getName());
        rDB.setDescription(r.getDescription());
        rDB.setActive(r.isActive());
        rDB.setPermissions(r.getPermissions());
        return this.roleRepository.save(rDB);

    }

    public void handleDeleteRole(long id) {
        this.roleRepository.deleteById(id);
    }

    public ResultPaginationDTO fetchAllRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> pageRole = this.roleRepository.findAll(spec, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageRole.getTotalPages());
        meta.setTotal(pageRole.getTotalElements());

        res.setMeta(meta);

        res.setResult(pageRole.getContent());
        return res;
    }

}
