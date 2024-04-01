package com.example.miniproject.domain.order.controller;

import com.example.miniproject.domain.hotel.constant.ActiveStatus;
import com.example.miniproject.domain.hotel.constant.BedType;
import com.example.miniproject.domain.hotel.constant.RoomType;
import com.example.miniproject.domain.hotel.constant.ViewType;
import com.example.miniproject.domain.hotel.dto.RoomDTO;
import com.example.miniproject.domain.hotel.entity.Hotel;
import com.example.miniproject.domain.hotel.entity.Room;
import com.example.miniproject.domain.member.service.MemberService;
import com.example.miniproject.domain.order.constant.OrderStatus;
import com.example.miniproject.domain.order.dto.OrderDTO;
import com.example.miniproject.domain.order.service.OrderService;
import com.example.miniproject.exception.ApiErrorCode;
import com.example.miniproject.exception.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("오더 컨트롤러 테스트")
@ActiveProfiles("default")
@AutoConfigureMockMvc
@Transactional
@SpringBootTest
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private MemberService memberService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;


    @Test
    @WithMockUser
    public void 예약_추가_정보_입력_성공() throws Exception {
        Long roomId = 1L;
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(1);
        int adultCount = 2;
        int childCount = 1;
        BigDecimal totalPrice = new BigDecimal("100000.00");

        OrderDTO.OrderRequest orderRequest = OrderDTO.OrderRequest.builder()
                .roomId(roomId)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .adultCount(adultCount)
                .childCount(childCount)
                .totalPrice(totalPrice)
                .build();

        OrderDTO.OrderResponse orderResponse = OrderDTO.OrderResponse.builder()
                .id(1L)
                .memberId(1L)
                .roomId(orderRequest.getRoomId())
                .checkIn(orderRequest.getCheckIn())
                .checkOut(orderRequest.getCheckOut())
                .adultCount(orderRequest.getAdultCount())
                .childCount(orderRequest.getChildCount())
                .totalPrice(orderRequest.getTotalPrice())
                .build();

        given(orderService.createOrder(anyString(), any(OrderDTO.OrderRequest.class)))
                .willReturn(orderResponse);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result.room_id").value(orderRequest.getRoomId()));
    }

    @Test
    @WithMockUser
    public void 예약_추가정보_입력_실패_없는_객실() throws Exception {

        Long roomId = 1L;
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(1);
        int adultCount = 2;
        int childCount = 1;
        BigDecimal totalPrice = new BigDecimal("100000.00");

        OrderDTO.OrderRequest orderRequest = OrderDTO.OrderRequest.builder()
                .roomId(roomId)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .adultCount(adultCount)
                .childCount(childCount)
                .totalPrice(totalPrice)
                .build();

        given(orderService.createOrder(anyString(), any(OrderDTO.OrderRequest.class)))
                .willThrow(new ApiException(ApiErrorCode.NOT_AVAILABLE_ROOM.getDescription()));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.result").value(ApiErrorCode.NOT_AVAILABLE_ROOM.getDescription()));
    }

    @Test
    @WithMockUser
    public void 예약_추가정보_입력_실패_최대인원_초과() throws Exception {

        Long roomId = 1L;
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(1);
        int adultCount = 3;
        int childCount = 2;
        BigDecimal totalPrice = new BigDecimal("100000.00");

        OrderDTO.OrderRequest orderRequest = OrderDTO.OrderRequest.builder()
                .roomId(roomId)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .adultCount(adultCount)
                .childCount(childCount)
                .totalPrice(totalPrice)
                .build();

        Hotel hotel = new Hotel();

        RoomType type = RoomType.DELUXE;
        ActiveStatus activeStatus = ActiveStatus.ACTIVE;
        BedType bedType = BedType.BUNK_BED;
        int standardCapacity = 2;
        int maximumCapacity = 4;
        ViewType viewType = ViewType.CITY;
        BigDecimal standardPrice = new BigDecimal("10000.00");
        BigDecimal adultFare = new BigDecimal("1000.00");
        BigDecimal childFare = new BigDecimal("500.00");

        RoomDTO.Request roomRequest = RoomDTO.Request.builder()
                .type(type)
                .activeStatus(activeStatus)
                .bedType(bedType)
                .standardCapacity(standardCapacity)
                .maximumCapacity(maximumCapacity)
                .viewType(viewType)
                .standardPrice(standardPrice)
                .adultFare(adultFare)
                .childFare(childFare)
                .build();

        Room.saveAs(hotel, roomRequest);

        given(orderService.createOrder(anyString(), any(OrderDTO.OrderRequest.class)))
                .willThrow(new ApiException(ApiErrorCode.EXCEEDS_MAXIMUM_CAPACITY.getDescription()));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.result").value(ApiErrorCode.EXCEEDS_MAXIMUM_CAPACITY.getDescription()));
    }

    @Test
    @WithMockUser
    public void 예약_추가정보_입력_실패_로그인사용자와_예약자_불일치() throws Exception {

        Long roomId = 1L;
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(1);
        int adultCount = 3;
        int childCount = 2;
        BigDecimal totalPrice = new BigDecimal("100000.00");

        OrderDTO.OrderRequest orderRequest = OrderDTO.OrderRequest.builder()
                .roomId(roomId)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .adultCount(adultCount)
                .childCount(childCount)
                .totalPrice(totalPrice)
                .build();

        given(orderService.createOrder(anyString(), any(OrderDTO.OrderRequest.class)))
                .willThrow(new ApiException(ApiErrorCode.NOT_FOUND_MEMBER.getDescription()));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.result").value(ApiErrorCode.NOT_FOUND_MEMBER.getDescription()));
    }

    @Test
    @WithMockUser
    public void 예약_개인상세정보_입력_성공() throws Exception {
        String zipCode = "123-1234";
        String nation = "KOREA";
        String city = "서울시";
        String address = "강남구 압구정로";
        String comment = "잘 부탁드립니다.";
        Long orderId = 1L;

        OrderDTO.OrderInfoRequest orderRequest = OrderDTO.OrderInfoRequest.builder()
                .zipCode(zipCode)
                .nation(nation)
                .city(city)
                .address(address)
                .comment(comment)
                .build();

        mockMvc.perform(patch("/api/orders/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(orderService, times(1))
                .updateOrderInfo(anyString(), anyLong(), any(OrderDTO.OrderInfoRequest.class));
    }

    @Test
    @WithMockUser
    public void 예약_개인상세정보_입력_실패_없는_예약아이디() throws Exception {

        String zipCode = "123-1234";
        String nation = "KOREA";
        String city = "서울시";
        String address = "강남구 압구정로";
        String comment = "잘 부탁드립니다.";
        Long orderId = 1L;

        OrderDTO.OrderInfoRequest orderRequest = OrderDTO.OrderInfoRequest.builder()
                .zipCode(zipCode)
                .nation(nation)
                .city(city)
                .address(address)
                .comment(comment)
                .build();

        doThrow(new ApiException(ApiErrorCode.NOT_FOUND_ORDER.getDescription()))
                .when(orderService).updateOrderInfo(anyString(), anyLong(), any());

        mockMvc.perform(patch("/api/orders/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.result").value(ApiErrorCode.NOT_FOUND_ORDER.getDescription()));
    }

    @Test
    @WithMockUser
    public void 마스터_예약_전체내역_조회_성공() throws Exception {
        Long orderId = 1L;
        String address = "강남구 압구정로";
        String city = "서울시";
        String nation = "KOREA";
        String zipCode = "123-1234";
        String comment = "잘 부탁드립니다.";
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(1);
        int adultCount = 2;
        int childCount = 1;
        BigDecimal totalPrice = new BigDecimal("100000.00");
        OrderStatus status = OrderStatus.PAYMENT_COMPLETED;

        List<OrderDTO.OrderDetailResponse> list = List.of(
                OrderDTO.OrderDetailResponse.builder()
                        .id(orderId)
                        .member(null)
                        .hotel(null)
                        .room(null)
                        .checkIn(checkIn)
                        .checkOut(checkOut)
                        .adultCount(adultCount)
                        .childCount(childCount)
                        .totalPrice(totalPrice)
                        .status(status)
                        .address(address)
                        .city(city)
                        .nation(nation)
                        .zipCode(zipCode)
                        .comment(comment)
                        .build());

        Pageable pageable = PageRequest.of(0, 1);
        Page<OrderDTO.OrderDetailResponse> responsePage = new PageImpl<>(list, pageable, list.size());

        given(orderService.orderList(anyString(), any()))
                .willReturn(responsePage);

        mockMvc.perform(get("/api/orders/order-list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(responsePage)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content").isArray())
                .andExpect(jsonPath("$.result.content[0].id").value(list.get(0).getId()));
    }

    @Test
    @WithMockUser
    public void 마스터_예약_전체내역_조회_실패_마스터계정_미인증() throws Exception {

        Long orderId = 1L;
        String address = "강남구 압구정로";
        String city = "서울시";
        String nation = "KOREA";
        String zipCode = "123-1234";
        String comment = "잘 부탁드립니다.";
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(1);
        int adultCount = 2;
        int childCount = 1;
        BigDecimal totalPrice = new BigDecimal("100000.00");
        OrderStatus status = OrderStatus.PAYMENT_COMPLETED;

        List<OrderDTO.OrderDetailResponse> list = List.of(
                OrderDTO.OrderDetailResponse.builder()
                        .id(orderId)
                        .member(null)
                        .hotel(null)
                        .room(null)
                        .checkIn(checkIn)
                        .checkOut(checkOut)
                        .adultCount(adultCount)
                        .childCount(childCount)
                        .totalPrice(totalPrice)
                        .status(status)
                        .address(address)
                        .city(city)
                        .nation(nation)
                        .zipCode(zipCode)
                        .comment(comment)
                        .build());

        Pageable pageable = PageRequest.of(0, 1);
        Page<OrderDTO.OrderDetailResponse> responsePage = new PageImpl<>(list, pageable, list.size());

        doThrow(new ApiException(ApiErrorCode.NO_PERMISSION.getDescription()))
                .when(orderService).orderList(anyString(), any());

        mockMvc.perform(get("/api/orders/order-list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(responsePage)))
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.result").value(ApiErrorCode.NO_PERMISSION.getDescription()));
    }

}
