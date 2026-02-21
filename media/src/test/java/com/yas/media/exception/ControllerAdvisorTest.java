package com.yas.media.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.exception.UnsupportedMediaTypeException;
import com.yas.media.viewmodel.ErrorVm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

class ControllerAdvisorTest {

    private ControllerAdvisor controllerAdvisor;

    @BeforeEach
    void setUp() {
        controllerAdvisor = new ControllerAdvisor();
    }

    @Test
    void testHandleUnsupportedMediaTypeException() {
        UnsupportedMediaTypeException exception = new UnsupportedMediaTypeException("error");
        WebRequest request = mock(WebRequest.class);

        ResponseEntity<ErrorVm> response = controllerAdvisor.handleUnsupportedMediaTypeException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Unsupported media type", response.getBody().title());
    }

    @Test
    void testHandleNotFoundException() {
        NotFoundException exception = new NotFoundException("Media 1 is not found");
        WebRequest request = mock(WebRequest.class);

        ResponseEntity<ErrorVm> response = controllerAdvisor.handleNotFoundException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testHandleConstraintViolationException() {
        jakarta.validation.ConstraintViolationException exception = mock(
                jakarta.validation.ConstraintViolationException.class);

        ResponseEntity<ErrorVm> response = controllerAdvisor.handleConstraintViolation(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testHandleRuntimeException() {
        RuntimeException exception = new RuntimeException("Runtime error occurred");
        WebRequest request = mock(WebRequest.class);

        ResponseEntity<ErrorVm> response = controllerAdvisor.handleIoException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testHandleOtherException() {
        Exception exception = new Exception("Generic exception");
        WebRequest request = mock(WebRequest.class);

        ResponseEntity<ErrorVm> response = controllerAdvisor.handleOtherException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testExceptionHandlerWithNullMessage() {
        RuntimeException exception = new RuntimeException();
        WebRequest request = mock(WebRequest.class);

        ResponseEntity<ErrorVm> response = controllerAdvisor.handleIoException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testUnsupportedMediaTypeExceptionWithDifferentMessages() {
        UnsupportedMediaTypeException exception1 = new UnsupportedMediaTypeException("jpeg not allowed");
        UnsupportedMediaTypeException exception2 = new UnsupportedMediaTypeException("gif not allowed");

        ResponseEntity<ErrorVm> response1 = controllerAdvisor.handleUnsupportedMediaTypeException(exception1, null);
        ResponseEntity<ErrorVm> response2 = controllerAdvisor.handleUnsupportedMediaTypeException(exception2, null);

        assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    @Test
    void testNotFoundExceptionMultipleTimes() {
        NotFoundException exception1 = new NotFoundException("Resource 1 not found");
        NotFoundException exception2 = new NotFoundException("Resource 2 not found");

        ResponseEntity<ErrorVm> response1 = controllerAdvisor.handleNotFoundException(exception1, null);
        ResponseEntity<ErrorVm> response2 = controllerAdvisor.handleNotFoundException(exception2, null);

        assertEquals(HttpStatus.NOT_FOUND, response1.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
    }

    @Test
    void testConstraintViolationWithFieldErrors() {
        jakarta.validation.ConstraintViolationException exception = mock(
                jakarta.validation.ConstraintViolationException.class);

        ResponseEntity<ErrorVm> response = controllerAdvisor.handleConstraintViolation(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Request information is not valid", response.getBody().detail());
    }

    @Test
    void testMultipleExceptionHandlers() {
        RuntimeException runtimeEx = new RuntimeException("test");
        Exception genericEx = new Exception("test");

        ResponseEntity<ErrorVm> response1 = controllerAdvisor.handleIoException(runtimeEx, null);
        ResponseEntity<ErrorVm> response2 = controllerAdvisor.handleOtherException(genericEx, null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response1.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response2.getStatusCode());
    }
}
