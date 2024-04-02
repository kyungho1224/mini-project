package com.example.miniproject.domain.hotel.service;

import com.example.miniproject.common.service.ImageService;
import com.example.miniproject.domain.hotel.constant.*;
import com.example.miniproject.domain.hotel.dto.BasicOptions;
import com.example.miniproject.domain.hotel.dto.HotelDTO;
import com.example.miniproject.domain.hotel.entity.Favorite;
import com.example.miniproject.domain.hotel.entity.Hotel;
import com.example.miniproject.domain.hotel.entity.HotelThumbnail;
import com.example.miniproject.domain.hotel.repository.FavoriteRepository;
import com.example.miniproject.domain.hotel.repository.HotelRepository;
import com.example.miniproject.domain.hotel.repository.HotelThumbnailRepository;
import com.example.miniproject.domain.member.constant.MemberRole;
import com.example.miniproject.domain.member.constant.MemberStatus;
import com.example.miniproject.domain.member.entity.Member;
import com.example.miniproject.domain.member.service.MemberService;
import com.example.miniproject.exception.ApiErrorCode;
import com.example.miniproject.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DisplayName("호텔 서비스 테스트")
@ActiveProfiles("test")
@Transactional
@SpringBootTest
class HotelServiceTest {

    @InjectMocks
    private HotelService hotelService;

    @Mock
    private MemberService memberService;

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private HotelThumbnailRepository hotelThumbnailRepository;

    @Mock
    private ImageService imageService;

    @Mock
    private FavoriteRepository favoriteRepository;

    private Member member;
    private Hotel hotel;
    private HotelDTO.Request request;

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
          .email("master@example.com")
          .password("password")
          .name("name")
          .birth("20000101")
          .status(MemberStatus.CERTIFICATED)
          .role(MemberRole.MASTER)
          .credit(new BigDecimal("1000000.00"))
          .uuid("uuid")
          .build();

        hotel = Hotel.builder()
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
          .rooms(null)
          .build();

        request = HotelDTO.Request.builder()
          .nation(hotel.getNation())
          .name(hotel.getName())
          .description(hotel.getDescription())
          .basicOptions(hotel.getBasicOptions())
          .checkIn(hotel.getCheckIn())
          .checkOut(hotel.getCheckOut())
          .smokingRule(hotel.getSmokingRule())
          .petRule(hotel.getPetRule())
          .poolOpeningTime(hotel.getPoolOpeningTime())
          .poolClosingTime(hotel.getPoolOpeningTime())
          .activeStatus(hotel.getActiveStatus())
          .build();

