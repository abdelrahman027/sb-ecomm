package com.abdelrahman027.sbecom2.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {


    private Long addressId;


    @NotBlank
    @Size(min = 5,message = "street name must be at least 5 chars")
    private String street;

    @NotBlank
    @Size(min = 5,message = "building name must be at least 5 chars")
    private String buildingName;

    @NotBlank
    @Size(min = 2,message = "city name must be at least 3 chars")
    private String city;

    @NotBlank
    @Size(min = 2,message = "state name must be at least 3 chars")
    private String state;

    @NotBlank
    @Size(min = 3,message = "country name must be at least 3 chars")
    private String country;

    @NotBlank
    @Size(min = 3,message = "pincode name must be at least 3 chars")
    private String pincode;
}
