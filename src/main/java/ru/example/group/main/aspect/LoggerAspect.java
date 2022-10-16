package ru.example.group.main.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggerAspect {


    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)" +
            "||within(@org.springframework.stereotype.Repository *)" +
            "||within(@org.springframework.stereotype.Service *)")
    public void methodExecuting() {
    }

    @AfterThrowing(value = "methodExecuting()", throwing = "exception")
    public void recordFailedExecution(JoinPoint joinPoint, Exception exception) {
        log.error("Метод - {}, класса- {}, был аварийно завершен с исключением - {}\n" +
                        "стек: {}",
                joinPoint.getSignature().getName(),
                joinPoint.getSourceLocation().getWithinType().getName(),
                exception.getMessage(), exception.getStackTrace());
    }

}