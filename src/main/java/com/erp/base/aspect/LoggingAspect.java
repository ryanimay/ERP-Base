package com.erp.base.aspect;

import com.erp.base.model.ClientIdentity;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.LogModel;
import com.erp.base.service.LogService;
import com.erp.base.tool.ObjectTool;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URISyntaxException;

@Aspect
@Component
public class LoggingAspect {
    private LogService logService;
    @Autowired
    public void setLogService(LogService logService){
        this.logService = logService;
    }

    @SuppressWarnings("unchecked")
    @Around("@annotation(Loggable)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        LogModel model = new LogModel();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        model.setUrl(getRequestUrl(request));
        model.setParams(getRequestArgs(joinPoint));
        ClientModel user = ClientIdentity.getUser();
        if(user == null) throw new UsernameNotFoundException("LoggingAspect:USER_NOT_FOUND");
        model.setUserName(user.getUsername());
        model.setIp(getClientIp(request));

        ResponseEntity<ApiResponse> result;
        try {
            result = (ResponseEntity<ApiResponse>) joinPoint.proceed();
            ApiResponse response = result.getBody();
            if(response != null && response.getCode() == 200){
                model.setStatus(true);
                model.setResult(response.getMessage());//成功就只儲存成功信息
            }else{
                model.setStatus(false);
                model.setResult(ObjectTool.toJson(response));//請求失敗儲存完整返回結果
            }
        } catch (Throwable e) {
            model.setStatus(false);
            model.setResult("ERROR: " + e.getMessage());
            throw e;
        } finally {
            logService.save(model);
        }
        return result;
    }

    private String getRequestUrl(HttpServletRequest request) throws URISyntaxException {
        return ObjectTool.extractPath(request.getRequestURI());
    }

    private String getRequestArgs(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        return ObjectTool.toJson(args);
    }

    private String getClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");

        if (checkIp(clientIp)) {
            clientIp = request.getHeader("Proxy-Client-IP");
        }
        if (checkIp(clientIp)) {
            clientIp = request.getHeader("WL-Proxy-Client-IP");
        }
        if (checkIp(clientIp)) {
            clientIp = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (checkIp(clientIp)) {
            clientIp = request.getHeader("HTTP_X_FORWARDED");
        }
        if (checkIp(clientIp)) {
            clientIp = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
        }
        if (checkIp(clientIp)) {
            clientIp = request.getHeader("HTTP_CLIENT_IP");
        }
        if (checkIp(clientIp)) {
            clientIp = request.getHeader("HTTP_FORWARDED_FOR");
        }
        if (checkIp(clientIp)) {
            clientIp = request.getHeader("HTTP_FORWARDED");
        }
        if (checkIp(clientIp)) {
            clientIp = request.getHeader("HTTP_VIA");
        }
        if (checkIp(clientIp)) {
            clientIp = request.getHeader("REMOTE_ADDR");
        }
        if (checkIp(clientIp)) {
            clientIp = request.getRemoteAddr();
        }
        // 如果是多級代理，獲取第一個IP位址
        if (StringUtils.isNotEmpty(clientIp) && clientIp.contains(",")) {
            clientIp = clientIp.split(",")[0].trim();
        }
        return clientIp;
    }
    
    private boolean checkIp(String clientIp){
        return StringUtils.isEmpty(clientIp) || "unknown".equalsIgnoreCase(clientIp);
    }
}
