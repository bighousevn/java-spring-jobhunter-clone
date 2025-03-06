package vn.bighousevn.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.bighousevn.jobhunter.domain.Permission;
import vn.bighousevn.jobhunter.domain.response.ResultPaginationDTO;
import vn.bighousevn.jobhunter.repository.PermissionRepository;
import vn.bighousevn.jobhunter.repository.RoleRepository;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository, RoleRepository roleRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean isPermissionExist(Permission p) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(
                p.getModule(), p.getApiPath(), p.getMethod());
    }

    public Permission handleCreatePermission(Permission p) {
        return this.permissionRepository.save(p);
    }

    public Permission fetchById(long id) {
        Optional<Permission> p = this.permissionRepository.findById(id);
        if (p.isPresent())
            return p.get();

        return null;
    }

    public boolean isSameName(Permission p) {
        Permission permissionDB = this.fetchById(p.getId());
        if (permissionDB != null) {
            if (permissionDB.getName().equals(p.getName()))
                return true;
        }
        return false;
    }

    public Permission handleUpdatePermission(Permission p) {
        Permission pDB = fetchById(p.getId());

        if (pDB != null) {
            pDB.setModule(p.getModule());
            pDB.setApiPath(p.getApiPath());
            pDB.setMethod(p.getMethod());
            return this.permissionRepository.save(pDB);
        }
        return null;
    }

    public void handleDeletePermission(long id) {
        Permission p = fetchById(id);
        p.getRoles().forEach(item -> item.getPermissions().remove(p));

        this.permissionRepository.delete(p);
    }

    public ResultPaginationDTO fetchAllPermissions(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> pagePer = this.permissionRepository.findAll(spec, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pagePer.getTotalPages());
        meta.setTotal(pagePer.getTotalElements());

        res.setMeta(meta);

        res.setResult(pagePer.getContent());
        return res;
    }
}
