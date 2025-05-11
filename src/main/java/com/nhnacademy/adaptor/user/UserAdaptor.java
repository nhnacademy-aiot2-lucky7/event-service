package com.nhnacademy.adaptor.user;

import com.nhnacademy.adaptor.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserAdaptor {
    @GetMapping("/users/me")
    UserResponse getMyInfo();

    @GetMapping("/admin/users/departments/{departmentId}")
    List<UserResponse> findUsersByDepartmentId(@PathVariable String departmentId);
}
