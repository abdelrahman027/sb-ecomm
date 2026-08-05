package com.abdelrahman027.sbecom2.service;

import com.abdelrahman027.sbecom2.dto.AddressDTO;
import com.abdelrahman027.sbecom2.dto.AddressResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AddressService {
    AddressDTO createNewAddress(AddressDTO addressDTO);

    AddressResponse getAllAddress(Integer pageNumber,Integer pageSize ,String sortBy,String sortOrder);

    AddressDTO getAddressById(Long addressId);

    List<AddressDTO> getUserAddresses();

    AddressDTO updateAddressById(Long addressId,AddressDTO addressDTO);

    String deleteAddress(Long addressId);
}
