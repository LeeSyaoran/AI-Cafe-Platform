# Monitoring & Observability

## 1. Overview

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                           OBSERVABILITY STACK                                        │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│                                                                                      │
│    ┌──────────────────────────────────────────────────────────────────────────┐   │
│    │                          METRICS (Prometheus)                             │   │
│    │                                                                          │   │
│    │   • Request rates, latencies, errors                                     │   │
│    │   • Resource utilization (CPU, memory, network)                          │   │
│    │   • Business metrics (credits, orders, users)                            │   │
│    │   • Custom application metrics                                           │   │
│    │                                                                          │   │
│    └──────────────────────────────────────────────────────────────────────────┘   │
│                                      │                                              │
│                                      ▼                                              │
│    ┌──────────────────────────────────────────────────────────────────────────┐   │
│    │                           LOGS (Loki)                                     │   │
│    │                                                                          │   │
│    │   • Application logs                                                    │   │
│    │   • Access logs                                                         │   │
│    │   • Error logs                                                          │   │
│    │   • Audit logs                                                          │   │
│    │                                                                          │   │
│    └──────────────────────────────────────────────────────────────────────────┘   │
│                                      │                                              │
│                                      ▼                                              │
│    ┌──────────────────────────────────────────────────────────────────────────┐   │
│    │                         TRACES (Tempo)                                    │   │
│    │                                                                          │   │
│    │   • Distributed request tracing                                         │   │
│    │   • Service dependencies                                                │   │
│    │   • Performance bottlenecks                                             │   │
│    │   • Error correlation                                                   │   │
│    │                                                                          │   │
│    └──────────────────────────────────────────────────────────────────────────┘   │
│                                      │                                              │
│                                      ▼                                              │
│    ┌──────────────────────────────────────────────────────────────────────────┐   │
│    │                       ALERTS (Alertmanager)                               │   │
│    │                                                                          │   │
│    │   • Critical system alerts                                               │   │
│    │   • Performance degradation                                             │   │
│    │   • Error rate spikes                                                   │   │
│    │   • Business anomalies                                                   │   │
│    │                                                                          │   │
│    └──────────────────────────────────────────────────────────────────────────┘   │
│                                      │                                              │
│                                      ▼                                              │
│    ┌──────────────────────────────────────────────────────────────────────────┐   │
│    │                         DASHBOARDS (Grafana)                              │   │
│    │                                                                          │   │
│    │   • Service overview                                                     │   │
│    │   • Business metrics                                                     │   │
│    │   • Infrastructure status                                               │   │
│    │   • User-facing metrics                                                  │   │
│    │                                                                          │   │
│    └──────────────────────────────────────────────────────────────────────────┘   │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Metrics Collection

### 2.1 Java (Micrometer + Prometheus)

```java
// src/main/java/com/aicafe/config/MetricsConfig.java
package com.aicafe.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MetricsConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config()
                .commonTags(List.of(
                        Tag.of("application", applicationName),
                        Tag.of("environment", System.getenv().getOrDefault("ENV", "dev")),
                        Tag.of("region", "ap-southeast-1")
                ));
    }
}
```

```java
// src/main/java/com/aicafe/service/CreditService.java
package com.aicafe.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class CreditService {

    private final Counter creditDeductedCounter;
    private final Counter creditEarnedCounter;
    private final Counter insufficientCreditsCounter;
    private final Timer creditOperationTimer;

    public CreditService(MeterRegistry registry) {
        this.creditDeductedCounter = Counter.builder("credits_deducted_total")
                .description("Total credits deducted")
                .tag("type", "ai_request")
                .register(registry);

        this.creditEarnedCounter = Counter.builder("credits_earned_total")
                .description("Total credits earned")
                .tag("type", "purchase")
                .register(registry);

        this.insufficientCreditsCounter = Counter.builder("credits_insufficient_total")
                .description("Insufficient credits attempts")
                .register(registry);

        this.creditOperationTimer = Timer.builder("credits_operation_duration")
                .description("Credit operation duration")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .register(registry);
    }

    public void deductCredits(String userId, long amount, String source) {
        creditOperationTimer.record(() -> {
            // Credit deduction logic
            creditDeductedCounter.increment(amount);
        });
    }
}
```

