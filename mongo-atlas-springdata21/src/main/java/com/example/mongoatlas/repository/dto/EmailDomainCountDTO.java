package com.example.mongoatlas.repository.dto;

public class EmailDomainCountDTO {

    private String domain;
    private Long total;

    public EmailDomainCountDTO() {
    }

    public EmailDomainCountDTO(String domain, Long total) {
        this.domain = domain;
        this.total = total;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}