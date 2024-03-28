package com.example.miniproject.domain.order.service;

import com.example.miniproject.domain.hotel.constant.*;
import com.example.miniproject.domain.hotel.dto.BasicOptions;
import com.example.miniproject.domain.hotel.entity.Hotel;
import com.example.miniproject.domain.hotel.entity.Room;
import com.example.miniproject.domain.hotel.service.RoomService;
import com.example.miniproject.domain.member.constant.MemberRole;
import com.example.miniproject.domain.member.constant.MemberStatus;
import com.example.miniproject.domain.member.entity.Member;
import com.example.miniproject.domain.member.service.MemberService;
import com.example.miniproject.domain.order.constant.OrderStatus;
import com.example.miniproject.domain.order.dto.OrderDTO;
import com.example.miniproject.domain.order.entity.Order;
import com.example.miniproject.domain.order.repository.OrderRepository;
import com.example.miniproject.exception.ApiErrorCode;
import com.example.miniproject.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("오더 서비스 테스트")
@ActiveProfiles("test")
@Transactional
@SpringBootTest
class OrderServiceTest {

    @Mock
    private MemberService memberService;

    @Mock
    private RoomService roomService;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Member member;
    private Room room;

    @BeforeEach
    void BeforeEach() {
        BasicOptions basicOptions = BasicOptions.builder()
                .swimmingPool(true)
                .breakFast(true)
                .wirelessInternet(true)
                .dryCleaning(true)
                .storageService(true)
                .convenienceStore(true)
                .ironingTools(true)
                .wakeupCall(true)
                .miniBar(true)
                .showerRoom(true)
                .airConditioner(true)
                .table(true)
                .tv(true)
                .safetyDepositBox(true)
                .build();

        member = Member.builder()
                .email("test@example.com")
                .password("password")
                .name("name")
                .birth("20000101")
                .status(MemberStatus.CERTIFICATED)
                .role(MemberRole.USER)
                .credit(new BigDecimal("1000000.00"))
                .uuid("uuid")
                .build();

        Hotel hotel = Hotel.builder()
                .nation(Nation.PHILIPPINES)
                .name("hotelName")
                .description("description")
                .checkIn(LocalTime.of(15, 0))
                .checkOut(LocalTime.of(11, 0))
                .smokingRule(SmokingRule.FULL_AVAILABLE)
                .petRule(PetRule.SOME_POSSIBLE)
                .poolOpeningTime(LocalTime.of(8, 0))
                .poolClosingTime(LocalTime.of(17, 0))
                .basicOptions(basicOptions)
                .activeStatus(ActiveStatus.ACTIVE)
                .registerStatus(RegisterStatus.VISIBLE)
                .build();

        room = Room.builder()
                .hotel(hotel)
                .type(RoomType.DELUXE)
                .registerStatus(RegisterStatus.VISIBLE)
                .activeStatus(ActiveStatus.ACTIVE)
                .bedType(BedType.DOUBLE)
                .standardCapacity(2)
                .maximumCapacity(4)
                .viewType(ViewType.CITY)
                .standardPrice(new BigDecimal("100000.00"))
                .adultFare(new BigDecimal("10000.00"))
                .childFare(new BigDecimal("5000.00"))
                .build();

    }


    @Test
    @WithMockUser
    public void 예약하기_예약_추가정보_입력_성공() {

        BigDecimal totalPrice = room.getStandardPrice()
                .add(room.getAdultFare().multiply(BigDecimal.valueOf(2)))
                .add(room.getChildFare().multiply(BigDecimal.valueOf(0)));

        OrderDTO.OrderRequest request = OrderDTO.OrderRequest.builder()
                .roomId(room.getId())
                .checkIn(LocalDate.now())
                .checkOut(LocalDate.now().plusDays(1))
                .adultCount(2)
                .childCount(0)
                .totalPrice(totalPrice)
                .build();

        Order order = Order.saveAs(
                member,
                room,
                request.getCheckIn(),
                request.getCheckOut(),
                request.getAdultCount(),
                request.getChildCount(),
                request.getTotalPrice());

        given(memberService.getValidMemberOrThrow(member.getEmail())).willReturn(member);
        given(roomService.getVisibleAndActiveRoomOrThrow(request.getRoomId())).willReturn(room);
        given(orderRepository.save(any(Order.class))).willReturn(order);

        OrderDTO.OrderResponse result = orderService.createOrder(member.getEmail(), request);

        assertEquals(order.getId(), result.getId());
        verify(orderRepository).save(any(Order.class));

    }