### 2.2 Go (Prometheus Client)

```go
// internal/metrics/metrics.go
package metrics

import (
	"github.com/prometheus/client_golang/prometheus"
	"github.com/prometheus/client_golang/prometheus/promauto"
)

var (
	// HTTP metrics
	HttpRequestsTotal = promauto.NewCounterVec(
		prometheus.CounterOpts{
			Name: "http_requests_total",
			Help: "Total number of HTTP requests",
		},
		[]string{"method", "path", "status"},
	)

	HttpRequestDuration = promauto.NewHistogramVec(
		prometheus.HistogramOpts{
			Name:    "http_request_duration_seconds",
			Help:    "HTTP request latency in seconds",
			Buckets: prometheus.DefBuckets,
		},
		[]string{"method", "path"},
	)

	// Credit metrics
	CreditsDeductedTotal = promauto.NewCounterVec(
		prometheus.CounterOpts{
			Name: "credits_deducted_total",
			Help: "Total credits deducted",
		},
		[]string{"source", "model"},
	)

	CreditsEarnedTotal = promauto.NewCounterVec(
		prometheus.CounterOpts{
			Name: "credits_earned_total",
			Help: "Total credits earned",
		},
		[]string{"source"},
	)

	// Business metrics
	ActiveUsersGauge = promauto.NewGauge(
		prometheus.GaugeOpts{
			Name: "active_users_total",
			Help: "Number of active users",
		},
	)

	OrdersTotal = promauto.NewCounterVec(
		prometheus.CounterOpts{
			Name: "orders_total",
			Help: "Total number of orders",
		},
		[]string{"status", "payment_method"},
	)

	// AI metrics
	AIRequestDuration = promauto.NewHistogramVec(
		prometheus.HistogramOpts{
			Name:    "ai_request_duration_seconds",
			Help:    "AI request duration in seconds",
			Buckets: []float64{0.1, 0.5, 1, 2, 5, 10, 30, 60},
		},
		[]string{"model", "status"},
	)

	AIRequestTokens = promauto.NewCounterVec(
		prometheus.CounterOpts{
			Name: "ai_request_tokens_total",
			Help: "Total tokens used",
		},
		[]string{"model", "type"}, // type: prompt, completion
	)
)

// Middleware for HTTP metrics
func HTTPMetricsMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		start := time.Now()
		path := c.FullPath()
		if path == "" {
			path = "unknown"
		}

		c.Next()

		duration := time.Since(start).Seconds()
		status := strconv.Itoa(c.Writer.Status())

		HttpRequestsTotal.WithLabelValues(c.Request.Method, path, status).Inc()
		HttpRequestDuration.WithLabelValues(c.Request.Method, path).Observe(duration)
	}
}
```

---

## 3. Logging

### 3.1 Java Logging (Logback + Loki)

