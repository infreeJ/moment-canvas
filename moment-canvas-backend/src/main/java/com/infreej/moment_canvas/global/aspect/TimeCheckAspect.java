package com.infreej.moment_canvas.global.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;


/**
 * 실행 시간 측정 Aspect
 */
@Slf4j
@Aspect
@Component
public class TimeCheckAspect {

    @Around("@annotation(com.infreej.moment_canvas.global.annotation.TimeCheck)")
    public Object measureTime(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        boolean isSuccess = true;

        try {
            // 시간 측정 시작
            stopWatch.start();

            // 원래 메서드 실행
            return joinPoint.proceed();
        } catch (Throwable e) {
            isSuccess = false;
            throw e; // 예외는 다시 던져서 컨트롤러나 예외 핸들러가 처리하게 둠
        } finally {
            // 메서드 실행 후 시간 측정 종료 및 로깅
            stopWatch.stop();
            long totalTime = stopWatch.getTotalTimeMillis();

            // 클래스명, 메서드명 가져오기
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String methodName = joinPoint.getSignature().getName();

            // 성공 여부
            String status = isSuccess ? "성공" : "실패";

            // 실행시간이 12초를 넘으면 WARN, 아니면 INFO
            if(totalTime > 12000) {
                log.warn("[{}]실행 시간: {}.{} = {}ms", status, className, methodName, totalTime);
            } else {
                log.info("[{}]실행 시간: {}.{} = {}ms", status, className, methodName, totalTime);
            }
        }
    }
}
