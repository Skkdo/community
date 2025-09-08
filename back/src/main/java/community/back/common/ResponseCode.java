package community.back.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {
    SUCCESS(200, "Success"),

    // auth
    SIGN_IN_FAIL(401, "Login information mismatch"),
    AUTHORIZATION_FAIL(401, "Authorization Failed"),

    // user
    DUPLICATE_EMAIL(400, "Duplicate Email"),
    DUPLICATE_NICKNAME(400, "Duplicate Nickname"),
    NOT_EXISTED_USER(400, "This user does not exist"),

    // board
    NOT_EXISTED_BOARD(400, "This board does not exist"),

    // comment
    NOT_EXISTED_COMMENT(400, "This comment does not exist"),


    // global
    VALIDATION_FAILED(400, "Validation Failed"),
    NO_PERMISSION(403, "Do not have permission"),
    DATABASE_ERROR(500, "Database Error"),

    ;
    private final int status;
    private final String message;
}
