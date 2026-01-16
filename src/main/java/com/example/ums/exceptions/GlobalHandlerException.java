package com.example.ums.exceptions;

import com.example.ums.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalHandlerException {
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserAlreadyExistsException(UserAlreadyExistsException userAlreadyExistsException) {
        Map<String, Object> errorMap = new HashMap<> ();
        errorMap.put ("timestamp", LocalDateTime.now ());
        errorMap.put ("status", HttpStatus.BAD_REQUEST.value ());
        errorMap.put ("error", "Bad Request");
        errorMap.put ("message", userAlreadyExistsException.getMessage ());
        return new ResponseEntity<> (errorMap, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<?> handleRoleNotFoundException(RoleNotFoundException roleNotFoundException){
        Map<String, Object> errorMap = new HashMap<> ();
        errorMap.put ("timestamp", LocalDateTime.now ());
        errorMap.put ("status", HttpStatus.NOT_FOUND.value ());
        errorMap.put ("error", "Not found");
        errorMap.put ("message", roleNotFoundException.getMessage ());
        return new ResponseEntity<> (errorMap, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException userNotFoundException){
        Map<String, Object> errorMap = new HashMap<> ();
        errorMap.put ("timestamp", LocalDateTime.now ());
        errorMap.put ("status", HttpStatus.NOT_FOUND.value ());
        errorMap.put ("error", "Not found");
        errorMap.put ("message", userNotFoundException.getMessage ());
        return new ResponseEntity<> (errorMap, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<?> handlePasswordMismatchException(PasswordMismatchException passwordMismatchException) {
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("timestamp", LocalDateTime.now());
        errorMap.put("status", HttpStatus.BAD_REQUEST.value());
        errorMap.put("error", "Bad Request");
        errorMap.put("message", passwordMismatchException.getMessage());
        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RefreshTokenNotFoundException.class)
    public ResponseEntity<?> handleRefreshTokenNotFoundException(RefreshTokenNotFoundException refreshTokenNotFoundException){
        Map<String, Object> errorMap = new HashMap<> ();
        errorMap.put ("timestamp", LocalDateTime.now ());
        errorMap.put ("status", HttpStatus.NOT_FOUND.value ());
        errorMap.put ("error", "Not found");
        errorMap.put ("message", refreshTokenNotFoundException.getMessage ());
        return new ResponseEntity<> (errorMap, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<?> handleInvalidRefreshTokenException(InvalidRefreshTokenException ex) {
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("timestamp", LocalDateTime.now());
        errorMap.put("status", HttpStatus.UNAUTHORIZED.value());
        errorMap.put("error", "Unauthorized");
        errorMap.put("message", ex.getMessage());
        return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BlacklistedTokenException.class)
    public ResponseEntity<?> handleBlacklistedTokenException(BlacklistedTokenException ex) {
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("timestamp", LocalDateTime.now());
        errorMap.put("status", HttpStatus.UNAUTHORIZED.value());
        errorMap.put("error", "Unauthorized");
        errorMap.put("message", ex.getMessage());
        return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidTokenTypeException.class)
    public ResponseEntity<?> handleInvalidTokenTypeException(InvalidTokenTypeException ex) {
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("timestamp", LocalDateTime.now());
        errorMap.put("status", HttpStatus.UNAUTHORIZED.value());
        errorMap.put("error", "Unauthorized");
        errorMap.put("message", ex.getMessage());
        return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<?> handleCustomerNotFoundException(CustomerNotFoundException customerNotFoundException){
        Map<String, Object> errorMap = new HashMap<> ();
        errorMap.put ("timestamp", LocalDateTime.now ());
        errorMap.put ("status", HttpStatus.NOT_FOUND.value ());
        errorMap.put ("error", "Not found");
        errorMap.put ("message", customerNotFoundException.getMessage ());
        return new ResponseEntity<> (errorMap, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED) // 401
    public ErrorResponse handleBadCredentials(BadCredentialsException ex) {
        return new ErrorResponse(
                "INVALID_CREDENTIALS",
                "Wrong email or password"
        );
    }
}
