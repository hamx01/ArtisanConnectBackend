package _11.asktpk.artisanconnectbackend.controller;

import _11.asktpk.artisanconnectbackend.dto.*;
import _11.asktpk.artisanconnectbackend.entities.Client;
import _11.asktpk.artisanconnectbackend.entities.Order;
import _11.asktpk.artisanconnectbackend.entities.Payment;
import _11.asktpk.artisanconnectbackend.service.OrderService;
import _11.asktpk.artisanconnectbackend.service.PaymentService;
import _11.asktpk.artisanconnectbackend.utils.Enums;
import _11.asktpk.artisanconnectbackend.utils.Tools;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final Tools tools;

    public OrderController(OrderService orderService, PaymentService paymentService, Tools tools) {
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.tools = tools;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addClient(@RequestBody OrderDTO orderDTO, HttpServletRequest request) {
        orderDTO.setClientId(tools.getClientIdFromRequest(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.addOrder(orderDTO));
    }

    @PutMapping("/changeStatus")
    public ResponseEntity<?> changeStatus(@RequestBody OrderStatusDTO orderStatusDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.changeOrderStatus(orderStatusDTO.getId(), orderStatusDTO.getStatus()));
    }

    @PostMapping("/token")
    public ResponseEntity<?> fetchToken(HttpServletRequest request,@RequestParam Long orderId) {
        Order order = orderService.getOrderById(orderId);
        Long clientId = tools.getClientIdFromRequest(request);
        Client client = order.getClient();
        OAuthPaymentResponseDTO authPaymentDTO = paymentService.getOAuthToken();
        TransactionPaymentRequestDTO.Payer payer = new TransactionPaymentRequestDTO.Payer(
                client.getEmail(), client.getFirstName()+' '+client.getLastName());

        String paymentDescription = order.getOrderType() == Enums.OrderType.ACTIVATION ? "Aktywacja ogłoszenia" : "Podbicie ogłoszenia";
        paymentDescription += order.getNotice().getTitle();
        TransactionPaymentRequestDTO paymentRequest = new TransactionPaymentRequestDTO(
                order.getAmount(), paymentDescription, payer);

        String response = paymentService.createTransaction(order, authPaymentDTO.getAccess_token(), paymentRequest);
        System.out.println(response);
        System.out.println(request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/get/all")
    public ResponseEntity<List<OrderWithPaymentsDTO>> getOrders(HttpServletRequest request) {
        Long clientId = tools.getClientIdFromRequest(request);
        List<Order> orders = orderService.getOrdersByClientId(clientId);

        List<OrderWithPaymentsDTO> dtoList = orders.stream().map(order -> {
            OrderWithPaymentsDTO dto = new OrderWithPaymentsDTO();
            dto.setOrderId(order.getId());
            dto.setOrderType(order.getOrderType().name());
            dto.setStatus(order.getStatus().name());
            dto.setAmount(order.getAmount());
            dto.setCreatedAt(order.getCreatedAt());

            List<Payment> payments = paymentService.getPaymentsByOrderId(order.getId());

            List<PaymentDTO> paymentDTOs = payments.stream().map(payment -> {
                PaymentDTO pDto = new PaymentDTO();
                pDto.setPaymentId(payment.getIdPayment());
                pDto.setAmount(payment.getAmount());
                pDto.setStatus(payment.getStatus().name());
                pDto.setTransactionPaymentUrl(payment.getTransactionPaymentUrl());
                pDto.setTransactionId(payment.getTransactionId());
                return pDto;
            }).toList();

            dto.setPayments(paymentDTOs);
            return dto;
        }).toList();

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/get/{orderId}")
    public ResponseEntity<OrderWithPaymentsDTO> getOrderById(HttpServletRequest request,
                                                             @PathVariable Long orderId) {
        Long clientId = tools.getClientIdFromRequest(request);

        Order order = orderService.getOrderById(orderId);

        if (!order.getClient().getId().equals(clientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // lub UNAUTHORIZED
        }

        OrderWithPaymentsDTO dto = new OrderWithPaymentsDTO();
        dto.setOrderId(order.getId());
        dto.setOrderType(order.getOrderType().name());
        dto.setStatus(order.getStatus().name());
        dto.setAmount(order.getAmount());
        dto.setCreatedAt(order.getCreatedAt());

        List<Payment> payments = paymentService.getPaymentsByOrderId(order.getId());
        List<PaymentDTO> paymentDTOs = payments.stream().map(payment -> {
            PaymentDTO pDto = new PaymentDTO();
            pDto.setPaymentId(payment.getIdPayment());
            pDto.setAmount(payment.getAmount());
            pDto.setStatus(payment.getStatus().name());
            pDto.setTransactionPaymentUrl(payment.getTransactionPaymentUrl());
            pDto.setTransactionId(payment.getTransactionId());
            return pDto;
        }).toList();

        dto.setPayments(paymentDTOs);

        return ResponseEntity.ok(dto);
    }


}
