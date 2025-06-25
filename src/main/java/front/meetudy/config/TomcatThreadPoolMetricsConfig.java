package front.meetudy.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 프로메테우스 THREAD 추가 설정
 */
@Configuration
public class TomcatThreadPoolMetricsConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer(MeterRegistry registry) {
        return factory -> factory.addConnectorCustomizers(connector -> {
            ProtocolHandler handler = connector.getProtocolHandler();
            if (handler instanceof AbstractProtocol<?> protocol) {
                ThreadPoolExecutor executor = new ThreadPoolExecutor(
                        10, 200,
                        60, TimeUnit.SECONDS,
                        new LinkedBlockingQueue<>()
                );
                protocol.setExecutor(executor);

                // 메트릭 등록
                registry.gauge("tomcat_threads_current_threads", executor, ThreadPoolExecutor::getPoolSize);
                registry.gauge("tomcat_threads_active_threads", executor, ThreadPoolExecutor::getActiveCount);
                registry.gauge("tomcat_threads_config_max", executor, ThreadPoolExecutor::getMaximumPoolSize);
                registry.gauge("tomcat_threads_idle_threads", executor, e -> e.getPoolSize() - e.getActiveCount());
                registry.gauge("tomcat_threads_queue_size", executor, e -> e.getQueue().size());
            }
        });
    }
}