package org.example.zupaybackend.dto;

import org.example.zupaybackend.model.BillType;

public class PayBillRequest {

    private String userId;
    private String reference;
    private BillType type;
    private String providerName;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public BillType getType() { return type; }
    public void setType(BillType type) { this.type = type; }
    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }
}