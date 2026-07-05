package br.com.fiapx.auth.infrastructure.monitoring;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;

class NewRelicTrackerTest {

    private final NewRelicTracker tracker = new NewRelicTracker();

    @Test
    void trackLogin_shouldNotThrow() {
        assertThatNoException().isThrownBy(() -> tracker.trackLogin("user@test.com"));
    }

    @Test
    void trackRegistration_shouldNotThrow() {
        assertThatNoException().isThrownBy(() -> tracker.trackRegistration("user@test.com"));
    }

    @Test
    void trackLoginFailure_shouldNotThrow() {
        assertThatNoException().isThrownBy(() -> tracker.trackLoginFailure("user@test.com"));
    }
}
