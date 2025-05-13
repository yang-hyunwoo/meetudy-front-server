package front.meetudy.util.aop;

import front.meetudy.auth.LoginUser;
import front.meetudy.dto.request.log.LogReqDto;
import front.meetudy.service.log.LogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/* TODO SecurityContextHolder.getContext().getAuthentication().getPrincipal() 값이 있을 경우에만 디비에 로그가 저장이 된다.
        log 테이블에 member가 연관관계로 되어 있어서 그렇다.
        이대로 갈지 로그 테이블을 조금 수정을 해야 될지 고민을 해 봐야 겠다..
*/

@Slf4j
@Component
@Aspect
@RequiredArgsConstructor
@ConditionalOnProperty(name = "aop.logging.enabled", havingValue = "true", matchIfMissing = true)
public class AspectAdvice {

    private final LogService logService;
    private final ThreadLocal<AspectUUID> traceIdHolder = new ThreadLocal<>();

    @Pointcut("execution(* front.meetudy.controller..*.*(..))")
    private void cut() {}

    private void syncTraceId() {
        traceIdHolder.set(new AspectUUID());
    }

    @Before("cut()")
    public void before(JoinPoint joinPoint) {
        syncTraceId();
        LogContext context = extractContext(joinPoint);
        logInfo(context.uuid, "before", context, getRequestBody(joinPoint));
        logIfAuthenticated(context, getRequestBody(joinPoint), null, null);
    }

    @AfterReturning(value = "cut()", returning = "result")
    public void afterReturn(JoinPoint joinPoint, Object result) {
        LogContext context = extractContext(joinPoint);
        logInfo(context.uuid, "afterReturn", context, result);
        logIfAuthenticated(context, null, result, null);
    }

    @AfterThrowing(value = "cut()", throwing = "e")
    public void afterThrow(JoinPoint joinPoint, Exception e) {
        LogContext context = extractContext(joinPoint);
        logInfo(context.uuid, "afterThrow", context, e.getMessage());
        logIfAuthenticated(context, null, null, e.getMessage());
    }

    @Around("cut()")
    public Object timer(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        LogContext context = extractContext(joinPoint);
        try {
            return joinPoint.proceed();
        } finally {
            log.info("timer = uuid:[{}] method:[{}] {}ms", context.uuid, context.classMethod, System.currentTimeMillis() - start);
            traceIdHolder.remove();
        }
    }

    private LogContext extractContext(JoinPoint joinPoint) {
        MethodSignature sig = (MethodSignature) joinPoint.getSignature();
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        AspectUUID aspectUUID = traceIdHolder.get();
        String uuid = (aspectUUID != null) ? aspectUUID.getUUID() : "UNKNOWN";

        return new LogContext(
                uuid,
                req.getMethod(),
                sig.getDeclaringType().getSimpleName() + "." + sig.getMethod().getName()
        );
    }

    private void logIfAuthenticated(LogContext ctx, Object request, Object response, String error) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!"anonymousUser".equals(principal)) {
            LoginUser user = (LoginUser) principal;
            LogReqDto dto = LogReqDto.of(ctx.uuid, error == null, ctx.classMethod, ctx.httpMethod,
                    response != null ? response.toString() : null,
                    request != null ? request.toString() : null,
                    error);
            logService.logInsert(dto, user);
        }
    }

    private void logInfo(String uuid, String phase, LogContext ctx, Object value) {
        log.info("{} = uuid:[{}] http:[{}] method:[{}] data:[{}]", phase, uuid, ctx.httpMethod, ctx.classMethod, value);
    }

    private String getRequestBody(JoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            if (arg != null) return arg.toString();
        }
        return null;
    }

    record LogContext(String uuid, String httpMethod, String classMethod) {}
}