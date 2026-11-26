package com.example.appdev_debugging;

import com.example.appdev_debugging.util.Validator;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ValidatorTest {

    @Test
    public void validateEmail_acceptsComEmailsOnly() {
        assertNull(Validator.validateEmail("user@example.com"));
        assertNull(Validator.validateEmail("USER@EXAMPLE.COM"));
    }

    @Test
    public void validateEmail_rejectsNonComDomainsAndPh() {
        assertNotNull(Validator.validateEmail("juan@school.ph"));
        assertNotNull(Validator.validateEmail("juan@up.edu.ph"));
        assertNotNull(Validator.validateEmail("user@domain.uk"));
        assertNotNull(Validator.validateEmail("user@domain.org"));
        assertNotNull(Validator.validateEmail("invalid-email"));
        assertNotNull(Validator.validateEmail("user@domain"));
        assertNotNull(Validator.validateEmail(""));
        assertNotNull(Validator.validateEmail(null));
    }

    @Test
    public void validateNewPassword_requiresLettersAndNumbers() {
        // Weak passwords (only letters or only digits) must be rejected
        assertNotNull(Validator.validateNewPassword("abcdefgh"));
        assertNotNull(Validator.validateNewPassword("12345678"));

        // Valid password containing both letters and numbers
        assertNull(Validator.validateNewPassword("abcd1234"));
    }
}
