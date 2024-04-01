package com.example.miniproject.config;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;

//@RequiredArgsConstructor
//@Aspect
//@Component
public class SlackNotificationAspect {

//    private final SlackApi slackApi;
//    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public void slackNotification(
      ProceedingJoinPoint proceedingJoinPoint,
      HttpServletRequest request,
      Exception e
    ) throws Throwable {
        proceedingJoinPoint.proceed();
    }

}
