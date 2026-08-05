package com.abdelrahman027.sbecom2.controller;

import com.abdelrahman027.sbecom2.config.AppConstants;
import com.abdelrahman027.sbecom2.dto.AddressDTO;
import com.abdelrahman027.sbecom2.dto.AddressResponse;
import com.abdelrahman027.sbecom2.model.Address;
import com.abdelrahman027.sbecom2.model.User;
import com.abdelrahman027.sbecom2.repository.AddressRepository;
import com.abdelrahman027.sbecom2.service.AddressService;
import com.abdelrahman027.sbecom2.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AddressController {


    private final AddressService addressService;


    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@RequestBody AddressDTO addressDTO) {
       return new ResponseEntity<>(addressService.createNewAddress(addressDTO), HttpStatus.OK);
    }


    @GetMapping("/addresses")
    public ResponseEntity<AddressResponse> getAllAddresses(
            @RequestParam(name = "pageNumber" ,defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name="pageSize",defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(value = "sortBy",defaultValue = AppConstants.ADDRESS_SORT_BY) String sortBy,
            @RequestParam(value = "sortOrder",defaultValue = AppConstants.SORT_ORDER) String sortOrder
    ) {


        return new ResponseEntity<AddressResponse>(addressService.getAllAddress(pageNumber,pageSize,sortBy,sortOrder),HttpStatus.OK);
    }


    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId) {
        return new ResponseEntity<AddressDTO>(addressService.getAddressById(addressId),HttpStatus.OK);
    }

    @GetMapping("/users/addresses")
    public ResponseEntity<List<AddressDTO>> getAddressById() {
        return new ResponseEntity<List<AddressDTO>>(addressService.getUserAddresses(),HttpStatus.OK);
    }


    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddressById(@PathVariable Long addressId,@RequestBody AddressDTO addressDTO) {
        return new ResponseEntity<AddressDTO>(addressService.updateAddressById(addressId,addressDTO),HttpStatus.OK);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> updateAddressById(@PathVariable Long addressId) {
        return new ResponseEntity<String>(addressService.deleteAddress(addressId),HttpStatus.OK);
    }

}
