package edu.utah.cs.uparknow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * 捕获并发冲突异常 (乐观锁冲突)
     * 返回 409 Conflict
     */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleOptimisticLockFailure(ObjectOptimisticLockingFailureException ex) {
        return "Concurrency conflict: " + ex.getMessage();
    }

 
}
