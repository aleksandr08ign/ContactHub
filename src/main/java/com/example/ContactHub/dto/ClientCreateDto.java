package com.example.ContactHub.dto;

import lombok.Data;

@Data
public class ClientCreateDto {
    private String name;
    private String lastName;
    private Long contactId;
}
