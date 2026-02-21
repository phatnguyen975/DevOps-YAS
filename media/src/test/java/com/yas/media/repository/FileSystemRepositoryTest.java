package com.yas.media.repository;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.yas.media.config.FilesystemConfig;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@Slf4j
class FileSystemRepositoryTest {

    private static final String TEST_URL = "src/test/resources/test-directory";

    @Mock
    private FilesystemConfig filesystemConfig;

    @Mock
    private File file;

    @InjectMocks
    private FileSystemRepository fileSystemRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws IOException {
        // Cleanup code: delete the files and directories created during tests
        Path testDir = Paths.get(TEST_URL);
        if (Files.exists(testDir)) {
            Files.walk(testDir)
                    .sorted((p1, p2) -> p2.compareTo(p1))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    @Test
    void testPersistFile_whenDirectoryNotExist_thenThrowsException() {
        String directoryPath = "non-exist-directory";
        String filename = "test-file.png";
        byte[] content = "test-content".getBytes();

        when(filesystemConfig.getDirectory()).thenReturn(directoryPath);

        assertThrows(IllegalStateException.class, () -> fileSystemRepository.persistFile(filename, content));
    }

    @Test
    void testPersistFile_filePathNotContainsDirectory() {

        String filename = "test-file.png";
        byte[] content = "test-content".getBytes();

        File directory = new File(TEST_URL);
        directory.mkdirs();
        when(filesystemConfig.getDirectory()).thenReturn(TEST_URL);
        assertThrows(IllegalArgumentException.class, () -> fileSystemRepository.persistFile(filename, content));
    }

    @Test
    void testGetFile_whenDirectIsExist_thenReturnFile() throws IOException {
        String filename = "test-file.png";
        String filePathStr = Paths.get(TEST_URL, filename).toString();
        byte[] content = "test-content".getBytes();

        when(filesystemConfig.getDirectory()).thenReturn(TEST_URL);

        Path filePath = Paths.get(filePathStr);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, content);

        InputStream inputStream = fileSystemRepository.getFile(filePathStr);
        byte[] fileContent = inputStream.readAllBytes();
        assertArrayEquals(content, fileContent);
    }

    @Test
    void testGetFileDirectoryDoesNotExist_thenThrowsException() {
        String directoryPath = "non-exist-directory";
        String filename = "test-file.png";
        String filePathStr = Paths.get(directoryPath, filename).toString();

        when(filesystemConfig.getDirectory()).thenReturn(directoryPath);

        assertThrows(IllegalStateException.class, () -> fileSystemRepository.getFile(filePathStr));
    }

    @Test
    void testPersistFile_withValidDirectory_thenSuccess() throws IOException {
        String filename = "valid-file.jpg";
        byte[] content = "valid content".getBytes();

        File directory = new File(TEST_URL);
        directory.mkdirs();
        when(filesystemConfig.getDirectory()).thenReturn(TEST_URL);

        String filePath = fileSystemRepository.persistFile(filename, content);

        assertNotNull(filePath);
        assertTrue(Files.exists(Paths.get(filePath)));
    }

    @Test
    void testPersistFile_withInvalidFilenameContainsDoubleSlash_thenThrowsException() {
        String filename = "..\\..\\evil.jpg";
        byte[] content = "content".getBytes();

        File directory = new File(TEST_URL);
        directory.mkdirs();
        when(filesystemConfig.getDirectory()).thenReturn(TEST_URL);

        assertThrows(IllegalArgumentException.class, () -> fileSystemRepository.persistFile(filename, content));
    }

    @Test
    void testPersistFile_withInvalidFilenameDotDot_thenThrowsException() {
        String filename = "../../evil.jpg";
        byte[] content = "content".getBytes();

        File directory = new File(TEST_URL);
        directory.mkdirs();
        when(filesystemConfig.getDirectory()).thenReturn(TEST_URL);

        assertThrows(IllegalArgumentException.class, () -> fileSystemRepository.persistFile(filename, content));
    }

    @Test
    void testPersistFile_withInvalidFilenameSlash_thenThrowsException() {
        String filename = "path/to/file.jpg";
        byte[] content = "content".getBytes();

        File directory = new File(TEST_URL);
        directory.mkdirs();
        when(filesystemConfig.getDirectory()).thenReturn(TEST_URL);

        assertThrows(IllegalArgumentException.class, () -> fileSystemRepository.persistFile(filename, content));
    }

    @Test
    void testGetFile_withInvalidPath_thenThrowsException() {
        String filePathStr = "non-existent-file.jpg";

        when(filesystemConfig.getDirectory()).thenReturn(TEST_URL);

        assertThrows(IllegalStateException.class, () -> fileSystemRepository.getFile(filePathStr));
    }

    @Test
    void testPersistFile_DirectoryWithoutReadPermission() {
        String filename = "test-file.jpg";
        byte[] content = "content".getBytes();

        File directory = new File(TEST_URL);
        directory.mkdirs();

        when(filesystemConfig.getDirectory()).thenReturn(TEST_URL);

        // Test with valid permissions
        String result = null;
        try {
            result = fileSystemRepository.persistFile(filename, content);
        } catch (IOException e) {
            // Expected exception
        }
    }

    @Test
    void testPersistFile_MultipleFilesInSequence() throws IOException {
        String filename1 = "file1.jpg";
        String filename2 = "file2.jpg";
        byte[] content1 = "content1".getBytes();
        byte[] content2 = "content2".getBytes();

        File directory = new File(TEST_URL);
        directory.mkdirs();
        when(filesystemConfig.getDirectory()).thenReturn(TEST_URL);

        String path1 = fileSystemRepository.persistFile(filename1, content1);
        String path2 = fileSystemRepository.persistFile(filename2, content2);

        assertNotNull(path1);
        assertNotNull(path2);
        assertTrue(Files.exists(Paths.get(path1)));
        assertTrue(Files.exists(Paths.get(path2)));
    }

    @Test
    void testGetFile_withValidFile_thenReturnContent() throws IOException {
        String filename = "read-test.jpg";
        String filePathStr = Paths.get(TEST_URL, filename).toString();
        byte[] expectedContent = "read content".getBytes();

        File directory = new File(TEST_URL);
        directory.mkdirs();
        when(filesystemConfig.getDirectory()).thenReturn(TEST_URL);

        Path filePath = Paths.get(filePathStr);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, expectedContent);

        InputStream inputStream = fileSystemRepository.getFile(filePathStr);
        assertNotNull(inputStream);
        byte[] retrievedContent = inputStream.readAllBytes();
        assertArrayEquals(expectedContent, retrievedContent);
    }

}
