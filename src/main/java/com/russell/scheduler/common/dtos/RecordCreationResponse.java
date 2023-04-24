package com.russell.scheduler.common.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecordCreationResponse {
    String id;

    public RecordCreationResponse(String id) {
        this.id = id;
    }
}
