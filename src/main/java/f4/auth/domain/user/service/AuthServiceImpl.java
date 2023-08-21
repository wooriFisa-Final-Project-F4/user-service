package f4.auth.domain.user.service;

import f4.auth.domain.user.dto.request.CreateTokenDto;
import f4.auth.domain.user.dto.request.LoginRequestDto;
import f4.auth.domain.user.dto.response.TokenResponseDto;
import f4.auth.domain.user.persist.entity.Member;
import f4.auth.domain.user.persist.repository.MemberRepository;
import f4.auth.global.constant.CustomErrorCode;
import f4.auth.global.exception.CustomException;
import f4.auth.global.redis.RedisService;
import f4.auth.global.security.jwt.JwtTokenProvider;
import f4.auth.global.security.jwt.JwtValidateService;
import f4.auth.global.utils.Encryptor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final JwtValidateService jwtValidateService;
    private final MemberRepository memberRepository;
    private final Encryptor encryptor;
    private final ModelMapper modelMapper;

    @Value("${jwt.token.access-expiration-time}")
    private Long atkDuration;

    @Value("${jwt.token.refresh-expiration-time}")
    private Long rtkDuration;

    @Override
    @Transactional
    public TokenResponseDto login(LoginRequestDto loginDto) {
        Member member = memberRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_MEMBER));

        if (!encryptor.matchers(loginDto.getPassword(), member.getPassword())) {
            throw new CustomException(CustomErrorCode.NOT_VALID_LOGIN_PASSWORD);
        }
        CreateTokenDto createTokenDto = modelMapper.map(member, CreateTokenDto.class);

        final String atk = jwtTokenProvider.createAccessToken(createTokenDto);
        final String rtk = jwtTokenProvider.createRefreshToken(createTokenDto);
        redisService.setDataExpire(loginDto.getEmail(), rtk, Duration.ofMillis(atkDuration));


        return TokenResponseDto.builder()
                .accessToken(atk)
                .refreshToken(rtk)
                .build();
    }

    @Override
    public TokenResponseDto getNewAccessToken(String rtk) {
        return null;
    }
}
