package org.example.expert.config;

import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAuthAnnotation = parameter.getParameterAnnotation(Auth.class) != null;
        boolean isAuthUserType = parameter.getParameterType().equals(AuthUser.class);

        // @Auth 어노테이션과 AuthUser 타입이 함께 사용되지 않은 경우 예외 발생
        if (hasAuthAnnotation != isAuthUserType) {
            throw new AuthException("@Auth와 AuthUser 타입은 함께 사용되어야 합니다.");
        }

        return hasAuthAnnotation;
    }

    //    @Override
//    public Object resolveArgument(
//        @Nullable MethodParameter parameter,
//        @Nullable ModelAndViewContainer mavContainer,
//        NativeWebRequest webRequest,
//        @Nullable WebDataBinderFactory binderFactory
//    ) {
//        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
//
//        // JwtFilter 에서 set 한 userId, email, userRole 값을 가져옴
//        Long userId = (Long) request.getAttribute("userId");
//        String email = (String) request.getAttribute("email");
//        System.out.println(userId);
//        System.out.println(email);
//        System.out.println((String) request.getAttribute("userRole"));
//
//        UserRole userRole = UserRole.of((String) request.getAttribute("userRole"));
//        String nickName = (String) request.getAttribute("nickName");
//
//        return new AuthUser(userId, email, nickName, userRole);
//    }
    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("인증되지 않은 사용자입니다.");
        }

        // SecurityContext에서 AuthUser 객체 반환
        Object principal = authentication.getPrincipal();

        if (principal instanceof AuthUser authUser) {
            return authUser;
        } else if (principal instanceof UserDetails userDetails) {
            // Principal이 UserDetails 타입일 경우 AuthUser로 변환
            return new AuthUser(
                null, // ID를 가져올 수 없는 경우 처리 필요
                userDetails.getUsername(),
                null,
                null
            );
        }

        throw new IllegalArgumentException("적합하지 않은 사용자 정보입니다.");
    }

}
