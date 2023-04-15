package com.example.chatapi.exception;

import com.example.chatapi.controller.UserController;
import com.example.chatapi.model.res.Result;
import com.example.chatapi.model.res.ResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class MyExceptionHandler {

    @Autowired
    private UserController userController;

    @ExceptionHandler(value = ExceptionModel.class)
    @ResponseBody
    public Result myExceptionHandler(ExceptionModel e) {
        // TODO 精确到某个用户
        String errorInfo = String.format("捕获异常信息：%s", e.getMessage());
        log.error(errorInfo);
        e.printStackTrace();
        return ResultEnum.ERROR.setCode(e.getCode()).setMsg(e.getMessage()).getResult();
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result globalExceptionHandler(Exception e) {
        String errorInfo = String.format("未处理的异常信息：%s", e.getMessage());
        log.error(errorInfo);
        e.printStackTrace();
        return ResultEnum.ERROR.setMsg(e.getMessage()).getResult();
    }

    @ExceptionHandler(value = MissingRequestHeaderException.class)
    @ResponseBody
    public Result globalExceptionHandler(MissingRequestHeaderException e) {
        String errorInfo = String.format("缺少请求头参数：%s", e.getMessage());
        log.error(errorInfo);
        e.printStackTrace();
        return ExceptionEnum.MISSING_REQUEST_HEADER.getResult();
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseBody
    public Result globalExceptionHandler(IllegalArgumentException e) {
        String errorInfo = String.format("非法参数：%s", e.getMessage());
        log.error(errorInfo);
        e.printStackTrace();
        return ExceptionEnum.ILLEGAL_ARGUMENT.getResult();
    }
}