```xml
<!-- src/main/resources/logback-spring.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>

    <springProperty scope="context" name="appName" source="spring.application.name"/>

    <!-- Console appender -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- JSON appender for structured logging -->
    <appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
            <layout class="ch.qos.logback.contrib.json.classic.JsonLayout">
                <timestampFormat>yyyy-MM-dd'T'HH:mm:ss.SSSX</timestampFormat>
                <timestampFormatTimezoneId>UTC</timestampFormatTimezoneId>
                <appendLineSeparator>true</appendLineSeparator>
                <jsonFormatter class="ch.qos.logback.contrib.jackson.JacksonJsonFormatter">
                    <prettyPrint>false</prettyPrint>
                </jsonFormatter>
            </layout>
        </encoder>
    </appender>

    <!-- Async wrapper for JSON -->
    <appender name="ASYNC_JSON" class="ch.qos.logback.classic.AsyncAppender">
        <queueSize>512</queueSize>
        <discardingThreshold>0</discardingThreshold>
        <includeCallerData>false</includeCallerData>
        <appender-ref ref="JSON"/>
    </appender>

    <!-- Loki appender -->
    <appender name="LOKI" class="com.github.loki4j.logback.EncodingAppender">
        <http>
            <url>https://loki.aicafe.vn/loki/api/v1/push</url>
            <connectTimeoutMs>5000</connectTimeoutMs>
            <writeTimeoutMs>5000</writeTimeoutMs>
        </http>
        <format>
            <label>
                <pattern>app=${appName},env=${ENV:-dev}</pattern>
            </label>
            <message>
                <pattern>${FILE_LOG_PATTERN}</pattern>
            </message>
            <sortByTime>true</sortByTime>
        </format>
        <backoff>
            <maxBackoff>30000</maxBackoff>
            <base>1000</base>
            <maxRetries>3</maxRetries>
        </backoff>
    </appender>

    <springProfile name="prod">
        <root level="INFO">
            <appender-ref ref="ASYNC_JSON"/>
            <appender-ref ref="LOKI"/>
        </root>
        <logger name="com.aicafe" level="DEBUG"/>
        <logger name="org.springframework.web" level="INFO"/>
        <logger name="org.hibernate.SQL" level="WARN"/>
    </springProfile>

    <springProfile name="dev">
        <root level="DEBUG">
            <appender-ref ref="CONSOLE"/>
        </root>
    </springProfile>
</configuration>
```

```java
// Structured logging example
@Slf4j
@Service
public class PaymentService {

    public void processPayment(Order order, PaymentResult result) {
        Map<String, Object> logData = Map.of(
            "order_id", order.getId(),
            "order_number", order.getOrderNumber(),
            "amount", order.getTotal(),
            "payment_method", result.getMethod(),
            "transaction_id", result.getTransactionId(),
            "duration_ms", result.getDurationMs()
        );

        if (result.isSuccess()) {
            log.info("Payment processed successfully",
                Tags.of("order_id", order.getId())
                    .and("amount", order.getTotal().toString())
                    .and("method", result.getMethod())
                    .and("duration_ms", result.getDurationMs())
            );
        } else {
            log.error("Payment failed",
                Tags.of("order_id", order.getId())
                    .and("error_code", result.getErrorCode())
                    .and("error_message", result.getErrorMessage())
            );
        }
    }
}
```

### 3.2 Go Logging (Zap + Loki)

```go
// pkg/logger/logger.go
package logger

import (
	"os"
	"time"

	"github.com/go-logr/logr"
	"github.com/go-logr/zapr"
	"github.com/grafana/loki/pkg/logcli/output"
	"github.com/grafana/loki/pkg/logproto"
	"github.com/grafana/loki/pkg/push"
	"go.uber.org/zap"
	"go.uber.org/zap/zapcore"
)

type Config struct {
	Level      string
	Format     string // json or console
	LokiURL    string
	LokiTenant string
	LokiKey    string
}

func New(cfg Config) logr.Logger {
	var encoder zapcore.Encoder
	encoderConfig := zap.NewProductionEncoderConfig()
	encoderConfig.TimeKey = "timestamp"
	encoderConfig.EncodeTime = func(t time.Time, enc zapcore.PrimitiveArrayEncoder) {
		enc.AppendString(t.UTC().Format(time.RFC3339Nano))
	}

	if cfg.Format == "console" {
		encoder = zapcore.NewConsoleEncoder(encoderConfig)
	} else {
		encoder = zapcore.NewJSONEncoder(encoderConfig)
	}

	level := zapcore.InfoLevel
	if cfg.Level == "debug" {
		level = zapcore.DebugLevel
	}

	core := zapcore.NewCore(
		encoder,
		zapcore.AddSync(os.Stdout),
		level,
	)

	// Add Loki if configured
	if cfg.LokiURL != "" {
		core = zapcore.NewTee(
			core,
			newLokiCore(cfg),
		)
	}

	return zapr.NewFunc(func(msg string, fields ...interface{}) {
		// Implementation
	})
}

// Loki pusher
type LokiPusher struct {
	client *push.HTTPClient
	url    string
}

func (p *LokiPusher) Push(streams []logproto.Stream) error {
	req := &push.PushRequest{Streams: streams}
	return p.client.Push(req)
}
```

