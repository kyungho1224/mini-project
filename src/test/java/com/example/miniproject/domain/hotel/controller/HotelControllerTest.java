package com.example.miniproject.domain.hotel.controller;

import com.example.miniproject.domain.hotel.constant.*;
import com.example.miniproject.domain.hotel.dto.BasicOptions;
import com.example.miniproject.domain.hotel.dto.HotelDTO;
import com.example.miniproject.domain.hotel.entity.Hotel;
import com.example.miniproject.domain.hotel.service.HotelService;
import com.example.miniproject.domain.member.constant.MemberRole;
import com.example.miniproject.domain.member.constant.MemberStatus;
import com.example.miniproject.domain.member.entity.Member;
import com.example.miniproject.exception.ApiErrorCode;
import com.example.miniproject.exception.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@DisplayName("오더 컨트롤러 테스트")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@SpringBootTest
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HotelService hotelService;

    private Member member;
    private List<HotelDTO.Response> list;
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

        list = List.of(HotelDTO.Response.builder()
                .id(1L)
                .nation(request.getNation())
                .name(request.getName())
                .description(request.getDescription())
                .thumbnails(null)
                .notices(null)
                .basicOptions(request.getBasicOptions())
                .checkIn(request.getCheckIn())
                .checkOut(request.getCheckOut())
                .smokingRule(request.getSmokingRule())
                .petRule(request.getPetRule())
                .poolOpeningTime(request.getPoolOpeningTime())
                .poolClosingTime(request.getPoolOpeningTime())
                .activeStatus(request.getActiveStatus())
                .latitude(null)
                .longitude(null)
                .rooms(null)
                .build());

    }

    @Test
    @WithMockUser
    public void 전체_상품_조회_성공() throws Exception {

        Pageable pageable = PageRequest.of(0, 1);
        Page<HotelDTO.Response> responsePage = new PageImpl<>(list, pageable, list.size());

        given(hotelService.findAllVisibleHotels(any(Pageable.class)))
                .willReturn(responsePage);

        mockMvc.perform(get("/api/hotels")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content").isArray())
                .andExpect(jsonPath("$.result.content[0].name", is("hotelName")));

    }

    @Test
    @WithMockUser
    public void 전체_상품_카테고리_조회_성공() throws Exception {

        Pageable pageable = PageRequest.of(0, 1);
        Page<HotelDTO.Response> responsePage = new PageImpl<>(list, pageable, list.size());

        given(hotelService.findByNation(any(), any(Pageable.class)))
                .willReturn(responsePage);

        mockMvc.perform(get("/api/hotels/nation/{nation}", request.getNation())
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content").isArray())
                .andExpect(jsonPath("$.result.content[0].nation", is("PHILIPPINES")));

    }

    @Test
    @WithMockUser
    public void 전체_상품_검색어_호텔명_조회_성공() throws Exception {

        Pageable pageable = PageRequest.of(0, 1);
        Page<HotelDTO.Response> responsePage = new PageImpl<>(list, pageable, list.size());

        given(hotelService.findHotelsByNameAndVisible(anyString(), any(Pageable.class))).willReturn(responsePage);

        mockMvc.perform(get("/api/hotels/name/{name}", request.getName())
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content").isArray())
                .andExpect(jsonPath("$.result.content[0].name", is("hotelName")));

    }

    @Test
    @WithMockUser
    public void 전체_상품_호텔_개별상품_조회_성공() throws Exception {

        Long hotelId = 1L;
        HotelDTO.Response response = HotelDTO.Response.builder()
                .id(1L)
                .nation(request.getNation())
                .name(request.getName())
                .description(request.getDescription())
                .thumbnails(null)
                .notices(null)
                .basicOptions(request.getBasicOptions())
                .checkIn(request.getCheckIn())
                .checkOut(request.getCheckOut())
                .smokingRule(request.getSmokingRule())
                .petRule(request.getPetRule())
                .poolOpeningTime(request.getPoolOpeningTime())
                .poolClosingTime(request.getPoolOpeningTime())
                .activeStatus(request.getActiveStatus())
                .latitude(null)
                .longitude(null)
                .rooms(null)
                .build();

        given(hotelService.findHotelById(hotelId))
                .willReturn(response);

        mockMvc.perform(get("/api/hotels/{hotelId}", hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.name", is("hotelName")));

    }

    @Test
    @WithMockUser(username = "master@example.com")
    public void 마스터_호텔_등록_성공() throws Exception{

        String hotelRequestJson = objectMapper.writeValueAsString(request);

        String fileName = "test1.jpg";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                fileName,
                "image/jpeg",
                "image content".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/hotels")
                        .file(file)
                        .param("request", hotelRequestJson)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated());

        Mockito.verify(hotelService)
                .create(
                anyString(),
                any(HotelDTO.Request.class),
                any(MultipartFile[].class));

    }

    @Test
    @WithMockUser(username = "user@example.com")
    public void 마스터_호텔_등록_실패_권한없음() throws Exception{

        String hotelRequestJson = objectMapper.writeValueAsString(request);

        String fileName = "test1.jpg";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                fileName,
                "image/jpeg",
                "image content".getBytes(StandardCharsets.UTF_8));

        doThrow(new ApiException(ApiErrorCode.NO_PERMISSION.getDescription()))
                .when(hotelService).create(anyString(), any(HotelDTO.Request.class), any(MultipartFile[].class));

        mockMvc.perform(multipart("/api/hotels")
                        .file(file)
                        .param("request", hotelRequestJson)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.result").value(ApiErrorCode.NO_PERMISSION.getDescription()));

        verify(hotelService).create(anyString(), any(HotelDTO.Request.class), any(MultipartFile[].class));

    }

    @Test
    @WithMockUser(username = "master@example.com")
    public void 마스터_호텔_수정_데이터_성공() throws Exception {

        Long hotelId = 1L;

        mockMvc.perform(patch("/api/hotels/{hotelId}", hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(hotelService, times(1))
                .updateData(anyString(), anyLong(), any(HotelDTO.Request.class));

    }

    @Test
    @WithMockUser(username = "user@example.com")
    public void 마스터_호텔_수정_데이터_실패_권한없음() throws Exception{

        Long hotelId = 1L;

        doThrow(new ApiException(ApiErrorCode.NO_PERMISSION.getDescription()))
                .when(hotelService).updateData(anyString(), anyLong(), any(HotelDTO.Request.class));

        mockMvc.perform(patch("/api/hotels/{hotelId}", hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.result").value(ApiErrorCode.NO_PERMISSION.getDescription()));

        verify(hotelService).updateData(anyString(), anyLong(), any(HotelDTO.Request.class));

    }

    @Test
    @WithMockUser(username = "master@example.com")
    public void 마스터_호텔_수정_이미지_성공() throws Exception {

        Long hotelId = 1L;
        Long thumbnailId = 1L;
        String fileName = "test1.jpg";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                fileName,
                "image/jpeg",
                "image content".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/hotels/{hotelId}/thumbnails/{thumbnailId}", hotelId, thumbnailId)
                        .file(file)
                        .param("file", fileName)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                )
                .andExpect(status().isNoContent());

        verify(hotelService, times(1))
                .updateThumbnail(anyString(), anyLong(), anyLong(), any(MultipartFile.class));

    }

    @Test
    @WithMockUser(username = "user@example.com")
    public void 마스터_호텔_수정_이미지_실패_권한없음() throws Exception{

        Long hotelId = 1L;
        Long thumbnailId = 1L;
        String fileName = "test1.jpg";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                fileName,
                "image/jpeg",
                "image content".getBytes(StandardCharsets.UTF_8));

        doThrow(new ApiException(ApiErrorCode.NO_PERMISSION.getDescription()))
                .when(hotelService).updateThumbnail(anyString(), anyLong(), anyLong(), any(MultipartFile.class));

        mockMvc.perform(multipart("/api/hotels/{hotelId}/thumbnails/{thumbnailId}", hotelId, thumbnailId)
                        .file(file)
                        .param("file", fileName)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                )
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.result").value(ApiErrorCode.NO_PERMISSION.getDescription()));

        verify(hotelService).updateThumbnail(anyString(), anyLong(), anyLong(), any(MultipartFile.class));

    }

    @Test
    @WithMockUser(username = "master@example.com")
    public void 마스터_호텔_삭제_성공() throws Exception {

        Long hotelId = 1L;

        doNothing().when(hotelService).unregister(member.getEmail(), hotelId);

        mockMvc.perform(delete("/api/hotels/{hotelId}", hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(hotelService).unregister(member.getEmail(), hotelId);

    }

    @Test
    @WithMockUser(username = "user@example.com")
    public void 마스터_호텔_삭제_실패_권한없음() throws Exception{

        Long hotelId = 1L;

        doThrow(new ApiException(ApiErrorCode.NO_PERMISSION.getDescription()))
                .when(hotelService).unregister(anyString(), anyLong());

        mockMvc.perform(delete("/api/hotels/{hotelId}", hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.result").value(ApiErrorCode.NO_PERMISSION.getDescription()));

        verify(hotelService).unregister(anyString(), anyLong());

    }

    @Test
    @WithMockUser(username = "favoriteTest@example.com")
    public void 호텔_즐겨찾기_등록취소_토글_성공() throws Exception{
        Long hotelId = 1L;

        doNothing().when(hotelService).toggleFavorite("favoriteTest@example.com", hotelId);

        mockMvc.perform(post("/api/hotels//{hotelId}/favorite", hotelId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(hotelService).toggleFavorite("favoriteTest@example.com", hotelId);

    }

    @Test
    @WithMockUser(username = "favoriteTest@example.com")
    public void 호텔_즐겨찾기_등록취소_토글_실패_없는_호텔() throws Exception{
        Long hotelId = 1L;

        doThrow(new ApiException(ApiErrorCode.NOT_FOUND_HOTEL.getDescription()))
                .when(hotelService).toggleFavorite(anyString(), anyLong());

        mockMvc.perform(post("/api/hotels//{hotelId}/favorite", hotelId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.result").value(ApiErrorCode.NOT_FOUND_HOTEL.getDescription()));

        verify(hotelService).toggleFavorite("favoriteTest@example.com", hotelId);

    }

}