package com.example.backend.dto.request;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCategoryRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String icon;
    private String color;
    private Integer sortOrder;
}