---

## 4. Distributed Tracing

### 4.1 Java (OpenTelemetry)

```java
// src/main/java/com/aicafe/config/OpenTelemetryConfig.java
package com.aicafe.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.semconv.ResourceAttributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTelemetryConfig {

    @Value("${spring.application.name}")
    private String serviceName;

    @Value("${OTEL_EXPORTER_OTLP_ENDPOINT:http://tempo:4317}")
    private String otlpEndpoint;

    @Bean
    public OpenTelemetry openTelemetry() {
        Resource resource = Resource.getDefault()
                .merge(Resource.create(Attributes.of(
                        ResourceAttributes.SERVICE_NAME, serviceName,
                        ResourceAttributes.SERVICE_VERSION, "1.0.0"
                )));

        OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint(otlpEndpoint)
                .build();

        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
                .setResource(resource)
                .build();

        return OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .buildAndRegisterGlobal();
    }

    @Bean
    public Tracer tracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer(serviceName);
    }
}
```

```java
// Using tracing in service
@Service
public class AIService {

    private final Tracer tracer;

    public AIService(OpenTelemetry openTelemetry) {
        this.tracer = openTelemetry.getTracer("ai-service");
    }

    public ChatResponse chat(ChatRequest request) {
        Span span = tracer.spanBuilder("ai.chat")
                .setAttribute("model", request.getModel())
                .setAttribute("user_id", request.getUserId())
                .startSpan();

        try (Scope scope = span.makeCurrent()) {
            // Call AI provider
            ChatResponse response = callAIProvider(request);

            span.setAttribute("response_tokens", response.getUsage().getCompletionTokens());
            span.setAttribute("credits_used", response.getCreditsUsed());
            span.setStatus(StatusCode.OK);

            return response;
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }
}
```

### 4.2 Go (OpenTelemetry)

```go
// internal/telemetry/telemetry.go
package telemetry

import (
	"context"
	"fmt"

	"go.opentelemetry.io/otel"
	"go.opentelemetry.io/otel/attribute"
	"go.opentelemetry.io/otel/exporters/otlp/otlptrace/otlptracegrpc"
	"go.opentelemetry.io/otel/propagation"
	"go.opentelemetry.io/otel/sdk/resource"
	sdktrace "go.opentelemetry.io/otel/sdk/trace"
	semconv "go.opentelemetry.io/otel/semconv/v1.17.0"
	"go.opentelemetry.io/otel/trace"
)

func InitTracer(serviceName, endpoint string) (func(context.Context) error, error) {
	ctx := context.Background()

	exporter, err := otlptracegrpc.New(ctx,
		otlptracegrpc.WithEndpoint(endpoint),
		otlptracegrpc.WithInsecure(),
	)
	if err != nil {
		return nil, fmt.Errorf("creating trace exporter: %w", err)
	}

	res, err := resource.New(ctx,
		resource.WithAttributes(
			semconv.ServiceName(serviceName),
			semconv.ServiceVersion("1.0.0"),
		),
	)
	if err != nil {
		return nil, fmt.Errorf("creating resource: %w", err)
	}

	tp := sdktrace.NewTracerProvider(
		sdktrace.WithBatcher(exporter),
		sdktrace.WithResource(res),
	)

	otel.SetTracerProvider(tp)
	otel.SetTextMapPropagator(propagation.NewCompositeTextMapPropagator(
		propagation.TraceContext{},
		propagation.Baggage{},
	))

	return tp.Shutdown, nil
}

// Middleware for HTTP tracing
func TracingMiddleware(serviceName string) gin.HandlerFunc {
	tracer := otel.Tracer(serviceName)

	return func(c *gin.Context) {
		ctx := otel.GetTextMapPropagator().Extract(c.Request.Context(),
			propagation.HeaderCarrier(c.Request.Header))

		spanName := fmt.Sprintf("%s %s", c.Request.Method, c.FullPath())
		ctx, span := tracer.Start(ctx, spanName,
			trace.WithAttributes(
				attribute.String("http.method", c.Request.Method),
				attribute.String("http.url", c.Request.URL.String()),
				attribute.String("http.host", c.Request.Host),
				attribute.String("http.user_agent", c.Request.UserAgent()),
			),
		)
		defer span.End()

		c.Request = c.Request.WithContext(ctx)
		c.Next()

		span.SetAttributes(attribute.Int("http.status_code", c.Writer.Status()))
	}
}
```

