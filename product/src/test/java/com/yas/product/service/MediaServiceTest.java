package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.config.ServiceUrlConfig;
import com.yas.product.viewmodel.NoFileMediaVm;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private RestClient restClient;
    @Mock
    private ServiceUrlConfig serviceUrlConfig;
    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private RestClient.ResponseSpec responseSpec;
    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @Mock
    private Jwt jwt;

    @InjectMocks
    private MediaService mediaService;

    private void setupSecurityContext() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getTokenValue()).thenReturn("test-jwt-token");
    }

    // Test saveFile successfully
    @Test
    void test_save_file_successfully() {
        // Arrange
        setupSecurityContext();
        MultipartFile multipartFile = new MockMultipartFile(
            "file", 
            "test.jpg", 
            MediaType.IMAGE_JPEG_VALUE, 
            "test content".getBytes()
        );
        String caption = "Test Caption";
        String fileNameOverride = "override.jpg";
        String mediaUrl = "http://media-service.com";
        
        NoFileMediaVm expectedResponse = new NoFileMediaVm(
            1L,
            caption,
            "test.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "http://media-service.com/test.jpg"
        );

        when(serviceUrlConfig.media()).thenReturn(mediaUrl);
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(URI.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.contentType(MediaType.MULTIPART_FORM_DATA)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.headers(any())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(MultiValueMap.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(NoFileMediaVm.class)).thenReturn(expectedResponse);

        // Act
        NoFileMediaVm result = mediaService.saveFile(multipartFile, caption, fileNameOverride);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("test.jpg", result.fileName());
        assertEquals(MediaType.IMAGE_JPEG_VALUE, result.mediaType());
        verify(restClient).post();
    }

    // Test getMedia successfully with valid ID
    @Test
    void test_get_media_successfully_with_valid_id() {
        // Arrange
        Long mediaId = 1L;
        String mediaUrl = "http://media-service.com";
        
        NoFileMediaVm expectedResponse = new NoFileMediaVm(
            mediaId,
            "Image caption",
            "image.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "http://media-service.com/image.jpg"
        );

        when(serviceUrlConfig.media()).thenReturn(mediaUrl);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(NoFileMediaVm.class)).thenReturn(expectedResponse);

        // Act
        NoFileMediaVm result = mediaService.getMedia(mediaId);

        // Assert
        assertNotNull(result);
        assertEquals(mediaId, result.id());
        assertEquals("image.jpg", result.fileName());
        verify(restClient).get();
    }

    // Test getMedia with null ID returns empty response
    @Test
    void test_get_media_with_null_id_returns_empty_response() {
        // Act
        NoFileMediaVm result = mediaService.getMedia(null);

        // Assert
        assertNotNull(result);
        assertNull(result.id());
        assertEquals("", result.fileName());
        assertEquals("", result.mediaType());
        assertEquals("", result.caption());
        assertEquals("", result.url());
    }

    // Test removeMedia successfully
    @Test
    void test_remove_media_successfully() {
        // Arrange
        setupSecurityContext();
        Long mediaId = 1L;
        String mediaUrl = "http://media-service.com";

        when(serviceUrlConfig.media()).thenReturn(mediaUrl);
        when(restClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(Void.class)).thenReturn(null);

        // Act
        mediaService.removeMedia(mediaId);

        // Assert
        verify(restClient).delete();
        verify(requestHeadersUriSpec).headers(any());
    }

    // Test saveFile with empty string caption instead of null
    @Test
    void test_save_file_with_empty_caption() {
        // Arrange
        setupSecurityContext();
        MultipartFile multipartFile = new MockMultipartFile(
            "file", 
            "test.jpg", 
            MediaType.IMAGE_JPEG_VALUE, 
            "test content".getBytes()
        );
        String caption = "";
        String fileNameOverride = "override.jpg";
        String mediaUrl = "http://media-service.com";
        
        NoFileMediaVm expectedResponse = new NoFileMediaVm(
            1L,
            caption,
            "test.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "http://media-service.com/test.jpg"
        );

        when(serviceUrlConfig.media()).thenReturn(mediaUrl);
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(URI.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.contentType(MediaType.MULTIPART_FORM_DATA)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.headers(any())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(MultiValueMap.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(NoFileMediaVm.class)).thenReturn(expectedResponse);

        // Act
        NoFileMediaVm result = mediaService.saveFile(multipartFile, caption, fileNameOverride);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(restClient).post();
    }

    // Test saveFile with empty string filename override instead of null
    @Test
    void test_save_file_with_empty_filename_override() {
        // Arrange
        setupSecurityContext();
        MultipartFile multipartFile = new MockMultipartFile(
            "file", 
            "test.jpg", 
            MediaType.IMAGE_JPEG_VALUE, 
            "test content".getBytes()
        );
        String caption = "Test Caption";
        String fileNameOverride = "";
        String mediaUrl = "http://media-service.com";
        
        NoFileMediaVm expectedResponse = new NoFileMediaVm(
            1L,
            caption,
            "test.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "http://media-service.com/test.jpg"
        );

        when(serviceUrlConfig.media()).thenReturn(mediaUrl);
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(URI.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.contentType(MediaType.MULTIPART_FORM_DATA)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.headers(any())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(MultiValueMap.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(NoFileMediaVm.class)).thenReturn(expectedResponse);

        // Act
        NoFileMediaVm result = mediaService.saveFile(multipartFile, caption, fileNameOverride);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(restClient).post();
    }

    // Test removeMedia with zero ID
    @Test 
    void test_remove_media_with_zero_id() {
        // Arrange
        setupSecurityContext();
        Long mediaId = 0L;
        String mediaUrl = "http://media-service.com";

        when(serviceUrlConfig.media()).thenReturn(mediaUrl);
        when(restClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(Void.class)).thenReturn(null);

        // Act
        mediaService.removeMedia(mediaId);

        // Assert
        verify(restClient).delete();
    }

    // Test getMedia with large ID number
    @Test
    void test_get_media_with_large_id_number() {
        // Arrange
        Long mediaId = Long.MAX_VALUE;
        String mediaUrl = "http://media-service.com";
        
        NoFileMediaVm expectedResponse = new NoFileMediaVm(
            mediaId, 
            "image.jpg", 
            MediaType.IMAGE_JPEG_VALUE, 
            "100KB", 
            "http://media-service.com/image.jpg"
        );

        when(serviceUrlConfig.media()).thenReturn(mediaUrl);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(NoFileMediaVm.class)).thenReturn(expectedResponse);

        // Act
        NoFileMediaVm result = mediaService.getMedia(mediaId);

        // Assert
        assertNotNull(result);
        assertEquals(mediaId, result.id());
        verify(restClient).get();
    }
}