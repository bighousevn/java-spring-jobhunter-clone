package vn.bighousevn.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.bighousevn.jobhunter.domain.Role;
import vn.bighousevn.jobhunter.domain.response.ResultPaginationDTO;
import vn.bighousevn.jobhunter.service.RoleService;
import vn.bighousevn.jobhunter.util.annotation.ApiMessage;
import vn.bighousevn.jobhunter.util.error.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("api/v1")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<Role> handleCreateRole(@RequestBody Role role) throws IdInvalidException {

        if (this.roleService.existByName(role.getName()))
            throw new IdInvalidException("Role với name = " + role.getName() + " đã tồn tại");

        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.handleCreateRole(role));
    }

    @PutMapping("/roles")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> handleUpdateRole(@RequestBody Role role) throws IdInvalidException {

        // check id
        if (this.roleService.fetchById(role.getId()) == null)
            throw new IdInvalidException("Role với id = " + role.getId() + " không tồn tại");
        // check name
        // if (this.roleService.existByName(role.getName()))
        // throw new IdInvalidException("Role với name = " + role.getName() + " đã tồn
        // tại");

        return ResponseEntity.ok().body(this.roleService.handleUpdateRole(role));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> handleDeleteRole(@PathVariable(name = "id") long id) throws IdInvalidException {

        if (this.roleService.fetchById(id) == null)
            throw new IdInvalidException("Role với id = " + id + " không tồn tại");

        this.roleService.handleDeleteRole(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/roles")
    @ApiMessage("Get all roles")
    public ResponseEntity<ResultPaginationDTO> fetchAllRoles(
            @Filter Specification<Role> spec, Pageable pageable) {

        return ResponseEntity.ok().body(this.roleService.fetchAllRoles(spec, pageable));
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Fetch role by id")
    public ResponseEntity<Role> getById(@PathVariable("id") long id) throws IdInvalidException {

        Role role = this.roleService.fetchById(id);
        if (role == null) {
            throw new IdInvalidException("Resume với id = " + id + " không tồn tại");
        }

        return ResponseEntity.ok().body(role);
    }

}
