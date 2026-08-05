package com.abdelrahman027.sbecom2.service;

import com.abdelrahman027.sbecom2.dto.AddressDTO;
import com.abdelrahman027.sbecom2.dto.AddressResponse;
import com.abdelrahman027.sbecom2.exception.ApiException;
import com.abdelrahman027.sbecom2.exception.ResourceNotFoundException;
import com.abdelrahman027.sbecom2.model.Address;
import com.abdelrahman027.sbecom2.model.Category;
import com.abdelrahman027.sbecom2.model.User;
import com.abdelrahman027.sbecom2.repository.AddressRepository;
import com.abdelrahman027.sbecom2.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService{
    private final ModelMapper modelMapper;
    private final AddressRepository addressRepository;
    private final AuthUtils authUtils;

    public AddressDTO createNewAddress(AddressDTO addressDTO) {
        User loggedUser = authUtils.loggedInUser();
        Address addedAddress = modelMapper.map(addressDTO,Address.class);
        addedAddress.setUser(loggedUser);
        Address savedAddress = addressRepository.save(addedAddress);
        return modelMapper.map(savedAddress,AddressDTO.class);
    }

    @Override
    public AddressResponse getAllAddress(Integer pageNumber,Integer pageSize ,String sortBy,String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() :Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Address> addressesPage = addressRepository.findAll(pageDetails);
        List<Address> addresses = addressesPage.getContent();
        List<AddressDTO> addressDTOS = addresses.stream().map(ad->modelMapper.map(ad,AddressDTO.class)).toList();
        AddressResponse addressResponse = new AddressResponse();
        addressResponse.setContent(addressDTOS);
        addressResponse.setPageNumber(addressesPage.getNumber());
        addressResponse.setPageSize(addressesPage.getSize());
        addressResponse.setTotalElements(addressesPage.getTotalElements());
        addressResponse.setTotalPages(addressesPage.getTotalPages());
        addressResponse.setLastPage(addressesPage.isLast());
        return addressResponse ;
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {

        Address address= addressRepository.findById(addressId).orElseThrow(()-> new ResourceNotFoundException("address",addressId,"address id"));
         return modelMapper.map(address,AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getUserAddresses() {
        User user = authUtils.loggedInUser();
        List<Address> addresses = addressRepository.findAddressByUser(user);
        if (addresses.isEmpty()) throw  new ApiException("no address for this user");
        return addresses.stream().map(ad->modelMapper.map(ad,AddressDTO.class)).toList();
    }

    @Override
    public AddressDTO updateAddressById(Long addressId, AddressDTO addressDTO) {
        Address address = addressRepository.findById(addressId).orElseThrow(()-> new ResourceNotFoundException("address",addressId,"address id"));
//        address.setCountry(addressDTO.getCountry());
//        address.setCity(addressDTO.getCity());
//        address.setStreet(addressDTO.getStreet());
//        address.setPincode(addressDTO.getPincode());
//        address.setPincode(addressDTO.getPincode());
//        address.setBuildingName(addressDTO.getBuildingName());
//        address.setState(addressDTO.getState());
        modelMapper.map(addressDTO,address);
        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress,AddressDTO.class);
    }

    @Override
    public String deleteAddress(Long addressId) {
        if (addressRepository.existsById(addressId)) {
            addressRepository.deleteById(addressId);
        } else throw new ApiException("address not found");

        return "deleted";
    }


}
