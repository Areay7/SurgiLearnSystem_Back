package com.discussio.resourc.common.config;

import com.discussio.resourc.common.domain.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 是否为“客户端断开”（Broken pipe 等），此时 response 可能已按流式写出，不应再写 JSON
     */
    private static boolean isClientAbort(Throwable e) {
        if (e == null) return false;
        String name = e.getClass().getName();
        if (name.contains("ClientAbortException")) return true;
        if (e.getCause() != null && e.getCause() instanceof IOException
                && "Broken pipe".equals(e.getCause().getMessage())) return true;
        return isClientAbort(e.getCause());
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public AjaxResult handleRuntimeException(RuntimeException e) {
        if (isClientAbort(e)) {
            logger.debug("客户端断开连接，忽略: {}", e.getMessage());
            return null; // 响应可能已按流式写出，不再写 JSON，避免 WARN
        }
        logger.error("运行时异常：", e);
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 处理所有异常
     */
    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e) {
        if (isClientAbort(e)) {
            logger.debug("客户端断开连接，忽略: {}", e.getMessage());
            return null; // 响应可能已按流式写出，不再写 JSON，避免 WARN
        }
        logger.error("系统异常：", e);
        return AjaxResult.error("系统异常：" + e.getMessage());
    }
}
