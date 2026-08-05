package com.abdelrahman027.sbecom2.repository;

import com.abdelrahman027.sbecom2.model.Address;
import com.abdelrahman027.sbecom2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAddressByUser(User user);
}
