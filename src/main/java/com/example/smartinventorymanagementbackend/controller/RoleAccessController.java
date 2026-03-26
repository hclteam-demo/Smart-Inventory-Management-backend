package com.example.smartinventorymanagementbackend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/access")
public class RoleAccessController {

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "ADMIN access granted";
    }

    @GetMapping("/manager")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public String managerAccess() {
        return "MANAGER access granted";
    }

    @GetMapping("/staff")
    @PreAuthorize("hasAnyRole('STAFF','MANAGER','ADMIN')")
    public String staffAccess() {
        return "STAFF access granted";
    }
}
