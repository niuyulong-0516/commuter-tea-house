package com.commutertea.user.dto;

import jakarta.validation.constraints.Pattern;

public class UpdateTransportTypeRequest {

    @Pattern(regexp = "RAIL|BUS", message = "出行类型不正确")
    private String transportType;

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }
}
