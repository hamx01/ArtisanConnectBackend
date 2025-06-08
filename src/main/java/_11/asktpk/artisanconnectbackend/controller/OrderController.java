package _11.asktpk.artisanconnectbackend.controller;

import _11.asktpk.artisanconnectbackend.dto.*;
import _11.asktpk.artisanconnectbackend.entities.Order;
import _11.asktpk.artisanconnectbackend.service.OrderService;
import _11.asktpk.artisanconnectbackend.service.PaymentService;
import _11.asktpk.artisanconnectbackend.utils.Enums;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static _11.asktpk.artisanconnectbackend.utils.Tools.getClientIdFromRequest;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    public OrderController(OrderService orderService, PaymentService paymentService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addClient(@RequestBody OrderDTO orderDTO, HttpServletRequest request) {
        orderDTO.setClientId(getClientIdFromRequest(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.addOrder(orderDTO));
    }

    @PutMapping("/changeStatus")
    public ResponseEntity<?> changeStatus(@RequestBody OrderStatusDTO orderStatusDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.changeOrderStatus(orderStatusDTO.getId(), orderStatusDTO.getStatus()));
    }

    @PostMapping("/token")
    public ResponseEntity<?> fetchToken() {
        Order order = orderService.getOrderById(1L);
        OAuthPaymentResponseDTO authPaymentDTO = paymentService.getOAuthToken();
        TransactionPaymentRequestDTO.Payer payer = new TransactionPaymentRequestDTO.Payer(
                "patryk@test.pl", "Patryk Test");

        String paymentDescription = order.getOrderType() == Enums.OrderType.ACTIVATION ? "Aktywacja ogłoszenia" : "Podbicie ogłoszenia";
        paymentDescription += order.getNotice().getTitle();
        TransactionPaymentRequestDTO request = new TransactionPaymentRequestDTO(
                order.getAmount(), paymentDescription, payer);

        String response = paymentService.createTransaction(order, authPaymentDTO.getAccess_token(), request);
        System.out.println(response);
        System.out.println(request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
