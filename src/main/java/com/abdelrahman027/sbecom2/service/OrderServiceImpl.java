package com.abdelrahman027.sbecom2.service;

import com.abdelrahman027.sbecom2.dto.OrderDTO;
import com.abdelrahman027.sbecom2.dto.OrderItemDTO;
import com.abdelrahman027.sbecom2.dto.OrderRequestDTO;
import com.abdelrahman027.sbecom2.dto.ProductDTO;
import com.abdelrahman027.sbecom2.exception.ApiException;
import com.abdelrahman027.sbecom2.exception.ResourceNotFoundException;
import com.abdelrahman027.sbecom2.model.*;
import com.abdelrahman027.sbecom2.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderDTO placeOrder(String emailId, OrderRequestDTO orderRequestDTO, String paymentMethod) {

        Cart cart = cartRepository.findCartByUserEmail(emailId);
        if (cart == null ) throw new ApiException("user cart not found");

        Address address = addressRepository.findById(orderRequestDTO.getAddressId()).orElseThrow(()->new ResourceNotFoundException("address",orderRequestDTO.getAddressId(),"address id"));
        Order order = new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDate.now());
        order.setOrderAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted!");
        order.setAddress(address);


        Payment payment = new Payment(paymentMethod,orderRequestDTO.getPgPaymentId(),orderRequestDTO.getPgStatus(),orderRequestDTO.getPgResponseMessage(),orderRequestDTO.getPgName());
        payment.setOrder(order);

        payment = paymentRepository.save(payment);
        order.setPayment(payment);

       Order savedOrder = orderRepository.save(order);
        List<CartItem> cartItems  = cart.getCartItems();
        if (cartItems.isEmpty()) throw new ApiException("cart is empty");

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            int quantity = cartItem.getQuantity();
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setProduct(cartItem.getProduct());
            orderItems.add(orderItem);
            Product product = cartItem.getProduct();
            product.setQuantity(product.getQuantity()-quantity);
            productRepository.save(product);
            cartService.deleteProductFromCart(cart.getCartId(),product.getProductId());
        }
        orderItemRepository.saveAll(orderItems);


        OrderDTO orderDTO = modelMapper.map(savedOrder,OrderDTO.class);
        List<OrderItemDTO> orderItemDTOS = new ArrayList<>();
        for (OrderItem orderItem:orderItems) {
            ProductDTO productDTO = modelMapper.map(orderItem.getProduct(),ProductDTO.class);
            OrderItemDTO orderItemDTO =modelMapper.map(orderItem,OrderItemDTO.class);
            orderItemDTO.setProductDTO(productDTO);
            orderItemDTOS.add(orderItemDTO);

        }

        orderItems.forEach(item->orderDTO.getOrderItems().add(modelMapper.map(item, OrderItemDTO.class)));
        orderDTO.setAddressId(address.getAddressId());
        orderDTO.setOrderItems(orderItemDTOS);
        orderDTO.setTotalAmount(order.getOrderAmount());
        return orderDTO;
    }
}