---

## 5. Dashboards

### 5.1 Service Overview Dashboard

```json
{
  "title": "AI Cafe - Service Overview",
  "panels": [
    {
      "title": "Request Rate",
      "type": "timeseries",
      "gridPos": {"x": 0, "y": 0, "w": 12, "h": 8},
      "targets": [
        {
          "expr": "sum(rate(http_requests_total[5m])) by (service)",
          "legendFormat": "{{service}}"
        }
      ],
      "fieldConfig": {
        "defaults": {
          "unit": "reqps",
          "custom": {
            "lineWidth": 2,
            "fillOpacity": 10
          }
        }
      }
    },
    {
      "title": "Error Rate by Service",
      "type": "timeseries",
      "gridPos": {"x": 12, "y": 0, "w": 12, "h": 8},
      "targets": [
        {
          "expr": "sum(rate(http_requests_total{status=~\"5..\"}[5m])) by (service) / sum(rate(http_requests_total[5m])) by (service) * 100",
          "legendFormat": "{{service}}"
        }
      ],
      "fieldConfig": {
        "defaults": {
          "unit": "percent",
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {"color": "green", "value": null},
              {"color": "yellow", "value": 1},
              {"color": "red", "value": 5}
            ]
          }
        }
      }
    },
    {
      "title": "Latency (p50, p95, p99)",
      "type": "timeseries",
      "gridPos": {"x": 0, "y": 8, "w": 16, "h": 8},
      "targets": [
        {
          "expr": "histogram_quantile(0.50, sum(rate(http_request_duration_seconds_bucket[5m])) by (le, service))",
          "legendFormat": "{{service}} p50"
        },
        {
          "expr": "histogram_quantile(0.95, sum(rate(http_request_duration_seconds_bucket[5m])) by (le, service))",
          "legendFormat": "{{service}} p95"
        },
        {
          "expr": "histogram_quantile(0.99, sum(rate(http_request_duration_seconds_bucket[5m])) by (le, service))",
          "legendFormat": "{{service}} p99"
        }
      ]
    },
    {
      "title": "CPU Usage",
      "type": "timeseries",
      "gridPos": {"x": 0, "y": 16, "w": 8, "h": 8},
      "targets": [
        {
          "expr": "sum(rate(container_cpu_usage_seconds_total{pod=~\"user-service-.*\"}[5m])) by (pod)"
        }
      ]
    },
    {
      "title": "Memory Usage",
      "type": "timeseries",
      "gridPos": {"x": 8, "y": 16, "w": 8, "h": 8},
      "targets": [
        {
          "expr": "container_memory_usage_bytes{pod=~\"user-service-.*\"} / container_spec_memory_limit_bytes{pod=~\"user-service-.*\"} * 100"
        }
      ]
    },
    {
      "title": "Pod Count",
      "type": "stat",
      "gridPos": {"x": 16, "y": 16, "w": 4, "h": 4},
      "targets": [
        {
          "expr": "count(kube_pod_info{namespace=\"aicafe\", pod=~\"user-service-.*\"})"
        }
      ]
    }
  ]
}
```

### 5.2 Business Metrics Dashboard

