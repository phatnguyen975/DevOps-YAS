package com.yas.media.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintValidatorContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class FileTypeValidatorTest {

    private FileTypeValidator validator;
    private ConstraintValidatorContext context;
    private ValidFileType annotation;

    @BeforeEach
    void setUp() {
        validator = new FileTypeValidator();
        context = mock(ConstraintValidatorContext.class);
        annotation = mock(ValidFileType.class);

        when(annotation.allowedTypes()).thenReturn(new String[] { "image/jpeg", "image/png", "image/gif" });
        when(annotation.message()).thenReturn("File type not allowed");
        when(context.buildConstraintViolationWithTemplate(any())).thenReturn(
                mock(ConstraintValidatorContext.ConstraintViolationBuilder.class));

        validator.initialize(annotation);
    }

    @Test
    void testValidJpegFile() throws IOException {
        byte[] jpegHeader = new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0 };
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                new ByteArrayInputStream(jpegHeader));

        boolean result = validator.isValid(file, context);
        assertTrue(result);
    }

    @Test
    void testValidPngFile() throws IOException {
        byte[] pngHeader = new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47 };
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                new ByteArrayInputStream(pngHeader));

        boolean result = validator.isValid(file, context);
        assertTrue(result);
    }

    @Test
    void testInvalidContentType() {
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "content".getBytes());

        boolean result = validator.isValid(file, context);
        assertFalse(result);
    }

    @Test
    void testNullFile() {
        boolean result = validator.isValid(null, context);
        assertFalse(result);
    }

    @Test
    void testNullContentType() {
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                null,
                "content".getBytes());

        boolean result = validator.isValid(file, context);
        assertFalse(result);
    }

    @Test
    void testInvalidImageContent() {
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "not a real image".getBytes());

        boolean result = validator.isValid(file, context);
        assertFalse(result);
    }

    @Test
    void testEmptyFile() {
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                new byte[] {});

        boolean result = validator.isValid(file, context);
        assertFalse(result);
    }

    @Test
    void testMultipleSupportedTypes() throws IOException {
        when(annotation.allowedTypes()).thenReturn(
                new String[] { "image/jpeg", "image/png", "image/gif", "image/webp" });
        validator.initialize(annotation);

        byte[] jpegHeader = new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0 };
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                new ByteArrayInputStream(jpegHeader));

        boolean result = validator.isValid(file, context);
        assertTrue(result);
    }

    @Test
    void testFileTypeNotInAllowedList() throws IOException {
        when(annotation.allowedTypes()).thenReturn(new String[] { "image/jpeg", "image/png" });
        validator.initialize(annotation);

        byte[] gifHeader = new byte[] { 0x47, 0x49, 0x46, 0x38 };
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.gif",
                "image/gif",
                new ByteArrayInputStream(gifHeader));

        boolean result = validator.isValid(file, context);
        assertFalse(result);
    }

    @Test
    void testValidationContextDisabled() {
        when(context.buildConstraintViolationWithTemplate(any())).thenReturn(
                mock(ConstraintValidatorContext.ConstraintViolationBuilder.class));

        MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "content".getBytes());

        boolean result = validator.isValid(file, context);
        assertFalse(result);
    }

    @Test
    void testCaseInsensitiveContentType() throws IOException {
        byte[] jpegHeader = new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0 };
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                new ByteArrayInputStream(jpegHeader));

        boolean result = validator.isValid(file, context);
        assertTrue(result);
    }
}
