package com.karandashov.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovedRequest {

    private UUID newsId;

    private Status status;

    public enum Status {
        APPROVED, REJECTED
    }
}
