package com.example.backend.dto.request;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;

import lombok.Data;

@Data
public class UpdateProductRequest {

    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String imageUrl;
    private String status;
    private Boolean isFeatured;
    private Boolean isBestSeller;
    private Integer sortOrder;
}