        ReflectionTestUtils.setField(hotel, "id", 1L);
        ReflectionTestUtils.setField(hotel, "registerStatus", RegisterStatus.VISIBLE);

    }

    @Test
    @WithMockUser
    public void 호텔_전체_조회_성공() {

        Pageable pageable = PageRequest.of(0, 3);
        List<Hotel> mockHotels = Arrays.asList(new Hotel(), new Hotel(), new Hotel());
        Page<Hotel> mockPage = new PageImpl<>(mockHotels, pageable, mockHotels.size());

        given(hotelRepository.findAllByRegisterStatus(pageable, RegisterStatus.VISIBLE))
          .willReturn(mockPage);

        Page<HotelDTO.Response> resultPage = hotelService.findAllVisibleHotels(pageable);

        assertEquals(mockHotels.size(), resultPage.getContent().size());

    }

    @Test
    @WithMockUser
    public void 호텔_국가_조회_성공() {

        Nation desiredNation = Nation.MALAYSIA;
        Pageable pageable = PageRequest.of(0, 3);
        List<Hotel> mockHotels = Arrays.asList(new Hotel(), new Hotel());
        Page<Hotel> mockPage = new PageImpl<>(mockHotels, pageable, mockHotels.size());

        given(hotelRepository.findAllByNationAndRegisterStatus(pageable, desiredNation, RegisterStatus.VISIBLE))
          .willReturn(mockPage);

        Page<HotelDTO.Response> resultPage = hotelService.findByNation(desiredNation, pageable);

        assertEquals(mockHotels.size(), resultPage.getContent().size());

    }

    @Test
    @WithMockUser
    public void 호텔_호텔명_조회_성공() {

        String searchName = "오크우드";
        Pageable pageable = PageRequest.of(0, 3);
        List<Hotel> mockHotels = Arrays.asList(new Hotel(), new Hotel());
        Page<Hotel> mockPage = new PageImpl<>(mockHotels, pageable, mockHotels.size());

        given(hotelRepository.findByNameContainingAndRegisterStatus(searchName, RegisterStatus.VISIBLE, pageable))
          .willReturn(mockPage);

        Page<HotelDTO.Response> resultPage = hotelService.findHotelsByNameAndVisible(searchName, pageable);

        assertEquals(mockHotels.size(), resultPage.getContent().size());

    }

    @Test
    @WithMockUser
    public void 호텔_호텔id로_개별_조회_성공() {

        Hotel hotel = mock(Hotel.class);

        given(hotel.getId()).willReturn(1L);
        given(hotel.getName()).willReturn("오크우드 호텔");

        given(hotelRepository.findByIdAndRegisterStatus(eq(1L), eq(RegisterStatus.VISIBLE)))
          .willReturn(Optional.of(hotel));

        HotelDTO.Response response = hotelService.findHotelById(1L);

        assertEquals(hotel.getId(), response.getId());
        assertEquals(hotel.getName(), response.getName());

        verify(hotelRepository, times(1))
          .findByIdAndRegisterStatus(eq(1L), eq(RegisterStatus.VISIBLE));

    }

    @Test
    @WithMockUser
    public void 전체_상품_호텔_이름_국가_조회_성공() {

        String name = "hotel";
        Nation nation = Nation.PHILIPPINES;
        Pageable pageable = PageRequest.of(0, 10);

        Page<Hotel> mockHotelPage = new PageImpl<>(Collections.singletonList(hotel));
        given(hotelRepository.findByNameAndNationContainingAndRegisterStatus(
          any(String.class), any(Nation.class), any(RegisterStatus.class), any(Pageable.class)))
          .willReturn(mockHotelPage);

        Page<HotelDTO.Response> response = hotelService.findHotelsByNameAndNationAndVisible(
          name, nation, pageable);

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getName())
          .isEqualTo(hotel.getName());
        assertThat(response.getContent().get(0).getNation())
          .isEqualTo(hotel.getNation());

        verify(hotelRepository).findByNameAndNationContainingAndRegisterStatus(
          name, nation, RegisterStatus.VISIBLE, pageable);

    }

    @Test
    @WithMockUser
    public void 전체_상품_호텔_이름_국가_룸타입_뷰타입_조회_성공() {

        Nation nation = Nation.PHILIPPINES;
        RoomType roomType = RoomType.TWIN;
        ViewType viewType = ViewType.OCEAN;
        Pageable pageable = PageRequest.of(0, 10);

        List<Hotel> mockHotelList = new ArrayList<>();
        Hotel mockHotel = new Hotel();
        mockHotelList.add(mockHotel);

        Page<Hotel> mockPage = new PageImpl<>(mockHotelList);
        given(hotelRepository.findByNationAndRoomTypeAndViewTypeAndRegisterStatus(
          any(Nation.class), any(RoomType.class), any(ViewType.class),
          any(RegisterStatus.class), any(Pageable.class)))
          .willReturn(mockPage);

        Page<HotelDTO.Response> response = hotelService.findHotelsByNationAndTypeAndVisible(
          nation, roomType, viewType, pageable);

        assertEquals(1, response.getContent().size());

    }

    @Test
    @WithMockUser
    public void 마스터_호텔_등록_데이터_성공() {

        hotelService.create(member.getEmail(), request);

        verify(memberService).getMasterMemberOrThrow(member.getEmail());
        verify(hotelRepository).save(any(Hotel.class));

    }

    @Test
    @WithMockUser
    public void 마스터_호텔_등록_데이터_실패_권한없음() {

        doThrow(new ApiException(ApiErrorCode.NO_PERMISSION.getDescription()))
          .when(memberService).getMasterMemberOrThrow(member.getEmail());

        assertThrows(ApiException.class, () -> {
            hotelService.create(member.getEmail(), request);
        });

        verify(memberService).getMasterMemberOrThrow(member.getEmail());
        verify(hotelRepository, never()).save(any(Hotel.class));

    }

    @Test
    @WithMockUser
    public void 마스터_호텔_등록_이미지포함_성공() throws Exception {

        MockMultipartFile file1 = new MockMultipartFile(
          "files",
          "thumbnail1.jpg",
          "image/jpeg",
          "Thumbnail 1".getBytes(StandardCharsets.UTF_8));

        MockMultipartFile file2 = new MockMultipartFile(
          "files",
          "thumbnail2.jpg",
          "image/jpeg",
          "Thumbnail 2".getBytes(StandardCharsets.UTF_8));

        given(hotelRepository.save(any(Hotel.class)))
          .willReturn(hotel);
        given(hotelThumbnailRepository.save(any(HotelThumbnail.class)))
          .willAnswer(i -> i.getArguments()[0]);
        given(hotelRepository.findByIdAndRegisterStatus(eq(1L), eq(RegisterStatus.VISIBLE)))
          .willReturn(Optional.of(hotel));

        MultipartFile[] files = new MultipartFile[]{file1, file2};
        hotelService.create(member.getEmail(), request, files);

        verify(memberService, times(2)).getMasterMemberOrThrow(member.getEmail());
        verify(hotelRepository, times(1)).save(any(Hotel.class));
        verify(imageService, times(files.length)).upload(any(MultipartFile.class), anyString());
        verify(hotelThumbnailRepository, times(files.length)).save(any(HotelThumbnail.class));

    }

    @Test
    @WithMockUser
    public void 마스터_호텔_등록_이미지포함_실패_권한없음() {

        MockMultipartFile file1 = new MockMultipartFile(
          "files",
          "thumbnail1.jpg",
          "image/jpeg",
          "Thumbnail 1".getBytes(StandardCharsets.UTF_8));

        MockMultipartFile file2 = new MockMultipartFile(
          "files",
          "thumbnail2.jpg",
          "image/jpeg",
          "Thumbnail 2".getBytes(StandardCharsets.UTF_8));

        MultipartFile[] files = new MultipartFile[]{file1, file2};

        doThrow(new ApiException(ApiErrorCode.NO_PERMISSION.getDescription()))
          .when(memberService).getMasterMemberOrThrow(member.getEmail());

        assertThrows(ApiException.class, () -> {
            hotelService.create(member.getEmail(), request, files);
        });

        verify(memberService).getMasterMemberOrThrow(member.getEmail());
        verify(hotelRepository, never()).save(any(Hotel.class));

    }

    @Test
    @WithMockUser
    public void 마스터_호텔_수정_데이터_성공() {

        Member member = mock(Member.class);
        Hotel hotel = mock(Hotel.class);
        HotelDTO.Request request = mock(HotelDTO.Request.class);

        given(member.getEmail()).willReturn("master@example.com");
        given(hotel.getId()).willReturn(1L);

        given(memberService.getMasterMemberOrThrow(anyString()))
          .willReturn(member);
        given(hotelRepository.findByIdAndRegisterStatus(anyLong(), any()))
          .willReturn(Optional.of(hotel));

        hotelService.updateData(member.getEmail(), hotel.getId(), request);

        verify(memberService).getMasterMemberOrThrow(member.getEmail());
        verify(hotelRepository).findByIdAndRegisterStatus(hotel.getId(), RegisterStatus.VISIBLE);
        verify(hotel).updateData(request);

    }

    @Test
    @WithMockUser
    public void 마스터_호텔_수정_데이터_실패_권한없음() {

        String email = "user@example.com";
        Long hotelId = 1L;
        HotelDTO.Request request = mock(HotelDTO.Request.class);

        doThrow(new ApiException(ApiErrorCode.NO_PERMISSION.getDescription()))
          .when(memberService).getMasterMemberOrThrow(email);

        ApiException exception = assertThrows(ApiException.class, () -> {
            hotelService.updateData(email, hotelId, request);
        });

        assertEquals(ApiErrorCode.NO_PERMISSION.getDescription(), exception.getMessage());

    }

    @Test
    @WithMockUser
    public void 마스터_호텔_수정_데이터_실패_데이터없음() {

        String email = "master@example.com";
        Long hotelId = 999L;
        HotelDTO.Request request = mock(HotelDTO.Request.class);

        given(hotelRepository.findByIdAndRegisterStatus(eq(hotelId), any()))
          .willReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            hotelService.updateData(email, hotelId, request);
        });

        assertEquals(ApiErrorCode.NOT_FOUND_HOTEL.getDescription(), exception.getMessage());

    }

    @Test
    @WithMockUser
    public void 마스터_호텔_썸네일_등록_데이터_성공() throws Exception {

        String email = "master@example.com";
        Long hotelId = 1L;
        MockMultipartFile file = new MockMultipartFile(
          "files",
          "thumbnail1.jpg",
          "image/jpeg",
          "Thumbnail 1".getBytes(StandardCharsets.UTF_8));

        MultipartFile[] files = new MultipartFile[]{file};
        Member member = mock(Member.class);
        Hotel hotel = mock(Hotel.class);
        String imgUrl = "http://test.com/image.jpg";
        HotelThumbnail thumbnail = new HotelThumbnail(hotel, imgUrl);

        given(memberService.getMasterMemberOrThrow(anyString()))
          .willReturn(member);
        given(hotelRepository.findByIdAndRegisterStatus(anyLong(), eq(RegisterStatus.VISIBLE)))
          .willReturn(Optional.of(hotel));
        given(imageService.upload(any(MultipartFile.class), anyString()))
          .willReturn(imgUrl);
        given(hotelThumbnailRepository.save(any(HotelThumbnail.class)))
          .willReturn(thumbnail);

        hotelService.uploadThumbnail(email, hotelId, files);

        verify(memberService).getMasterMemberOrThrow(email);
        verify(hotelRepository).findByIdAndRegisterStatus(hotelId, RegisterStatus.VISIBLE);
        verify(imageService).upload(eq(file), anyString());
        verify(hotelThumbnailRepository).save(any(HotelThumbnail.class));
        verify(hotel, times(files.length)).addThumbnail(any(HotelThumbnail.class));

    }

    @Test
    @WithMockUser
    public void 마스터_호텔_썸네일_등록_데이터_실패_권한없음() {

        String email = "user@example.com";
        Long hotelId = 1L;

        MockMultipartFile file = new MockMultipartFile(
          "files",
          "thumbnail1.jpg",
          "image/jpeg",
          "Thumbnail 1".getBytes(StandardCharsets.UTF_8));

        MultipartFile[] files = new MultipartFile[]{file};

        doThrow(new ApiException(ApiErrorCode.NO_PERMISSION.getDescription()))
          .when(memberService).getMasterMemberOrThrow(email);

        ApiException exception = assertThrows(ApiException.class, () -> {
            hotelService.uploadThumbnail(email, hotelId, files);
        });

        assertEquals(ApiErrorCode.NO_PERMISSION.getDescription(), exception.getMessage());

    }

    @Test
    @WithMockUser
    public void 마스터_호텔_썸네일_등록_데이터_실패_데이터없음() {

        String email = "master@example.com";
        Long hotelId = 999L;
        MockMultipartFile file = new MockMultipartFile(
          "files",
          "thumbnail1.jpg",
          "image/jpeg",
          "Thumbnail 1".getBytes(StandardCharsets.UTF_8));

        MultipartFile[] files = new MultipartFile[]{file};

        given(hotelRepository.findByIdAndRegisterStatus(eq(hotelId), eq(RegisterStatus.VISIBLE)))
          .willReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            hotelService.uploadThumbnail(email, hotelId, files);
        });

        assertEquals(ApiErrorCode.NOT_FOUND_HOTEL.getDescription(), exception.getMessage());

    }

    @Test
    @WithMockUser
    public void 마스터_호텔_썸네일_수정_데이터_성공() throws Exception {

        String email = "master@example.com";
        Long hotelId = 1L;
        Long thumbnailId = 1L;
        MockMultipartFile file = new MockMultipartFile(
          "files",
          "thumbnail1.jpg",
          "image/jpeg",
          "Thumbnail 1".getBytes(StandardCharsets.UTF_8));
        Member member = mock(Member.class);
        Hotel hotel = mock(Hotel.class);
        HotelThumbnail thumbnail = mock(HotelThumbnail.class);
        String imgUrl = "http://test.com/image.jpg";

        given(memberService.getMasterMemberOrThrow(anyString()))
          .willReturn(member);
        given(hotelRepository.findByIdAndRegisterStatus(anyLong(), eq(RegisterStatus.VISIBLE)))
          .willReturn(Optional.of(hotel));
        given(hotelThumbnailRepository.findById(anyLong()))
          .willReturn(Optional.of(thumbnail));
        given(imageService.upload(any(MultipartFile.class), anyString()))
          .willReturn(imgUrl);

        hotelService.updateThumbnail(email, hotelId, thumbnailId, file);

        verify(memberService).getMasterMemberOrThrow(email);
        verify(hotelRepository).findByIdAndRegisterStatus(hotelId, RegisterStatus.VISIBLE);
        verify(hotelThumbnailRepository).findById(thumbnailId);
        verify(imageService).upload(eq(file), anyString());
        verify(thumbnail).updateThumbnail(imgUrl);

    }

    @Test
    @WithMockUser
    public void 마스터_호텔_썸네일_수정_데이터_실패_권한없음() {

        String email = "user@example.com";
        Long hotelId = 1L;
        Long thumbnailId = 1L;

        MockMultipartFile file = new MockMultipartFile(
          "files",
          "thumbnail1.jpg",
          "image/jpeg",
          "Thumbnail 1".getBytes(StandardCharsets.UTF_8));

        doThrow(new ApiException(ApiErrorCode.NO_PERMISSION.getDescription()))
          .when(memberService).getMasterMemberOrThrow(email);

        ApiException exception = assertThrows(ApiException.class, () -> {
            hotelService.updateThumbnail(email, hotelId, thumbnailId, file);
        });

        assertEquals(ApiErrorCode.NO_PERMISSION.getDescription(), exception.getMessage());

    }

    @Test
    @WithMockUser
    public void 마스터_호텔_썸네일_수정_데이터_실패_데이터없음() {

        String email = "master@example.com";
        Long hotelId = 999L;
        Long thumbnailId = 1L;
        MockMultipartFile file = new MockMultipartFile(
          "files",
          "thumbnail1.jpg",
          "image/jpeg",
          "Thumbnail 1".getBytes(StandardCharsets.UTF_8));

        given(hotelRepository.findByIdAndRegisterStatus(eq(hotelId), eq(RegisterStatus.VISIBLE)))
          .willReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            hotelService.updateThumbnail(email, hotelId, thumbnailId, file);
        });

        assertEquals(ApiErrorCode.NOT_FOUND_HOTEL.getDescription(), exception.getMessage());

    }

    @Test
    @WithMockUser
    public void 마스터_호텔_삭제_성공() {

        Member member = mock(Member.class);
        Hotel hotel = mock(Hotel.class);
        given(member.getEmail()).willReturn("master@example.com");
        given(hotel.getId()).willReturn(1L);

        when(memberService.getValidMemberOrThrow(member.getEmail())).thenReturn(member);

        when(hotelRepository.findByIdAndRegisterStatus(hotel.getId(), RegisterStatus.VISIBLE))
          .thenReturn(Optional.of(hotel));

        hotelService.unregister(member.getEmail(), hotel.getId());

        verify(memberService, times(1))
          .getValidMemberOrThrow(member.getEmail());
        verify(hotelRepository, times(1))
          .findByIdAndRegisterStatus(hotel.getId(), RegisterStatus.VISIBLE);
        verify(hotel, times(1)).delete();

    }

    @Test
    @WithMockUser
    public void 마스터_호텔_썸네일_삭제_데이터_실패_권한없음() {

        String email = "user@example.com";
        Long hotelId = 1L;

        doThrow(new ApiException(ApiErrorCode.NO_PERMISSION.getDescription()))
          .when(memberService).getValidMemberOrThrow(email);

        ApiException exception = assertThrows(ApiException.class, () -> {
            hotelService.unregister(email, hotelId);
        });

        assertEquals(ApiErrorCode.NO_PERMISSION.getDescription(), exception.getMessage());

    }

    @Test
    @WithMockUser
    public void 마스터_호텔_썸네일_삭제_데이터_실패_데이터없음() {

        String email = "master@example.com";
        Long hotelId = 999L;

        given(hotelRepository.findByIdAndRegisterStatus(eq(hotelId), eq(RegisterStatus.VISIBLE)))
          .willReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            hotelService.unregister(email, hotelId);
        });

        assertEquals(ApiErrorCode.NOT_FOUND_HOTEL.getDescription(), exception.getMessage());

    }

    @Test
    @WithMockUser
    public void 즐겨찾기_등록_성공() {

        String email = "test@example.com";
        Member member = mock(Member.class);
        Hotel hotel = mock(Hotel.class);

        given(memberService.getValidMemberOrThrow(anyString()))
          .willReturn(member);
        given(hotelRepository.findByIdAndRegisterStatus(anyLong(), eq(RegisterStatus.VISIBLE)))
          .willReturn(Optional.of(hotel));
        given(favoriteRepository.findByMemberIdAndHotelId(member.getId(), hotel.getId()))
          .willReturn(Optional.empty());

        hotelService.toggleFavorite(email, hotel.getId());

        verify(favoriteRepository).findByMemberIdAndHotelId(member.getId(), hotel.getId());
        verify(favoriteRepository).save(any(Favorite.class));

    }

    @Test
    @WithMockUser
    public void 즐겨찾기_해제_성공() {

        String email = "test@example.com";
        Member member = mock(Member.class);
        Hotel hotel = mock(Hotel.class);

        Favorite favorite = new Favorite(member, hotel);
        given(memberService.getValidMemberOrThrow(anyString()))
          .willReturn(member);
        given(hotelRepository.findByIdAndRegisterStatus(anyLong(), eq(RegisterStatus.VISIBLE)))
          .willReturn(Optional.of(hotel));
        given(favoriteRepository.findByMemberIdAndHotelId(member.getId(), hotel.getId()))
          .willReturn(Optional.of(favorite));

        hotelService.toggleFavorite(email, hotel.getId());

        verify(favoriteRepository)
          .findByMemberIdAndHotelId(member.getId(), hotel.getId());
        verify(hotel).removeFavorite(favorite);

    }

    @Test
    @WithMockUser
    public void 즐겨찾기_등록_실패_비유효계정() {

        String email = "test@example.com";
        Long hotelId = 1L;

        doThrow(new ApiException(ApiErrorCode.NOT_FOUND_MEMBER.getDescription()))
          .when(memberService).getValidMemberOrThrow(email);

        ApiException exception = assertThrows(ApiException.class, () -> {
            hotelService.toggleFavorite(email, hotelId);
        });

        assertEquals(ApiErrorCode.NOT_FOUND_MEMBER.getDescription(), exception.getMessage());

    }

    @Test
    @WithMockUser
    public void 즐겨찾기_등록_실패_없는호텔() {

        String email = "test@example.com";
        Long hotelId = 999L;

        given(hotelRepository.findByIdAndRegisterStatus(eq(hotelId), eq(RegisterStatus.VISIBLE)))
          .willReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            hotelService.toggleFavorite(email, hotelId);
        });

        assertEquals(ApiErrorCode.NOT_FOUND_HOTEL.getDescription(), exception.getMessage());

    }

}