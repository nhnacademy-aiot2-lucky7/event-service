package com.nhnacademy.adaptor.user;

import com.nhnacademy.adaptor.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "user-service", path = "/users")
public interface UserAdaptor {
    @GetMapping("/me")
    ResponseEntity<UserResponse> getMyInfo();
}
