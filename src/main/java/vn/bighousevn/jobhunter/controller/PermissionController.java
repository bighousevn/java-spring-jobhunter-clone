package vn.bighousevn.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.bighousevn.jobhunter.domain.Permission;
import vn.bighousevn.jobhunter.domain.response.ResultPaginationDTO;
import vn.bighousevn.jobhunter.service.PermissionService;
import vn.bighousevn.jobhunter.util.annotation.ApiMessage;
import vn.bighousevn.jobhunter.util.error.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create a permission")
    public ResponseEntity<Permission> handleCreatePermission(@Valid @RequestBody Permission p)
            throws IdInvalidException {

        if (this.permissionService.isPermissionExist(p))
            throw new IdInvalidException("Permission đã tồn tại.");

        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.handleCreatePermission(p));
    }

    @PutMapping("/permissions")
    @ApiMessage("Update a permission")
    public ResponseEntity<Permission> handleUpdatePermission(@Valid @RequestBody Permission p)
            throws IdInvalidException {

        // check exist by id
        if (this.permissionService.fetchById(p.getId()) == null) {
            throw new IdInvalidException("Permission với id = " + p.getId() + " không tồn tại.");
        }

        // check exist by module, apiPath and method
        if (this.permissionService.isPermissionExist(p))
            if (this.permissionService.isSameName(p))
                throw new IdInvalidException("Permission đã tồn tại.");

        return ResponseEntity.ok().body(this.permissionService.handleUpdatePermission(p));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete a permission")
    public ResponseEntity<Void> handleDeletePermission(@PathVariable(name = "id") long id) throws IdInvalidException {

        if (this.permissionService.fetchById(id) == null)
            throw new IdInvalidException("Permission với id = " + id + " không tồn tại.");

        this.permissionService.handleDeletePermission(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/permissions")
    @ApiMessage("Fetch all permissions")
    public ResponseEntity<ResultPaginationDTO> fetchAllPermissions(
            @Filter Specification<Permission> spec, Pageable pageable) {

        return ResponseEntity.ok().body(this.permissionService.fetchAllPermissions(spec, pageable));
    }

}
