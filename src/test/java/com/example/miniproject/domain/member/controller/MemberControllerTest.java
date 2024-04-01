package com.example.miniproject.domain.member.controller;

import com.example.miniproject.domain.hotel.constant.Nation;
import com.example.miniproject.domain.hotel.constant.PetRule;
import com.example.miniproject.domain.hotel.constant.SmokingRule;
import com.example.miniproject.domain.hotel.dto.HotelDTO;
import com.example.miniproject.domain.hotel.entity.Hotel;
import com.example.miniproject.domain.member.constant.MemberRole;
import com.example.miniproject.domain.member.constant.MemberStatus;
import com.example.miniproject.domain.member.dto.MemberDTO;
import com.example.miniproject.domain.member.service.MemberService;
import com.example.miniproject.domain.order.dto.OrderDTO;
import com.example.miniproject.exception.ApiErrorCode;
import com.example.miniproject.exception.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("멤버 컨트롤러 테스트")
@ActiveProfiles("default")
@AutoConfigureMockMvc
@Transactional
@SpringBootTest
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void 회원가입_요청_성공() throws Exception {
        String email = "test@gmail.com";
        String password = "password";
        String name = "username";
        String birth = "20001111";
        MemberDTO.JoinRequest request = MemberDTO.JoinRequest.builder()
            .email(email)
            .password(password)
            .name(name)
            .birth(birth)
            .build();

        given(memberService.create(any(MemberDTO.JoinRequest.class)))
            .willReturn(MemberDTO.JoinResponse.builder()
                .id(1L)
                .email(request.getEmail())
                .status(MemberStatus.NOT_CERTIFICATED)
                .role(MemberRole.USER)
                .build());

        mockMvc.perform(post("/api/members/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.result.email").value(request.getEmail()));
    }

    @Test
    public void 회원가입_요청_실패_중복_이메일() throws Exception {
        String email = "test@gmail.com";
        String password = "password";
        String name = "username";
        String birth = "20001111";
        MemberDTO.JoinRequest request = MemberDTO.JoinRequest.builder()
            .email(email)
            .password(password)
            .name(name)
            .birth(birth)
            .build();

        given(memberService.create(any(MemberDTO.JoinRequest.class)))
            .willThrow(new ApiException(ApiErrorCode.DUPLICATED_EMAIL.getDescription()));

        mockMvc.perform(post("/api/members/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is5xxServerError())
            .andExpect(jsonPath("$.result").value(ApiErrorCode.DUPLICATED_EMAIL.getDescription()));
    }

    @Test
    public void 회원가입_요청_실패_이메일_누락() throws Exception {
        MemberDTO.JoinRequest request = MemberDTO.JoinRequest.builder()
            .password("password")
            .name("username")
            .birth("20001111")
            .build();
        given(memberService.create(request)).willThrow(new ApiException("이메일은 필수입니다"));

        mockMvc.perform(post("/api/members/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.result").value("이메일은 필수입니다"));
    }

    @Test
    public void 회원가입_요청_실패_비밀번호_누락() throws Exception {
        MemberDTO.JoinRequest request = MemberDTO.JoinRequest.builder()
            .email("test@gmail.com")
            .name("username")
            .birth("20001111")
            .build();
        given(memberService.create(request)).willThrow(new ApiException("비밀번호는 필수입니다"));

        mockMvc.perform(post("/api/members/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.result").value("비밀번호는 필수입니다"));
    }

    @Test
    public void 회원가입_요청_실패_이름_누락() throws Exception {
        MemberDTO.JoinRequest request = MemberDTO.JoinRequest.builder()
            .email("test@gmail.com")
            .password("password1234")
            .birth("20001111")
            .build();
        given(memberService.create(request)).willThrow(new ApiException("이름은 필수입니다"));

        mockMvc.perform(post("/api/members/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.result").value("이름은 필수입니다"));
    }

    @Test
    public void 회원가입_요청_실패_생년월일_누락() throws Exception {
        MemberDTO.JoinRequest request = MemberDTO.JoinRequest.builder()
            .email("test@gmail.com")
            .password("password1234")
            .name("username")
            .build();
        given(memberService.create(request)).willThrow(new ApiException("생년월일은 필수입니다"));

        mockMvc.perform(post("/api/members/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.result").value("생년월일은 필수입니다"));
    }

    @Test
    public void 회원가입_요청_실패_생년월일_자리수_오류_7자리() throws Exception {
        MemberDTO.JoinRequest request = MemberDTO.JoinRequest.builder()
            .email("test@gmail.com")
            .password("password1234")
            .name("username")
            .birth("2000111")
            .build();
        given(memberService.create(request)).willThrow(new ApiException("8자리(yyyyMMdd) 숫자를 입력해주세요"));

        mockMvc.perform(post("/api/members/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.result").value("8자리(yyyyMMdd) 숫자를 입력해주세요"));
    }

    @Test
    public void 회원가입_요청_실패_생년월일_자리수_오류_9자리() throws Exception {
        MemberDTO.JoinRequest request = MemberDTO.JoinRequest.builder()
            .email("test@gmail.com")
            .password("password1234")
            .name("username")
            .birth("200011112")
            .build();
        given(memberService.create(request)).willThrow(new ApiException("8자리(yyyyMMdd) 숫자를 입력해주세요"));

        mockMvc.perform(post("/api/members/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.result").value("8자리(yyyyMMdd) 숫자를 입력해주세요"));
    }

    @Test
    public void 회원가입_요청_실패_이메일_형식_오류() throws Exception {
        MemberDTO.JoinRequest request = MemberDTO.JoinRequest.builder()
            .email("test gmail.com")
            .password("password")
            .name("username")
            .birth("20001111")
            .build();

        given(memberService.create(request)).willThrow(new ApiException("이메일 형식에 맞지 않습니다"));

        mockMvc.perform(post("/api/members/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.result").value("이메일 형식에 맞지 않습니다"));
    }

    @Test
    public void 회원_인증_요청_성공() throws Exception {
        String uuid = "some-uuid";

        mockMvc.perform(get("/api/members/verify")
                .param("uuid", uuid)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isAccepted());

        verify(memberService, times(1)).updateCertificate(uuid);
    }

    @Test
    public void 로그인_요청_성공() throws Exception {
        String email = "test@gmail.com";
        String password = "password";
        String encodedPassword = "encoded-password";
        String name = "username";
        String birth = "20001111";
        MemberStatus status = MemberStatus.CERTIFICATED;
        MemberRole role = MemberRole.USER;
        BigDecimal credit = BigDecimal.valueOf(1000000);
        String accessToke = "some-token";

        MemberDTO.LoginRequest request = MemberDTO.LoginRequest.builder()
            .email(email)
            .password(password)
            .build();

        given(passwordEncoder.matches(password, encodedPassword)).willReturn(true);
        given(memberService.login(any(MemberDTO.LoginRequest.class)))
            .willReturn(MemberDTO.LoginResponse.builder()
                .id(1L)
                .email(email)
                .name(name)
                .birth(birth)
                .status(status)
                .role(role)
                .credit(credit)
                .accessToken(accessToke)
                .build());

        mockMvc.perform(post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.email").value(request.getEmail()))
            .andExpect(jsonPath("$.result.name").value(name))
            .andExpect(jsonPath("$.result.access_token").value("some-token"));
    }

    @Test
    public void 로그인_요청_실패_이메일_누락() throws Exception {
        String email = "";
        String password = "password";

        MemberDTO.LoginRequest request = MemberDTO.LoginRequest.builder()
            .email(email)
            .password(password)
            .build();

        given(memberService.login(request)).willThrow(new ApiException("이메일은 필수입니다"));

        mockMvc.perform(post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.result").value("이메일은 필수입니다"));
    }

    @Test
    public void 로그인_요청_실패_이메일_형식_오류() throws Exception {
        String email = "test.gmail.com";
        String password = "password";

        MemberDTO.LoginRequest request = MemberDTO.LoginRequest.builder()
            .email(email)
            .password(password)
            .build();

        given(memberService.login(request)).willThrow(new ApiException("이메일 형식에 맞지 않습니다"));

        mockMvc.perform(post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.result").value("이메일 형식에 맞지 않습니다"));
    }

    @Test
    public void 로그인_요청_실패_비밀번호_누락() throws Exception {
        String email = "test@gmail.com";
        String password = "";

        MemberDTO.LoginRequest request = MemberDTO.LoginRequest.builder()
            .email(email)
            .password(password)
            .build();

        given(memberService.login(request)).willThrow(new ApiException("비밀번호는 필수입니다"));

        mockMvc.perform(post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.result").value("비밀번호는 필수입니다"));
    }

    @Test
    public void 로그인_요청_실패_가입된_이메일_없음() throws Exception {
        String email = "test@naver.com";
        String password = "password";

        MemberDTO.LoginRequest request = MemberDTO.LoginRequest.builder()
            .email(email)
            .password(password)
            .build();

        given(memberService.login(any(MemberDTO.LoginRequest.class)))
            .willThrow(new ApiException(ApiErrorCode.NOT_FOUND_MEMBER.getDescription()));

        mockMvc.perform(post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is5xxServerError())
            .andExpect(jsonPath("$.result").value(ApiErrorCode.NOT_FOUND_MEMBER.getDescription()));
    }

    @Test
    public void 로그인_요청_실패_비밀번호_틀림() throws Exception {
        String email = "test@gmail.com";
        String password = "password";
        String encodedPassword = "encoded-password";

        MemberDTO.LoginRequest request = MemberDTO.LoginRequest.builder()
            .email(email)
            .password(password)
            .build();

        given(passwordEncoder.matches(password, encodedPassword)).willReturn(false);
        given(memberService.login(any(MemberDTO.LoginRequest.class)))
            .willThrow(new ApiException(ApiErrorCode.NOT_MATCH_PASSWORD.getDescription()));

        mockMvc.perform(post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is5xxServerError())
            .andExpect(jsonPath("$.result").value(ApiErrorCode.NOT_MATCH_PASSWORD.getDescription()));
    }

    @Test
    @WithMockUser
    public void 사용자_프로필_이미지_업로드_성공() throws Exception {
        String filename = "test.jpg";
        MockMultipartFile file = new MockMultipartFile(
            "file",
            filename,
            "image/jpeg",
            "image content".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/members/upload")
                .file(file))
            .andExpect(status().isOk());

        verify(memberService, times(1))
            .uploadProfile(anyString(), any(MultipartFile.class));
    }

    @Test
    @WithAnonymousUser
    public void 사용자_프로필_이미지_업로드_실패_비인증_상태() throws Exception {
        String filename = "test.jpg";
        MockMultipartFile file = new MockMultipartFile(
            "file",
            filename,
            "image/jpeg",
            "image content".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/members/upload")
                .file(file))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "certificated user")
    public void 사용자_프로필_이미지_업로드_실패_파일_없음() throws Exception {
        String filename = "test.jpg";
        MockMultipartFile file = new MockMultipartFile(
            "null",
            filename,
            "image/jpeg",
            "image content".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/members/upload")
                .file(file))
            .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    public void 마이페이지_사용자_정보_조회_성공() throws Exception {
        Long id = 1L;
        String profileImage = "profile-image.jpg";
        String name = "username";
        String email = "test@gmail.com";
        String birth = "20001111";
        MemberStatus status = MemberStatus.CERTIFICATED;
        MemberRole role = MemberRole.USER;
        String address = "구월말로58번길 15-19";
        String city = "인천광역시";
        String nation = "대한민국";
        String zipCode = "21535";
        BigDecimal credit = BigDecimal.valueOf(1000000);

        given(memberService.getMyPageInfo(email))
            .willReturn(MemberDTO.DetailResponse.builder()
                .id(id)
                .profileImage(profileImage)
                .name(name)
                .email(email)
                .birth(birth)
                .status(status)
                .role(role)
                .address(address)
                .city(city)
                .nation(nation)
                .zipCode(zipCode)
                .credit(credit)
                .build());

        mockMvc.perform(get("/api/members/my-info")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.id").value(id))
            .andExpect(jsonPath("$.result.profile_image").value(profileImage))
            .andExpect(jsonPath("$.result.name").value(name))
            .andExpect(jsonPath("$.result.email").value(email))
            .andExpect(jsonPath("$.result.birth").value(birth))
            .andExpect(jsonPath("$.result.status").value(status.toString()))
            .andExpect(jsonPath("$.result.role").value(role.toString()))
            .andExpect(jsonPath("$.result.address").value(address))
            .andExpect(jsonPath("$.result.city").value(city))
            .andExpect(jsonPath("$.result.nation").value(nation))
            .andExpect(jsonPath("$.result.zip_code").value(zipCode))
            .andExpect(jsonPath("$.result.credit").value(credit));
    }

    @Test
    @WithAnonymousUser
    public void 마이페이지_사용자_정보_조회_실패_비인증_상태() throws Exception {
        mockMvc.perform(get("/api/members/my-info")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    public void 마이페이지_사용자_정보_조회_실패_다른_사용자() throws Exception {
        String email = "test@gmail.com";

        given(memberService.getMyPageInfo(email))
            .willThrow(new ApiException(ApiErrorCode.NOT_FOUND_MEMBER.getDescription()));

        mockMvc.perform(get("/api/members/my-info")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().is5xxServerError());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    public void 마이페이지_사용자_정보_변경_성공() throws Exception {
        String password = "password";
        String encodedPassword = "encoded-password";
        String address = "address";
        String city = "city";
        String nation = "nation";
        String zipCode = "zip-code";

        given(passwordEncoder.matches(password, encodedPassword)).willReturn(true);
        MemberDTO.UpdateMemberRequest request = MemberDTO.UpdateMemberRequest.builder()
            .password(password)
            .address(address)
            .city(city)
            .nation(nation)
            .zipCode(zipCode)
            .build();

        mockMvc.perform(patch("/api/members/my-info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk());

        verify(memberService, times(1))
            .updateMemberInfo(anyString(), any(MemberDTO.UpdateMemberRequest.class));
    }

    @Test
    @WithAnonymousUser
    public void 마이페이지_사용자_정보_변경_실패_비인증_상태() throws Exception {
        mockMvc.perform(patch("/api/members/my-info")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", password = "password1")
    public void 마이페이지_사용자_정보_변경_실패_비밀번호_틀림() throws Exception {
        String correctPassword = "password1";
        String incorrectPassword = "password2";
        String address = "address";
        String city = "city";
        String nation = "nation";
        String zipCode = "zip-code";

        MemberDTO.UpdateMemberRequest request = MemberDTO.UpdateMemberRequest.builder()
            .password(incorrectPassword)
            .address(address)
            .city(city)
            .nation(nation)
            .zipCode(zipCode)
            .build();

        doThrow(new ApiException(ApiErrorCode.NOT_MATCH_PASSWORD.getDescription()))
            .when(memberService)
            .updateMemberInfo(anyString(), any(MemberDTO.UpdateMemberRequest.class));

        mockMvc.perform(patch("/api/members/my-info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is5xxServerError())
            .andExpect(jsonPath("$.result").value(ApiErrorCode.NOT_MATCH_PASSWORD.getDescription()));

        verify(memberService, times(1))
            .updateMemberInfo(anyString(), any(MemberDTO.UpdateMemberRequest.class));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    public void 사용자_즐겨찾는_숙소_목록_가져오기_성공() throws Exception {
        Hotel hotel1 = Hotel.builder()
            .nation(Nation.TAIWAN)
            .name("name1")
            .description("description1")
            .checkIn(LocalTime.of(14, 0))
            .checkOut(LocalTime.of(11, 0))
            .smokingRule(SmokingRule.FULL_AVAILABLE)
            .petRule(PetRule.FULL_AVAILABLE)
            .build();
        Hotel hotel2 = Hotel.builder()
            .nation(Nation.MALAYSIA)
            .name("name2")
            .description("description2")
            .checkIn(LocalTime.of(14, 30))
            .checkOut(LocalTime.of(11, 30))
            .smokingRule(SmokingRule.SOME_POSSIBLE)
            .petRule(PetRule.SOME_POSSIBLE)
            .build();
        Hotel hotel3 = Hotel.builder()
            .nation(Nation.PHILIPPINES)
            .name("name3")
            .description("description3")
            .checkIn(LocalTime.of(15, 20))
            .checkOut(LocalTime.of(13, 20))
            .smokingRule(SmokingRule.TOTAL_IMPOSSIBLE)
            .petRule(PetRule.TOTAL_IMPOSSIBLE)
            .build();

        List<HotelDTO.Response> responses = List.of(
            HotelDTO.Response.of(hotel1),
            HotelDTO.Response.of(hotel2),
            HotelDTO.Response.of(hotel3)
        );
        Pageable pageable = PageRequest.of(0, 10);
        Page<HotelDTO.Response> responsePage = new PageImpl<>(responses, pageable, responses.size());

        given(memberService.getMyFavoriteList(anyString(), any(Pageable.class))).willReturn(responsePage);

        mockMvc.perform(get("/api/members/my-favorite")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content").isArray())
            .andExpect(jsonPath("$.result.content[0].nation").value(Nation.TAIWAN.name()))
            .andExpect(jsonPath("$.result.content[1].nation").value(Nation.MALAYSIA.name()))
            .andExpect(jsonPath("$.result.content[2].nation").value(Nation.PHILIPPINES.name()))
            .andExpect(jsonPath("$.result.content[0].name").value("name1"))
            .andExpect(jsonPath("$.result.content[1].name").value("name2"))
            .andExpect(jsonPath("$.result.content[2].name").value("name3"))
            .andExpect(jsonPath("$.result.content[0].smoking_rule").value(SmokingRule.FULL_AVAILABLE.name()))
            .andExpect(jsonPath("$.result.content[1].smoking_rule").value(SmokingRule.SOME_POSSIBLE.name()))
            .andExpect(jsonPath("$.result.content[2].smoking_rule").value(SmokingRule.TOTAL_IMPOSSIBLE.name()))
            .andExpect(jsonPath("$.result.content[0].pet_rule").value(PetRule.FULL_AVAILABLE.name()))
            .andExpect(jsonPath("$.result.content[1].pet_rule").value(PetRule.SOME_POSSIBLE.name()))
            .andExpect(jsonPath("$.result.content[2].pet_rule").value(PetRule.TOTAL_IMPOSSIBLE.name()))
            .andExpect(jsonPath("$.result.content[0].check_in").value(hotel1.getCheckIn().toString()))
            .andExpect(jsonPath("$.result.content[1].check_in").value(hotel2.getCheckIn().toString()))
            .andExpect(jsonPath("$.result.content[2].check_in").value(hotel3.getCheckIn().toString()))
            .andExpect(jsonPath("$.result.content[0].check_out").value(hotel1.getCheckOut().toString()))
            .andExpect(jsonPath("$.result.content[1].check_out").value(hotel2.getCheckOut().toString()))
            .andExpect(jsonPath("$.result.content[2].check_out").value(hotel3.getCheckOut().toString()));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    public void 사용자_즐겨찾는_숙소_목록_가져오기_성공_목록_없음() throws Exception {
        List<HotelDTO.Response> responses = List.of();
        Pageable pageable = PageRequest.of(0, 1);
        Page<HotelDTO.Response> responsePage = new PageImpl<>(responses, pageable, responses.size());

        given(memberService.getMyFavoriteList(anyString(), any(Pageable.class))).willReturn(responsePage);

        mockMvc.perform(get("/api/members/my-favorite")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content").isEmpty());
    }

    @Test
    @WithAnonymousUser
    public void 사용자_즐겨찾는_숙소_목록_가져오기_실패_비인증_상태() throws Exception {
        mockMvc.perform(get("/api/members/favorites")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    public void 사용자_장바구니_가져오기_성공() throws Exception {
        List<OrderDTO.OrderDetailResponse> responses = List.of(
            OrderDTO.OrderDetailResponse.builder()
                .id(1L)
                .totalPrice(BigDecimal.valueOf(50000))
                .build(),
            OrderDTO.OrderDetailResponse.builder()
                .id(2L)
                .totalPrice(BigDecimal.valueOf(90000))
                .build()
        );
        Pageable pageable = PageRequest.of(0, 1);
        Page<OrderDTO.OrderDetailResponse> responsePage = new PageImpl<>(responses, pageable, responses.size());

        given(memberService.getMyCartList(anyString(), any(Pageable.class))).willReturn(responsePage);

        mockMvc.perform(get("/api/members/my-cart")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content").isArray())
            .andExpect(jsonPath("$.result.content").isNotEmpty())
            .andExpect(jsonPath("$.result.content[0].id").value(1L))
            .andExpect(jsonPath("$.result.content[1].id").value(2L))
            .andExpect(jsonPath("$.result.content[0].total_price").value(responses.get(0).getTotalPrice()))
            .andExpect(jsonPath("$.result.content[1].total_price").value(responses.get(1).getTotalPrice()));
    }

    @Test
    @WithMockUser
    public void 사용자_장바구니_가져오기_성공_목록_없음() throws Exception {
        List<OrderDTO.OrderDetailResponse> responses = List.of();
        Pageable pageable = PageRequest.of(0, 1);
        Page<OrderDTO.OrderDetailResponse> responsePage = new PageImpl<>(responses, pageable, responses.size());

        given(memberService.getMyCartList(anyString(), any(Pageable.class))).willReturn(responsePage);

        mockMvc.perform(get("/api/members/my-cart")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content").isArray())
            .andExpect(jsonPath("$.result.content").isEmpty());
    }

    @Test
    @WithAnonymousUser
    public void 사용자_장바구니_가져오기_실패_비인증_상태() throws Exception {
        mockMvc.perform(get("/api/members/my-cart")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    public void 사용자_장바구니_아이템_삭제_성공() throws Exception {
        String email = "test@gmail.com";
        Long orderId = 1L;
        doNothing().when(memberService).removeCartItem(email, orderId);

        mockMvc.perform(patch("/api/members/my-cart/{orderId}", orderId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithAnonymousUser
    public void 사용자_장바구니_아이템_삭제_실패_비인증_상태() throws Exception {
        Long orderId = 1L;

        mockMvc.perform(patch("/api/members/my-cart/{orderId}", orderId)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    public void 사용자_장바구니_아이템_삭제_실패_없는_아이템_요청() throws Exception {
        Long orderId = 1L;
        doThrow(new ApiException(ApiErrorCode.NOT_FOUND_ORDER.getDescription()))
            .when(memberService).removeCartItem(anyString(), anyLong());

        mockMvc.perform(patch("/api/members/my-cart/{orderId}", orderId)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().is5xxServerError())
            .andExpect(jsonPath("$.result").value(ApiErrorCode.NOT_FOUND_ORDER.getDescription()));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    public void 사용자_주문_목록_가져오기_성공() throws Exception {
        String email = "test@gmail.com";
        MemberDTO.SimpleResponse simpleMember = MemberDTO.SimpleResponse.builder()
            .id(1L)
            .email(email)
            .name("username1")
            .build();

        List<OrderDTO.OrderDetailResponse> detailResponses = List.of(
            OrderDTO.OrderDetailResponse.builder()
                .id(1L)
                .member(simpleMember)
                .build(),
            OrderDTO.OrderDetailResponse.builder()
                .id(2L)
                .member(simpleMember)
                .build()
        );

        Pageable pageable = PageRequest.of(0, 2);
        Page<OrderDTO.OrderDetailResponse> responsePage = new PageImpl<>(detailResponses, pageable, detailResponses.size());

        given(memberService.getMyOrderList(eq(email), any(Pageable.class))).willReturn(responsePage);

        mockMvc.perform(get("/api/members/my-order")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content").isNotEmpty())
            .andExpect(jsonPath("$.result.content").isArray())
            .andExpect(jsonPath("$.result.content", hasSize(2)))
            .andExpect(jsonPath("$.result.content[0].id").value(1L))
            .andExpect(jsonPath("$.result.content[1].id").value(2L))
            .andExpect(jsonPath("$.result.content[0].member.email", is(email)))
            .andExpect(jsonPath("$.result.content[1].member.email", is(email)));
    }

    @Test
    @WithAnonymousUser
    public void 사용자_주문_목록_가져오기_실패_비인증_상태() throws Exception {
        mockMvc.perform(get("/api/members/my-order")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().is3xxRedirection());
    }

}