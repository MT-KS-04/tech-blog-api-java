package com.mtks04.tech_blog_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordChangeFormDto {

    @NotBlank(message = "Nhập mật khẩu hiện tại")
    private String currentPassword;

    @NotBlank(message = "Nhập mật khẩu mới")
    @Size(min = 6, message = "Mật khẩu mới ít nhất 6 ký tự")
    private String newPassword;

    @NotBlank(message = "Xác nhận mật khẩu mới")
    private String confirmPassword;
}
