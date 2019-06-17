package com.griddynamics.mergesort;

import com.griddynamics.mergesort.exception.IncorrectFileNameException;
import com.griddynamics.mergesort.util.FileToArrayProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

public class UtilsTest {

    private static final String TEST_FILE1_TXT = "test.txt";
    private static final String INCORRECT_FILENAME = "null.txt";

    private File file1;
    private File incorrectFile;

    @Before
    public void setUp() throws Exception {
        this.file1 = new File(requireNonNull(this.getClass().getClassLoader().getResource(TEST_FILE1_TXT)).toURI());
        this.incorrectFile = new File(INCORRECT_FILENAME);
    }

    @After
    public void tearDown() {
       this.file1 = null;
       this.incorrectFile = null;
    }

    @Test
    public void shouldReadAFileAndReturnAnArray() throws IOException, IncorrectFileNameException {
        var linesFromFile = FileToArrayProvider.readLines(file1);
        assertThat(linesFromFile)
                .as("An array shouldn't be empty")
                .isNotEmpty();
    }

    @Test(expected = IncorrectFileNameException.class)
    public void shouldThrowIncorrectFileNameException() throws IOException, IncorrectFileNameException {
        FileToArrayProvider.readLines(incorrectFile);
    }
}
