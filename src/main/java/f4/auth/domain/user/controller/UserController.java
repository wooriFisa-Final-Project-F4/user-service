package f4.auth.domain.user.controller;

import f4.auth.domain.user.dto.request.SignupRequestDto;
import f4.auth.domain.user.dto.response.MailingResponseDto;
import f4.auth.domain.user.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user/v1")
public class UserController {

  private final UserService userService;

  /*
  * @date : 2023.08.22
  * @author : yuki
  * @param : RequestDto
  * @description : 회원 등록
  */
  @PostMapping("/signup")
  public ResponseEntity<?> register(@Valid @RequestBody SignupRequestDto signupRequestDto) {
    userService.register(signupRequestDto);
    return ResponseEntity.ok("회원가입에 성공하였습니다.");
  }

  /*
   * @date : 2023.08.24
   * @author : yuki
   * @param : userId
   * @description : email-service 해당 유저의 이메일 정보 조회
   * */
  @GetMapping("/email/{userId}")
  public MailingResponseDto getUserByUserIdForMailing(@PathVariable("userId") Long userId) {
    return userService.getUserByUserIdForMailing(userId);
  }
}
