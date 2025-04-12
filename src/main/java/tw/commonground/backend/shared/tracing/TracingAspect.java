package tw.commonground.backend.shared.tracing;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Aspect
@Component
public class TracingAspect {

    private final Tracer tracer;

    private final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public TracingAspect(Tracer tracer) {
        this.tracer = tracer;
    }

    @Pointcut("within(@Traced *)")
    public void tracedClass() { }

    @SneakyThrows
    @Around("tracedClass() || execution(* org.springframework.data.jpa.repository.JpaRepository+.*(..))))")
    public Object traceMethod(ProceedingJoinPoint joinPoint) {

        String spanName = joinPoint.getSignature().getName();
        Span newSpan = tracer.spanBuilder(spanName)
                .setSpanKind(SpanKind.INTERNAL)
                .startSpan();

        newSpan.setAttribute("class", joinPoint.getTarget().getClass().getName());
        newSpan.setAttribute("method", joinPoint.getSignature().getName());

        Object[] args = joinPoint.getArgs();
        String[] parameterNames = parameterNameDiscoverer
                .getParameterNames(((MethodSignature) joinPoint.getSignature()).getMethod());

        Map<String, Object> attributes = new HashMap<>();
        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                attributes.put(parameterNames[i], args[i]);
            }
        }

        String formattedArgs = attributes.entrySet()
                .stream()
                .map(entry -> {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (key.toLowerCase().contains("token")) {
                        value = "********";
                    }
                    return String.format("%s: %s", key, value);
                })
                .collect(Collectors.joining("\n"));

        newSpan.setAttribute("args", formattedArgs);

        if (attributes.containsKey("id")) {
            newSpan.setAttribute("id", attributes.get("id").toString());
        }

        try (Scope scope = newSpan.makeCurrent()) {
            Object result = joinPoint.proceed();
            if (result != null) {
                newSpan.setAttribute("result", result.toString());
            }
            return result;
        } catch (Throwable e) {
            if (!(e instanceof IllegalArgumentException && e.getMessage().contains("Invalid token"))) {
                newSpan.setAttribute("error", true);
            }
            newSpan.recordException(e);
            throw e;
        } finally {
            newSpan.end();
        }
    }

}
