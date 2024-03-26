package com.example.miniproject.domain.member.service;

import com.example.miniproject.common.service.ImageService;
import com.example.miniproject.domain.hotel.repository.FavoriteRepository;
import com.example.miniproject.domain.member.constant.MemberStatus;
import com.example.miniproject.domain.member.dto.MemberDTO;
import com.example.miniproject.domain.member.dto.TokenDTO;
import com.example.miniproject.domain.member.entity.Member;
import com.example.miniproject.domain.member.repository.MemberCacheRepository;
import com.example.miniproject.domain.member.repository.MemberRepository;
import com.example.miniproject.domain.order.repository.OrderRepository;
import com.example.miniproject.exception.ApiErrorCode;
import com.example.miniproject.exception.ApiException;
import com.example.miniproject.util.JwtTokenUtil;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DisplayName("멤버 서비스 테스트")
@ActiveProfiles("test")
@Transactional
@SpringBootTest
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private ImageService imageService;

    @Mock
    MemberCacheRepository memberCacheRepository;

    private MemberService memberService;

    @BeforeEach
    public void before() {
        memberService = new MemberService(
            orderRepository,
            favoriteRepository,
            memberRepository,
            passwordEncoder,
            mailSender,
            jwtTokenUtil,
            imageService,
            memberCacheRepository
        );
    }

    @Test
    public void 회원가입_요청_성공() throws Exception {
        String email = "test@gmail.com";
        String password = "raw-password";
        String encodedPassword = "encoded-password";
        String name = "username1";
        String birth = "20000101";
        MemberDTO.JoinRequest request = MemberDTO.JoinRequest.builder()
            .email(email)
            .password(password)
            .name(name)
            .birth(birth)
            .build();

        given(memberRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(password)).willReturn(encodedPassword);
        given(mailSender.createMimeMessage()).willReturn(mock(MimeMessage.class));

        MemberDTO.JoinResponse joinResponse = memberService.create(request);

        verify(passwordEncoder, times(1)).encode(password);
        verify(memberRepository, times(1)).save(any(Member.class));
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
        assertEquals(request.getEmail(), joinResponse.getEmail());
    }

    @Test
    public void 회원가입_요청_실패_중복_이메일() {
        String email = "test@gmail.com";
        String password = "raw-password";
        String encodedPassword = "encoded-password";
        String name = "username1";
        String birth = "20000101";
        MemberDTO.JoinRequest request = MemberDTO.JoinRequest.builder()
            .email(email)
            .password(password)
            .name(name)
            .birth(birth)
            .build();

        given(memberRepository.existsByEmail(anyString())).willReturn(true);
        given(passwordEncoder.encode(password)).willReturn(encodedPassword);
        given(mailSender.createMimeMessage()).willReturn(mock(MimeMessage.class));

        ApiException apiException =
            assertThrows(ApiException.class, () -> memberService.create(request));

        assertEquals(ApiErrorCode.DUPLICATED_EMAIL.getDescription(), apiException.getMessage());
    }

    @Test
    public void 회원_인증_요청_성공() {
        String email = "test@gmail.com";
        String password = "raw-password";
        String encodedPassword = "encoded-password";

        Member member = Member.builder()
            .email(email)
            .password(encodedPassword)
            .build();

        given(memberRepository.findByUuid(anyString())).willReturn(Optional.of(member));
        memberService.updateCertificate("some-uuid");

        assertEquals(MemberStatus.CERTIFICATED, member.getStatus());
        verify(memberRepository, times(1)).findByUuid("some-uuid");
    }

    @Test
    public void 회원_인증_요청_실패_uuid_다름() {
        given(memberRepository.findByUuid(anyString()))
            .willThrow(new ApiException(ApiErrorCode.NOT_FOUND_MEMBER.getDescription()));

        ApiException apiException =
            assertThrows(ApiException.class, () -> memberService.updateCertificate("some-uuid"));

        assertEquals(ApiErrorCode.NOT_FOUND_MEMBER.getDescription(), apiException.getErrorDescription());
    }

    @Test
    public void 로그인_요청_성공() {
        String email = "test@gmail.com";
        String password = "raw-password";
        String encodedPassword = "encoded-password";
        MemberDTO.LoginRequest request = MemberDTO.LoginRequest.builder()
            .email(email)
            .password(password)
            .build();
        Member member = Member.builder()
            .email(email)
            .password(encodedPassword)
            .build();

        given(memberRepository.findByEmailAndStatus(anyString(), any(MemberStatus.class))).willReturn(Optional.of(member));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(jwtTokenUtil.generatedToken(anyString())).willReturn(new TokenDTO("access-token", "refresh-token"));

        MemberDTO.LoginResponse loginResponse = memberService.login(request);

        verify(memberRepository, times(1)).findByEmailAndStatus(anyString(), any(MemberStatus.class));
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(jwtTokenUtil, times(1)).generatedToken(anyString());
        assertEquals(request.getEmail(), loginResponse.getEmail());
        assertEquals("access-token", loginResponse.getAccessToken());
    }

    @Test
    public void 로그인_요청_실패_가입된_이메일_없음() {
        String email = "test@gmail.com";
        String password = "raw-password";
        String encodedPassword = "encoded-password";
        MemberDTO.LoginRequest request = MemberDTO.LoginRequest.builder()
            .email(email)
            .password(password)
            .build();

        given(memberRepository.findByEmailAndStatus(anyString(), any(MemberStatus.class)))
            .willThrow(new ApiException(ApiErrorCode.NOT_FOUND_MEMBER.getDescription()));

        ApiException apiException =
            assertThrows(ApiException.class, () -> memberService.login(request));

        assertEquals(ApiErrorCode.NOT_FOUND_MEMBER.getDescription(), apiException.getErrorDescription());
    }

    @Test
    public void 로그인_요청_실패_비밀번호_틀림() {
        String email = "test@gmail.com";
        String password = "raw-password";
        String encodedPassword = "encoded-password";
        MemberDTO.LoginRequest request = MemberDTO.LoginRequest.builder()
            .email(email)
            .password(password)
            .build();
        Member member = Member.builder()
            .email(email)
            .password(encodedPassword)
            .build();

        given(memberRepository.findByEmailAndStatus(anyString(), any(MemberStatus.class)))
            .willReturn(Optional.of(member));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        ApiException apiException =
            assertThrows(ApiException.class, () -> memberService.login(request));

        assertEquals(ApiErrorCode.NOT_MATCH_PASSWORD.getDescription(), apiException.getErrorDescription());
    }

    @Test
    public void 사용자_프로필_이미지_업로드_성공() throws Exception {
        String email = "test@gmail.com";
        String imgUrl = "img-url";

        Member member = mock(Member.class);
        MultipartFile file = mock(MultipartFile.class);

        doAnswer(args -> {
            Object argument = args.getArgument(0);
            given(member.getProfileImage()).willReturn((String) argument);
            return null;
        }).when(member).updateProfileImage(anyString());

        given(memberCacheRepository.getMember(email)).willReturn(Optional.of(member));
        given(imageService.upload(any(), anyString())).willReturn(imgUrl);

        memberService.uploadProfile(email, file);

        assertEquals(imgUrl, member.getProfileImage());
    }

    @Test
    public void 사용자_프로필_이미지_업로드_실패_파일_오류() throws Exception {
        String email = "test@gmail.com";

        Member member = mock(Member.class);
        MultipartFile file = mock(MultipartFile.class);

        given(memberCacheRepository.getMember(email)).willReturn(Optional.of(member));
        given(imageService.upload(any(), anyString()))
            .willThrow(new ApiException(ApiErrorCode.FIREBASE_EXCEPTION.getDescription()));

        ApiException apiException =
            assertThrows(ApiException.class, () -> memberService.uploadProfile(email, file));

        assertEquals(ApiErrorCode.FIREBASE_EXCEPTION.getDescription(), apiException.getErrorDescription());
    }

    @Test
    public void 마이페이지_사용자_정보_조회_성공() {
        String email = "test@gmail.com";
        String encodedPassword = "encoded-password";
        Member member = Member.builder()
            .email(email)
            .password(encodedPassword)
            .build();

        given(memberCacheRepository.getMember(email)).willReturn(Optional.of(member));

        MemberDTO.DetailResponse myPageInfo = memberService.getMyPageInfo(email);
        assertEquals(email, myPageInfo.getEmail());
    }

    @Test
    public void 마이페이지_사용자_정보_조회_실패_다른_사용자() {
        String email = "test@gmail.com";
        String encodedPassword = "encoded-password";
        Member member = Member.builder()
            .email(email)
            .password(encodedPassword)
            .build();
        Member otherMember = Member.builder()
            .email("test@naver.com")
            .password("password123")
            .build();

        given(memberCacheRepository.getMember(email)).willReturn(Optional.of(member));
        memberService.getMyPageInfo(email);

        ApiException apiException =
            assertThrows(ApiException.class, () -> memberService.getMyPageInfo(otherMember.getEmail()));
        assertEquals(ApiErrorCode.NOT_FOUND_MEMBER.getDescription(), apiException.getErrorDescription());
    }

}