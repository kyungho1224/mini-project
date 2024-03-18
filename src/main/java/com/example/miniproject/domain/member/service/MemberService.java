package com.example.miniproject.domain.member.service;

import com.example.miniproject.domain.member.constant.MemberStatus;
import com.example.miniproject.domain.member.dto.MemberDTO;
import com.example.miniproject.domain.member.dto.TokenDTO;
import com.example.miniproject.domain.member.entity.Member;
import com.example.miniproject.domain.member.repository.MemberRepository;
import com.example.miniproject.exception.ApiErrorCode;
import com.example.miniproject.exception.ApiException;
import com.example.miniproject.util.JwtTokenUtil;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final JwtTokenUtil jwtTokenUtil;

    @Value("${spring.mail.username}")
    private String mailSenderUsername;

    public MemberDTO.JoinResponse create(MemberDTO.JoinRequest request) throws Exception {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(ApiErrorCode.DUPLICATED_EMAIL);
        }

        String uuid = UUID.randomUUID().toString();
        Member newMember = Member.saveAs(
          request.getEmail(),
          passwordEncoder.encode(request.getPassword()),
          request.getName(),
          request.getBirth(),
          uuid);
        sendMail(newMember, uuid);
        memberRepository.save(newMember);
        return MemberDTO.JoinResponse.of(newMember);
    }

    public void updateCertificate(String uuid) {
        memberRepository.findByUuid(uuid)
          .map(member -> {
              member.updateStatus(MemberStatus.CERTIFICATED);
              return member;
          })
          .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_MEMBER));
    }

    public MemberDTO.LoginResponse login(MemberDTO.LoginRequest request) {
        return memberRepository.findByEmailAndStatus(request.getEmail(), MemberStatus.CERTIFICATED)
          .map(member -> {
              validatePasswordWithThrow(request.getPassword(), member.getPassword());
              TokenDTO tokenDTO = jwtTokenUtil.generatedToken(member.getEmail());
              member.updateRefreshToken(tokenDTO.getRefreshToken());
              return MemberDTO.LoginResponse.of(member, tokenDTO.getAccessToken());
          })
          .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_MEMBER));
    }

    private void validatePasswordWithThrow(String password, String encodedPassword) {
        if (!passwordEncoder.matches(password, encodedPassword)) {
            throw new ApiException(ApiErrorCode.NOT_MATCH_PASSWORD);
        }
    }

    @Async
    protected void sendMail(Member newMember, String uuid) throws Exception {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);

        mimeMessage.addRecipients(Message.RecipientType.TO, newMember.getEmail());
        messageHelper.setSubject("가입 인증 메일");
        String body = "<div>"
          + "<h1> 안녕하세요.</h1>"
          + "<br>"
          + "<p>아래 링크를 클릭하면 이메일 인증이 완료됩니다.<p>"
          + "<a href='http://localhost:8080/api/members/verify?uuid=" + uuid + "'>인증 링크</a>"
          + "</div>";
        messageHelper.setText(body, true);
        messageHelper.setFrom(new InternetAddress(mailSenderUsername, "MASTER"));
        mailSender.send(mimeMessage);
    }

}
