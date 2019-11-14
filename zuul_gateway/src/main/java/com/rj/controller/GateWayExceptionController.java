package com.rj.controller;

import com.rj.exception.GateWayException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GateWayExceptionController {
    @ExceptionHandler(GateWayException.class)
    @ResponseBody
    public String HandleGateWayException(GateWayException e) {
        return "路由异常："+ e.getCode()+","+e.getMessage();
    }
}