```json
{
  "title": "AI Cafe - Business Metrics",
  "panels": [
    {
      "title": "Active Users (24h)",
      "type": "stat",
      "gridPos": {"x": 0, "y": 0, "w": 4, "h": 4},
      "targets": [
        {
          "expr": "sum(active_users_total)"
        }
      ],
      "fieldConfig": {
        "defaults": {
          "color": {"mode": "thresholds"},
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {"color": "red", "value": null},
              {"color": "yellow", "value": 100},
              {"color": "green", "value": 500}
            ]
          }
        }
      }
    },
    {
      "title": "Credits Usage (Today)",
      "type": "stat",
      "gridPos": {"x": 4, "y": 0, "w": 4, "h": 4},
      "targets": [
        {
          "expr": "sum(credits_deducted_total)"
        }
      ]
    },
    {
      "title": "Orders (Today)",
      "type": "stat",
      "gridPos": {"x": 8, "y": 0, "w": 4, "h": 4},
      "targets": [
        {
          "expr": "sum(orders_total{status=\"PAID\"})"
        }
      ]
    },
    {
      "title": "Revenue (Today)",
      "type": "stat",
      "gridPos": {"x": 12, "y": 0, "w": 4, "h": 4},
      "targets": [
        {
          "expr": "sum(orders_total{status=\"PAID\"} * on(order_id) group_left(amount) orders_amount)"
        }
      ],
      "fieldConfig": {
        "defaults": {
          "unit": "currencyVND"
        }
      }
    },
    {
      "title": "AI Requests by Model",
      "type": "piechart",
      "gridPos": {"x": 0, "y": 4, "w": 8, "h": 8},
      "targets": [
        {
          "expr": "sum(increase(ai_requests_total[24h])) by (model)"
        }
      ]
    },
    {
      "title": "Credits Usage by Source",
      "type": "piechart",
      "gridPos": {"x": 8, "y": 4, "w": 8, "h": 8},
      "targets": [
        {
          "expr": "sum(credits_deducted_total) by (source)"
        }
      ]
    },
    {
      "title": "User Registration Trend",
      "type": "timeseries",
      "gridPos": {"x": 0, "y": 12, "w": 12, "h": 8},
      "targets": [
        {
          "expr": "sum(increase(user_registrations_total[1d]))"
        }
      ]
    }
  ]
}
```

---

## 6. Alerting

### 6.1 Alert Rules

```yaml
# monitoring/alerts/aicafe-alerts.yaml
groups:
  - name: aicafe-system
    interval: 30s
    rules:
      # Service Down
      - alert: ServiceDown
        expr: up{job=~"user-service|credit-service|order-service"} == 0
        for: 2m
        labels:
          severity: critical
          team: backend
        annotations:
          summary: "Service {{ $labels.job }} is down"
          description: "{{ $labels.job }} has been down for more than 2 minutes"
          runbook_url: "https://wiki.aicafe.vn/runbooks/service-down"

      # High Error Rate
      - alert: HighErrorRate
        expr: |
          (
            sum(rate(http_requests_total{status=~"5.."}[5m])) by (service)
            /
            sum(rate(http_requests_total[5m])) by (service)
          ) > 0.05
        for: 5m
        labels:
          severity: critical
          team: backend
        annotations:
          summary: "High error rate on {{ $labels.service }}"
          description: "Error rate is {{ $value | humanizePercentage }} (threshold: 5%)"

      # High Latency
      - alert: HighLatencyP99
        expr: |
          histogram_quantile(0.99, 
            sum(rate(http_request_duration_seconds_bucket[5m])) by (le, service)
          ) > 3
        for: 5m
        labels:
          severity: warning
          team: backend
        annotations:
          summary: "High p99 latency on {{ $labels.service }}"
          description: "p99 latency is {{ $value | humanize }}s (threshold: 3s)"

      # Database Connection Pool
      - alert: DatabasePoolExhausted
        expr: |
          hikaricp_connections_active{pool="primary"} / 
          hikaricp_connections_max{pool="primary"} > 0.9
        for: 5m
        labels:
          severity: warning
          team: backend
        annotations:
          summary: "Database connection pool nearly exhausted"
          description: "Active connections: {{ $value | humanizePercentage }}"

      # Redis Connection
      - alert: RedisConnectionFailed
        expr: redis_connected_clients == 0
        for: 1m
        labels:
          severity: critical
          team: backend
        annotations:
          summary: "Redis connection lost"
          description: "No connected Redis clients detected"

  - name: aicafe-business
    interval: 1m
    rules:
      # Low Credits
      - alert: UserLowCredits
        expr: wallet_credits < 100
        for: 5m
        labels:
          severity: info
          team: product
        annotations:
          summary: "User has low credits"
          description: "User {{ $labels.user_id }} has only {{ $value }} credits"

      # Suspicious Activity
      - alert: HighCreditDeduction
        expr: |
          sum by (user_id) (increase(credits_deducted_total[1h])) > 10000
        for: 5m
        labels:
          severity: warning
          team: security
        annotations:
          summary: "High credit deduction detected"
          description: "User {{ $labels.user_id }} deducted {{ $value }} credits in 1 hour"

      # Payment Failures
      - alert: HighPaymentFailureRate
        expr: |
          sum(rate(orders_total{status="PAYMENT_FAILED"}[5m])) /
          sum(rate(orders_total[5m])) > 0.1
        for: 10m
        labels:
          severity: warning
          team: backend
        annotations:
          summary: "High payment failure rate"
          description: "Payment failure rate is {{ $value | humanizePercentage }}"

  - name: aicafe-ai
    interval: 1m
    rules:
      # AI Provider Down
      - alert: AIProviderDown
        expr: |
          sum(rate(ai_requests_total{status="PROVIDER_ERROR"}[5m])) by (provider) /
          sum(rate(ai_requests_total[5m])) by (provider) > 0.5
        for: 3m
        labels:
          severity: critical
          team: ai-platform
        annotations:
          summary: "AI Provider {{ $labels.provider }} experiencing issues"
          description: "More than 50% of requests to {{ $labels.provider }} are failing"

      # AI Latency Spike
      - alert: AIServiceLatencySpike
        expr: |
          histogram_quantile(0.99, 
            sum(rate(ai_request_duration_seconds_bucket[5m])) by (le, model)
          ) > 30
        for: 5m
        labels:
          severity: warning
          team: ai-platform
        annotations:
          summary: "High latency for AI model {{ $labels.model }}"
          description: "p99 latency is {{ $value | humanize }}s"
```

