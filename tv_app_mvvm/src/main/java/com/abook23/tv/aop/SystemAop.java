package com.abook23.tv.aop;

import android.util.Log;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.MethodSignature;

/**
 * description:    描述
 * author:         yangxiong
 * e-mail:         abook23@163.com
 * createDate:     2020/12/24 15:13
 * updateUser:     更新者：
 * updateDate:     2020/12/24 15:13
 * updateRemark:   更新说明：
 * version:        1.0
 */
//@Aspect
public class SystemAop {
//   private final String TAG = "SystemAop";
//
//    @Pointcut("execution(* android.view.View.OnClickListener.onClick(..))")
//    public void clickMethod() {}
//
//    /*
//     * joinPoint.proceed() 执行注解所标识的代码
//     * @After 可以在方法前插入代码
//     * @Before 可以在方法后插入代码
//     * @Around 可以在方法前后各插入代码
//     */
//    @Around("clickMethod()")
//    public void aroundJoinPoint( ProceedingJoinPoint joinPoint) throws Throwable {
//        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//        String className = methodSignature.getDeclaringType().getSimpleName();
//        String methodName = methodSignature.getName();
//        //统计时间
//        long begin = System.currentTimeMillis();
//        Object result = joinPoint.proceed();
//        long duration = System.currentTimeMillis() - begin;
//        Log.e(TAG, String.format("功能：%s类的%s方法执行了，用时%d ms", className, methodName, duration));
//    }
}
