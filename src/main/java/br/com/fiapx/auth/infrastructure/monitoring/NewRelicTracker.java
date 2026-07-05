package br.com.fiapx.auth.infrastructure.monitoring;

import com.newrelic.api.agent.NewRelic;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NewRelicTracker {

    public void trackLogin(String email) {
        NewRelic.getAgent().getInsights().recordCustomEvent("UserLogin", Map.of("email", email));
    }

    public void trackRegistration(String email) {
        NewRelic.getAgent().getInsights().recordCustomEvent("UserRegistration", Map.of("email", email));
    }

    public void trackLoginFailure(String email) {
        NewRelic.getAgent().getInsights().recordCustomEvent("UserLoginFailure", Map.of("email", email));
        NewRelic.incrementCounter("Custom/Auth/LoginFailures");
    }
}