### 6.2 Alert Routing

```yaml
# monitoring/alertmanager.yaml
global:
  resolve_timeout: 5m

route:
  receiver: default
  group_by: ['alertname', 'severity']
  group_wait: 30s
  group_interval: 5m
  repeat_interval: 4h
  routes:
    - match:
        severity: critical
      receiver: critical-alerts
      group_wait: 10s
      repeat_interval: 1h
      routes:
        - match:
            team: security
          receiver: security-alerts
          pagerduty_configs:
            - service_key: <pagerduty-key>
              severity: critical

    - match:
        severity: warning
      receiver: warning-alerts
      routes:
        - match:
            team: backend
          receiver: backend-slack

    - match:
        severity: info
      receiver: info-alerts

receivers:
  - name: default
    slack_configs:
      - channel: '#alerts-default'
        send_resolved: true

  - name: critical-alerts
    pagerduty_configs:
      - service_key: <pagerduty-key>
        severity: critical
        event_action: trigger
    slack_configs:
      - channel: '#alerts-critical'
        send_resolved: true
    opsgenie_configs:
      - api_key: <opsgenie-key>
        responders:
          - id: on-call
            type: schedule

  - name: warning-alerts
    slack_configs:
      - channel: '#alerts-warning'
        send_resolved: true

  - name: backend-slack
    slack_configs:
      - channel: '#team-backend'
        send_resolved: true
        title: "{{ .GroupLabels.alertname }}"
        text: |
          {{ range .Alerts }}
          *{{ .Labels.severity | upper }}* - {{ .Labels.alertname }}
          {{ .Annotations.summary }}
          
          {{ .Annotations.description }}
          
          Dashboard: {{ .Annotations.dashboard_url }}
          Runbook: {{ .Annotations.runbook_url }}
          {{ end }}

  - name: security-alerts
    slack_configs:
      - channel: '#team-security'
        send_resolved: true

inhibit_rules:
  - source_match:
      severity: critical
    target_match:
      severity: warning
    equal: ['alertname', 'service']
```

---

