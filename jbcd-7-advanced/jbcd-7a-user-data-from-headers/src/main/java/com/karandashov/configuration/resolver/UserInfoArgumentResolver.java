package com.karandashov.configuration.resolver;

import com.karandashov.dto.UserInfoHeaders;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.yaml.snakeyaml.util.UriEncoder;
import tools.jackson.databind.ObjectMapper;

/**
 * resolver, упрощающий внедрение UserInfo в rest-контроллеры
 */
@Slf4j
public class UserInfoArgumentResolver implements HandlerMethodArgumentResolver {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return UserInfoHeaders.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public UserInfoHeaders resolveArgument(@NonNull MethodParameter parameter,
                                           ModelAndViewContainer mavContainer,
                                           NativeWebRequest webRequest,
                                           WebDataBinderFactory binderFactory) {

        var request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            log.error("request is null");
            return null;
        }

        var userInfoEncodedJson = request.getHeader("user");
        var userInfo = parseUserInfo(userInfoEncodedJson);
        log.info("Получен пользователь: {}", userInfo);
        return userInfo;
    }

    @SneakyThrows
    public static UserInfoHeaders parseUserInfo(String header) {
        log.info("Получен header user = {}", header);
        var userInfoJson = UriEncoder.decode(header);
        return OBJECT_MAPPER.readValue(userInfoJson, UserInfoHeaders.class);
    }
}