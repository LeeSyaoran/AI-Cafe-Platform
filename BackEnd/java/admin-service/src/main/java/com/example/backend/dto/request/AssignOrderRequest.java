package com.example.backend.dto.request;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;

import lombok.Data;

import java.util.UUID;

@Data
public class AssignOrderRequest {
    private UUID staffId;
    private UUID cafeId;
}