## 7. Health Checks

### 7.1 Application Health

```java
// src/main/java/com/aicafe/config/HealthIndicatorConfig.java
package com.aicafe.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    public DatabaseHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            // Check connection
            connection.createStatement().execute("SELECT 1");
            
            // Get pool metrics
            HikariPoolMXBean pool = ((HikariDataSource) dataSource).getHikariPoolMXBean();
            
            return Health.up()
                    .withDetail("database", "PostgreSQL")
                    .withDetail("activeConnections", pool.getActiveConnections())
                    .withDetail("idleConnections", pool.getIdleConnections())
                    .withDetail("totalConnections", pool.getTotalConnections())
                    .withDetail("threadsAwaitingConnection", pool.getThreadsAwaitingConnection())
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
```

```java
// Kubernetes health endpoints
@RestController
@RequestMapping("/actuator")
public class HealthController {

    @GetMapping("/health/liveness")
    public ResponseEntity<Map<String, String>> liveness() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }

    @GetMapping("/health/readiness")
    public ResponseEntity<Map<String, Object>> readiness() {
        Map<String, Object> health = new HashMap<>();
        
        // Check database
        boolean dbHealthy = checkDatabase();
        health.put("database", dbHealthy ? "UP" : "DOWN");
        
        // Check redis
        boolean redisHealthy = checkRedis();
        health.put("redis", redisHealthy ? "UP" : "DOWN");
        
        boolean ready = dbHealthy && redisHealthy;
        health.put("status", ready ? "UP" : "DOWN");
        
        return ready 
            ? ResponseEntity.ok(health)
            : ResponseEntity.status(503).body(health);
    }
}
```

### 7.2 Kubernetes Probes

```yaml
# Probe configuration in deployment
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 60
  periodSeconds: 10
  failureThreshold: 3
  timeoutSeconds: 5

readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 5
  failureThreshold: 3
  timeoutSeconds: 3
  successThreshold: 1
```

---

## 8. On-Call

### 8.1 Runbook Template

```markdown
# Runbook: [Incident Name]

## Severity
[Severity Level]

## Summary
[Brief description of the incident]

## Impact
- Users affected: [Number]
- Services affected: [List]
- Revenue impact: [Estimated]

## Symptoms
- [Symptom 1]
- [Symptom 2]

## Root Cause
[Analysis of the root cause]

## Resolution
[Steps taken to resolve]

## Timeline
| Time | Event |
|------|-------|
| HH:MM | Event 1 |
| HH:MM | Event 2 |

## Follow-up Actions
- [ ] Action item 1
- [ ] Action item 2

## Prevention
[How to prevent this from happening again]
```

### 8.2 Escalation Policy

```
Level 1 (0-15 min):
├── On-call engineer
└── Slack: #incidents

Level 2 (15-30 min):
├── Team lead
├── Slack: #incidents-critical
└── PagerDuty: @team-lead

Level 3 (30+ min):
├── Engineering Manager
├── CTO notification
└── Status page update
```

---

## 9. SLI/SLO

### 9.1 Service Level Indicators

| Service | SLI | Target | Current |
|---------|-----|--------|---------|
| API Gateway | Availability | 99.9% | 99.95% |
| User Service | Latency p99 | < 500ms | 320ms |
| Credit Service | Latency p99 | < 200ms | 85ms |
| AI Chat | Latency p99 | < 10s | 4.2s |
| Payment | Success rate | > 99.5% | 99.8% |

### 9.2 Error Budget

```yaml
# SLO definitions
service_level:
  - name: api-availability
    target: 99.9
    window: 30d
    budget_alert: 50
    burn_rate_alert: 14.4

  - name: api-latency
    target: 99.0
    window: 30d
    threshold: 500ms
    budget_alert: 50

# Error budget policy
error_budget_policy:
  burn_rate_alerts:
    - name: fast-burn
      burn_rate: 14.4
      window: 1h
      action: page immediately

    - name: slow-burn
      burn_rate: 6
      window: 6h
      action: notify team

  maintenance:
    max_percentage: 1
    cool_down: 24h
```
