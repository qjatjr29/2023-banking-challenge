package numble.banking.core.common.error;

import lombok.Getter;

@Getter
public enum ErrorCode {

  INVALID_INPUT_VALUE(400, "COMMON-001", "유효성 검증에 실패한 경우"),
  DUPLICATE_INPUT_VALUE(409, "COMMON-002", "중복된 값이 들어온 경우"),
  BAD_REQUEST(400, "COMMON-003", "잘못된 요청이 들어온 경우"),
  ENTITY_NOT_FOUND(404, "COMMON-004", "엔티티를 찾을 수 없는 경우"),

  UNAUTHORIZED(401, "USER-001", "인증에 실패한 경우"),
  USER_NOT_FOUND(404, "USER-002", "계정을 찾을 수 없는 경우"),
  ROLE_NOT_EXISTS(403, "USER-003", "권한이 부족한 경우"),
  TOKEN_NOT_EXISTS(404, "USER-004", "인증 토큰이 존재하지 않는 경우"),
  DUPLICATE_LOGIN_ID(409, "USER-005", "계정명이 중복된 경우"),
  DUPLICATE_PHONE_NUMBER(409, "USER-006", "휴대폰 번호가 중복된 경우"),
  DUPLICATE_EMAIL(409, "USER-006", "이메일이 중복된 경우"),

  EXPIRED_VERIFICATION_TOKEN(403, "AUTH-001", "인증 토큰이 만료된 경우"),
  INVALID_VERIFICATION_TOKEN(403, "AUTH-002", "토큰이 유효하지 않은 경우"),
  CERTIFICATION_TYPE_NOT_MATCH(403, "AUTH-003", "인증 타입이 일치하지 않은 경우");

  int statusCode;
  String resultCode;
  String message;

  ErrorCode(int statusCode, String resultCode, String message) {
    this.statusCode = statusCode;
    this.resultCode = resultCode;
    this.message = message;
  }
}
