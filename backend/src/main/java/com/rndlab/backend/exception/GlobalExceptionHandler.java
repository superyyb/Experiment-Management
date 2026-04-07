package com.rndlab.backend.exception;

import org.apache.ibatis.exceptions.PersistenceException;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * 统一处理异常并返回友好的错误信息
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "参数验证失败");
        response.put("errors", errors);
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("error", ex.getClass().getSimpleName());
        // 打印详细堆栈信息到日志
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 处理MyBatis系统异常
     */
    @ExceptionHandler(MyBatisSystemException.class)
    public ResponseEntity<Map<String, Object>> handleMyBatisSystemException(MyBatisSystemException ex) {
        Map<String, Object> response = new HashMap<>();
        Throwable cause = ex.getCause();
        String detailMessage = ex.getMessage();
        
        if (cause != null) {
            detailMessage = cause.getMessage();
            // 打印详细堆栈信息到日志
            cause.printStackTrace();
        } else {
            ex.printStackTrace();
        }
        
        response.put("message", "数据库操作失败，请检查数据库连接和配置");
        response.put("error", "MyBatisSystemException");
        response.put("detail", detailMessage);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 处理MyBatis持久化异常
     */
    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<Map<String, Object>> handlePersistenceException(PersistenceException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "数据库操作失败");
        response.put("error", "PersistenceException");
        response.put("detail", ex.getMessage());
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "服务器内部错误");
        response.put("error", ex.getClass().getSimpleName());
        response.put("detail", ex.getMessage());
        // 打印详细堆栈信息到日志
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

