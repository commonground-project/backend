package tw.commonground.backend.configuration;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class TracingConfig {

    @Bean
    public OpenTelemetry openTelemetry() {
        String serviceName = System.getenv()
                .getOrDefault("TW_COMMONGROUND_BACKEND_SERVICE_VERSION", "local");

        OtlpGrpcSpanExporter exporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint("http://localhost:4317")
                .build();

        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .setResource(Resource.builder().put("service.name", "tw.commonground.backend:" + serviceName).build())
                .addSpanProcessor(BatchSpanProcessor.builder(exporter).build())
                .build();

        return OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .build();
    }

    @Bean
    public Tracer tracer() {
        return openTelemetry().getTracer("tw.commonground.backend");
    }

}