    @Test
    @WithMockUser
    public void 예약하기_예약_추가정보_입력_실패_최대_인원_초과() {
        Long roomId = room.getId();
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(1);
        int adultCount = room.getMaximumCapacity() + 1;
        int childCount = 0;

        BigDecimal totalPrice = room.getStandardPrice()
                .add(room.getAdultFare().multiply(BigDecimal.valueOf(2)))
                .add(room.getChildFare().multiply(BigDecimal.valueOf(0)));

        OrderDTO.OrderRequest request = OrderDTO.OrderRequest.builder()
                .roomId(roomId)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .adultCount(adultCount)
                .childCount(childCount)
                .totalPrice(totalPrice)
                .build();

        given(memberService.getValidMemberOrThrow(member.getEmail())).willReturn(member);
        given(roomService.getVisibleAndActiveRoomOrThrow(roomId)).willReturn(room);

        ApiException apiException =
                assertThrows(ApiException.class, () -> orderService.createOrder(member.getEmail(), request));

        assertEquals(ApiErrorCode.EXCEEDS_MAXIMUM_CAPACITY.getDescription(), apiException.getErrorDescription());

    }

    @Test
    @WithMockUser
    public void 예약하기_예약_추가정보_입력_실패_없는_객실() {
        Long roomId = 1L;
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(1);
        int adultCount = 2;
        int childCount = 0;

        BigDecimal totalPrice = room.getStandardPrice()
                .add(room.getAdultFare().multiply(BigDecimal.valueOf(adultCount)))
                .add(room.getChildFare().multiply(BigDecimal.valueOf(childCount)));

        OrderDTO.OrderRequest request = OrderDTO.OrderRequest.builder()
                .roomId(roomId)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .adultCount(adultCount)
                .childCount(childCount)
                .totalPrice(totalPrice)
                .build();

        given(memberService.getValidMemberOrThrow("test@example.com")).willReturn(member);
        given(roomService.getVisibleAndActiveRoomOrThrow(anyLong()))
                .willThrow(new ApiException(ApiErrorCode.NOT_AVAILABLE_ROOM.getDescription()));

        ApiException apiException =
                assertThrows(ApiException.class, () -> orderService.createOrder("test@example.com", request));

        assertEquals(ApiErrorCode.NOT_AVAILABLE_ROOM.getDescription(), apiException.getErrorDescription());

    }

    @Test
    @WithMockUser
    public void 예약하기_예약_상세정보_입력_및_주문_성공() {

        Long orderId = 1L;
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(1);
        int adultCount = 2;
        int childCount = 0;
        String zipCode = "zipCode";
        String memberNation = "KOREA";
        String city = "SEOUL";
        String address = "address";
        String comment = "comment";

        BigDecimal totalPrice = room.getStandardPrice()
                .add(room.getAdultFare().multiply(BigDecimal.valueOf(adultCount)))
                .add(room.getChildFare().multiply(BigDecimal.valueOf(childCount)));

        OrderDTO.OrderInfoRequest request = OrderDTO.OrderInfoRequest.builder()
                .zipCode(zipCode)
                .nation(memberNation)
                .city(city)
                .address(address)
                .comment(comment)
                .build();

        Order order = Order.saveAs(
                member,
                room,
                checkIn,
                checkOut,
                adultCount,
                childCount,
                totalPrice);
        order.updateStatus(OrderStatus.PAYMENT_PENDING);

        given(orderRepository.findById(anyLong())).willReturn(Optional.of(order));

        orderService.updateOrderInfo(member.getEmail(), orderId, request);

        verify(memberService).updateMember(any(Member.class));
        assertEquals(OrderStatus.PAYMENT_COMPLETED, order.getStatus());

    }

    @Test
    @WithMockUser
    public void 예약하기_예약_상세정보_입력_및_주문_실패_없는_주문() {

        Long orderId = 1L;

        OrderDTO.OrderInfoRequest request = OrderDTO.OrderInfoRequest.builder()
                .zipCode("zipCode")
                .nation("KOREA")
                .city("SEOUL")
                .address("address")
                .comment("comment")
                .build();

        given(orderRepository.findById(anyLong())).willReturn(Optional.empty());

        ApiException apiException = assertThrows(ApiException.class, () -> {
            orderService.updateOrderInfo(member.getEmail(), orderId, request);
        });

        assertEquals(ApiErrorCode.NOT_FOUND_ORDER.getDescription(), apiException.getErrorDescription());
    }

