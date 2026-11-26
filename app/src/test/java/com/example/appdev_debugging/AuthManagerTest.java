package com.example.appdev_debugging;

import com.example.appdev_debugging.auth.AuthManager;
import com.example.appdev_debugging.auth.AuthResult;
import com.example.appdev_debugging.data.InMemoryUserRepository;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AuthManagerTest {

    private AuthManager authManager;

    @Before
    public void setUp() {
        authManager = new AuthManager(new InMemoryUserRepository());
    }

    @Test
    public void demoAccount_canLogInSuccessfully() {
        AuthResult registerResult = authManager.register(
                ServiceLocator.DEMO_NAME,
                ServiceLocator.DEMO_EMAIL,
                ServiceLocator.DEMO_PASSWORD,
                ServiceLocator.DEMO_PASSWORD);
        assertTrue(registerResult.isSuccess());

        AuthResult loginResult = authManager.login(ServiceLocator.DEMO_EMAIL, ServiceLocator.DEMO_PASSWORD);
        assertTrue(loginResult.isSuccess());
        assertNotNull(loginResult.getUser());
    }

    @Test
    public void login_isCaseInsensitiveForEmail() {
        authManager.register("Juan Dela Cruz", "Juan.DelaCruz@Mail.com", "Password123", "Password123");

        // Login with lowercase email
        AuthResult loginLower = authManager.login("juan.delacruz@mail.com", "Password123");
        assertTrue(loginLower.isSuccess());

        // Login with uppercase email
        AuthResult loginUpper = authManager.login("JUAN.DELACRUZ@MAIL.COM", "Password123");
        assertTrue(loginUpper.isSuccess());
    }

    @Test
    public void register_rejectsDuplicateEmailWithDifferentCasing() {
        AuthResult first = authManager.register("User One", "Juan.DelaCruz@Mail.com", "Password123", "Password123");
        assertTrue(first.isSuccess());

        AuthResult second = authManager.register("User Two", "juan.delacruz@mail.com", "Password123", "Password123");
        assertFalse(second.isSuccess());
    }
}
