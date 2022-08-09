package ru.example.group.main.aspect;

import java.util.Arrays;
import liquibase.pro.packaged.T;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Aspect
@Component

public class LoggerAspect {

  private final Logger info = LogManager.getLogger("MyFile");
  private final Logger log = LogManager.getLogger();

  @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)"+
  "||within(@org.springframework.stereotype.Repository *)"+
  "||within(@org.springframework.stereotype.Service *)")
  public void methodExecuting() {
  }
  @Before(value = "methodExecuting()")//сюда бы еще параметры метода передать...
  public void beforeLogInfo(JoinPoint joinPoint){
    info.info("вызывается метод - {}, класса- {}, с параметрами - {}\n",
        joinPoint.getSignature().getName(),
        joinPoint.getSourceLocation().getWithinType().getName(),
        Arrays.toString(joinPoint.getArgs()));
  }
  @AfterReturning(value = "methodExecuting()", returning = "returningValue")
  public void recordSuccessfulExecution(JoinPoint joinPoint, Object returningValue) {
    if (returningValue != null) {
      info.info("Успешно выполнен метод - {}, класса- {}, с результатом выполнения -{}\n",
          joinPoint.getSignature().getName(),
          joinPoint.getSourceLocation().getWithinType().getName(),
          returningValue);
    }
    else {
      info.info("Успешно выполнен метод - {}, класса- {}\n",
          joinPoint.getSignature().getName(),
          joinPoint.getSourceLocation().getWithinType().getName());
    }
  }

  @AfterThrowing(value = "methodExecuting()", throwing = "exception")
  public void recordFailedExecution(JoinPoint joinPoint, Throwable exception) {
    info.debug("Метод - {}, класса- {}, был аварийно завершен с исключением - {}\n",
        joinPoint.getSignature().getName(),
        joinPoint.getSourceLocation().getWithinType().getName(),
        exception);
  }
}