    @Test
    @WithMockUser
    public void 예약하기_예약_상세정보_입력_및_주문_실패_회원_불일치() {

        Long orderId = 1L;
        String email = "test@example.com";
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(1);
        int adultCount = 2;
        int childCount = 0;
        BigDecimal totalPrice = new BigDecimal("100000.00");

        Member member = Member.builder()
                .email("another@example.com")
                .build();

        Order order = Order.saveAs(
                member,
                room,
                checkIn,
                checkOut,
                adultCount,
                childCount,
                totalPrice);
        order.updateStatus(OrderStatus.PAYMENT_PENDING);

        OrderDTO.OrderInfoRequest request = OrderDTO.OrderInfoRequest.builder()
                .zipCode("zipCode")
                .nation("KOREA")
                .city("SEOUL")
                .address("address")
                .comment("comment")
                .build();

        given(orderRepository.findById(anyLong())).willReturn(Optional.of(order));

        ApiException apiException = assertThrows(ApiException.class, () -> {
            orderService.updateOrderInfo(email, orderId, request);
        });

        assertEquals(ApiErrorCode.NOT_MATCH_MEMBER.getDescription(), apiException.getErrorDescription());
    }

    @Test
    @WithMockUser
    public void 예약하기_예약_상세정보_입력_및_주문_실패_잔액부족() {

        Long orderId = 1L;
        String email = "test@example.com";
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(1);
        int adultCount = 2;
        int childCount = 0;
        BigDecimal totalPrice = new BigDecimal("100000.00");
        BigDecimal credit = new BigDecimal("000.00");

        Member member = Member.builder()
                .email(email)
                .credit(credit)
                .build();

        Order order = Order.saveAs(
                member,
                room,
                checkIn,
                checkOut,
                adultCount,
                childCount,
                totalPrice);
        order.updateStatus(OrderStatus.PAYMENT_PENDING);

        OrderDTO.OrderInfoRequest request = OrderDTO.OrderInfoRequest.builder()
                .zipCode("zipCode")
                .nation("KOREA")
                .city("SEOUL")
                .address("address")
                .comment("comment")
                .build();

        given(orderRepository.findById(anyLong())).willReturn(Optional.of(order));

        ApiException apiException = assertThrows(ApiException.class, () -> {
            orderService.updateOrderInfo(member.getEmail(), orderId, request);
        });

        assertEquals(ApiErrorCode.LACK_CREDIT.getDescription(), apiException.getErrorDescription());
    }

    @Test
    @WithMockUser
    public void 예약하기_예약_상세정보_입력_및_주문_실패_예약상태_결제완료() {

        Long orderId = 1L;
        String email = "test@example.com";
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(1);
        int adultCount = 2;
        int childCount = 0;
        BigDecimal totalPrice = new BigDecimal("100000.00");
        BigDecimal credit = new BigDecimal("1000000.00");

        Member member = Member.builder()
                .email(email)
                .credit(credit)
                .build();

        Order order = Order.saveAs(
                member,
                room,
                checkIn,
                checkOut,
                adultCount,
                childCount,
                totalPrice);
        order.updateStatus(OrderStatus.PAYMENT_COMPLETED); // 결제 완료

        OrderDTO.OrderInfoRequest request = OrderDTO.OrderInfoRequest.builder()
                .zipCode("zipCode")
                .nation("KOREA")
                .city("SEOUL")
                .address("address")
                .comment("comment")
                .build();

        given(orderRepository.findById(anyLong())).willReturn(Optional.of(order));

        ApiException apiException = assertThrows(ApiException.class, () -> {
            orderService.updateOrderInfo(email, orderId, request);
        });

        assertEquals(ApiErrorCode.NOT_FOUND_ORDER.getDescription(), apiException.getErrorDescription());
    }

    @Test
    @WithMockUser
    public void 예약하기_예약_상세정보_입력_및_주문_실패_예약상태_결제취소() {

        Long orderId = 1L;
        String email = "test@example.com";
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(1);
        int adultCount = 2;
        int childCount = 0;
        BigDecimal totalPrice = new BigDecimal("100000.00");
        BigDecimal credit = new BigDecimal("1000000.00");

        Member member = Member.builder()
                .email(email)
                .credit(credit)
                .build();

        Order order = Order.saveAs(
                member,
                room,
                checkIn,
                checkOut,
                adultCount,
                childCount,
                totalPrice);
        order.updateStatus(OrderStatus.PAYMENT_CANCELED); // 결제 취소

        OrderDTO.OrderInfoRequest request = OrderDTO.OrderInfoRequest.builder()
                .zipCode("zipCode")
                .nation("KOREA")
                .city("SEOUL")
                .address("address")
                .comment("comment")
                .build();

        given(orderRepository.findById(anyLong())).willReturn(Optional.of(order));

        ApiException apiException = assertThrows(ApiException.class, () -> {
            orderService.updateOrderInfo(email, orderId, request);
        });

        assertEquals(ApiErrorCode.NOT_FOUND_ORDER.getDescription(), apiException.getErrorDescription());
    }

}