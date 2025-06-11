package _11.asktpk.artisanconnectbackend;

import _11.asktpk.artisanconnectbackend.controller.OrderController;
import _11.asktpk.artisanconnectbackend.dto.*;
import _11.asktpk.artisanconnectbackend.entities.Client;
import _11.asktpk.artisanconnectbackend.entities.Notice;
import _11.asktpk.artisanconnectbackend.entities.Order;
import _11.asktpk.artisanconnectbackend.entities.Payment;
import _11.asktpk.artisanconnectbackend.service.OrderService;
import _11.asktpk.artisanconnectbackend.service.PaymentService;
import _11.asktpk.artisanconnectbackend.utils.Enums;
import _11.asktpk.artisanconnectbackend.utils.Tools;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderControllerTest {

    private final OrderService orderService = Mockito.mock(OrderService.class);
    private final PaymentService paymentService = Mockito.mock(PaymentService.class);
    private final Tools tools = Mockito.mock(Tools.class);
    private final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private OrderController orderController;

    @BeforeEach
    public void setUp() {
        orderController = new OrderController(orderService, paymentService, tools);
    }

    @Test
    @DisplayName("Test dodawania zamówienia")
    public void testAddOrder() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setClientId(1L);
        orderDTO.setNoticeId(1L);
        orderDTO.setOrderType(Enums.OrderType.ACTIVATION);

        when(tools.getClientIdFromRequest(request)).thenReturn(1L);
        when(orderService.addOrder(orderDTO)).thenReturn(1L);

        ResponseEntity<?> response = orderController.addClient(orderDTO, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Status odpowiedzi powinien być 201 CREATED");
        assertEquals(1L, response.getBody(), "Ciało odpowiedzi powinno zawierać ID zamówienia");

        System.out.println("Test dodawania zamówienia przeszedł pomyślnie.");
    }

    @Test
    @DisplayName("Test zmiany statusu zamówienia")
    public void testChangeStatus() {
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setId(1L);
        orderStatusDTO.setStatus(Enums.OrderStatus.COMPLETED);

        when(orderService.changeOrderStatus(1L, Enums.OrderStatus.COMPLETED)).thenReturn(1L);

        ResponseEntity<?> response = orderController.changeStatus(orderStatusDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status odpowiedzi powinien być 200 OK");
        assertEquals(1L, response.getBody(), "Ciało odpowiedzi powinno zawierać ID zamówienia");

        System.out.println("Test zmiany statusu zamówienia przeszedł pomyślnie.");
    }

    @Test
    @DisplayName("Test pobierania tokena płatności")
    public void testFetchToken() {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setAmount(10.00);
        order.setOrderType(Enums.OrderType.ACTIVATION);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("test@example.com");
        client.setFirstName("Jan");
        client.setLastName("Kowalski");

        Notice notice = new Notice();
        notice.setTitle("Test Notice");

        order.setClient(client);
        order.setNotice(notice);

        OAuthPaymentResponseDTO oAuthResponse = new OAuthPaymentResponseDTO();
        oAuthResponse.setAccess_token("testAccessToken");

        when(orderService.getOrderById(orderId)).thenReturn(order);
        when(paymentService.getOAuthToken()).thenReturn(oAuthResponse);
        when(paymentService.createTransaction(eq(order), eq("testAccessToken"), any(TransactionPaymentRequestDTO.class)))
                .thenReturn("http://payment.url");

        ResponseEntity<?> response = orderController.fetchToken(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status odpowiedzi powinien być 200 OK");
        assertEquals("http://payment.url", response.getBody(), "Ciało odpowiedzi powinno zawierać URL płatności");

        System.out.println("Test pobierania tokena płatności przeszedł pomyślnie.");
    }

    @Test
    @DisplayName("Test pobierania wszystkich zamówień")
    public void testGetAllOrders() {
        Long clientId = 1L;
        Order order1 = new Order();
        order1.setId(1L);
        order1.setOrderType(Enums.OrderType.ACTIVATION);
        order1.setStatus(Enums.OrderStatus.PENDING);
        order1.setAmount(10.00);
        order1.setCreatedAt(LocalDateTime.now());

        Order order2 = new Order();
        order2.setId(2L);
        order2.setOrderType(Enums.OrderType.BOOST);
        order2.setStatus(Enums.OrderStatus.COMPLETED);
        order2.setAmount(8.00);
        order2.setCreatedAt(LocalDateTime.now());

        List<Order> orders = List.of(order1, order2);

        Payment payment1 = new Payment();
        payment1.setIdPayment(1L);
        payment1.setAmount(10.00);
        payment1.setStatus(Enums.PaymentStatus.PENDING);
        payment1.setTransactionPaymentUrl("http://payment.url/1");
        payment1.setTransactionId("trans1");

        Payment payment2 = new Payment();
        payment2.setIdPayment(2L);
        payment2.setAmount(8.00);
        payment2.setStatus(Enums.PaymentStatus.CORRECT);
        payment2.setTransactionPaymentUrl("http://payment.url/2");
        payment2.setTransactionId("trans2");

        when(tools.getClientIdFromRequest(request)).thenReturn(clientId);
        when(orderService.getOrdersByClientId(clientId)).thenReturn(orders);
        when(paymentService.getPaymentsByOrderId(1L)).thenReturn(List.of(payment1));
        when(paymentService.getPaymentsByOrderId(2L)).thenReturn(List.of(payment2));

        ResponseEntity<List<OrderWithPaymentsDTO>> response = orderController.getOrders(request);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status odpowiedzi powinien być 200 OK");
        List<OrderWithPaymentsDTO> dtoList = response.getBody();
        assertNotNull(dtoList, "Lista DTO nie powinna być null");
        assertEquals(2, dtoList.size(), "Lista DTO powinna zawierać 2 elementy");

        OrderWithPaymentsDTO dto1 = dtoList.getFirst();
        assertEquals(1L, dto1.getOrderId(), "ID zamówienia w DTO powinno być 1");
        assertEquals("ACTIVATION", dto1.getOrderType(), "Typ zamówienia w DTO powinien być ACTIVATION");
        assertEquals("PENDING", dto1.getStatus(), "Status zamówienia w DTO powinien być PENDING");
        assertEquals(10.00, dto1.getAmount(), "Kwota zamówienia w DTO powinna być 10.00");
        assertEquals(1, dto1.getPayments().size(), "Liczba płatności w DTO powinna być 1");

        System.out.println("Test pobierania wszystkich zamówień przeszedł pomyślnie.");
    }

    @Test
    @DisplayName("Test pobierania zamówienia po ID")
    public void testGetOrderById() {
        Long clientId = 1L;
        Long orderId = 1L;

        Order order = new Order();
        order.setId(orderId);
        order.setOrderType(Enums.OrderType.ACTIVATION);
        order.setStatus(Enums.OrderStatus.PENDING);
        order.setAmount(10.00);
        order.setCreatedAt(LocalDateTime.now());

        Client client = new Client();
        client.setId(clientId);
        order.setClient(client);

        Payment payment = new Payment();
        payment.setIdPayment(1L);
        payment.setAmount(10.00);
        payment.setStatus(Enums.PaymentStatus.PENDING);
        payment.setTransactionPaymentUrl("http://payment.url/1");
        payment.setTransactionId("trans1");

        when(tools.getClientIdFromRequest(request)).thenReturn(clientId);
        when(orderService.getOrderById(orderId)).thenReturn(order);
        when(paymentService.getPaymentsByOrderId(orderId)).thenReturn(List.of(payment));

        ResponseEntity<OrderWithPaymentsDTO> response = orderController.getOrderById(request, orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status odpowiedzi powinien być 200 OK");
        OrderWithPaymentsDTO dto = response.getBody();
        assertNotNull(dto, "DTO nie powinno być null");
        assertEquals(orderId, dto.getOrderId(), "ID zamówienia w DTO powinno być równe podanemu");

        System.out.println("Test pobierania zamówienia po ID przeszedł pomyślnie.");
    }

    @Test
    @DisplayName("Test pobierania zamówienia po ID - brak uprawnień")
    public void testGetOrderByIdForbidden() {
        Long clientId = 1L;
        Long orderId = 1L;

        Order order = new Order();
        order.setId(orderId);
        order.setOrderType(Enums.OrderType.ACTIVATION);
        order.setStatus(Enums.OrderStatus.PENDING);
        order.setAmount(10.00);
        order.setCreatedAt(LocalDateTime.now());

        Client client = new Client();
        client.setId(2L);
        order.setClient(client);

        when(tools.getClientIdFromRequest(request)).thenReturn(clientId);
        when(orderService.getOrderById(orderId)).thenReturn(order);

        ResponseEntity<OrderWithPaymentsDTO> response = orderController.getOrderById(request, orderId);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(), "Status odpowiedzi powinien być 403 FORBIDDEN");

        System.out.println("Test pobierania zamówienia po ID - brak uprawnień przeszedł pomyślnie.");
    }
}