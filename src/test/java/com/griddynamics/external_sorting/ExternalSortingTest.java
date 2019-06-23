package com.griddynamics.external_sorting;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.google.common.io.Files.copy;
import static java.io.File.createTempFile;
import static java.lang.System.out;
import static java.nio.file.Files.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExternalSortingTest {

    private ExternalSort externalSort = new ExternalSort();

    private static final List<String> SAMPLE_LIST = List.of("b", "c", "d", "h", "u", "z", "x", "y", "c", "w", "j", "a");
    private static final List<String> EXPECTED_SORTED_LIST_BY_CHARS = List.of("a", "b", "c", "c", "d", "h", "j", "u", "w", "x", "y", "z");

    private static final String FILE_1_TXT = "test_file1.txt";
    private static final String FILE_2_TXT = "test_file2.txt";
    private static final String FILE_3_TXT = "test_file3.txt";
    private static final String FILE_4_TXT = "test_file4.txt";
    private static final String TEST_RESULT_FILE = "test_result.txt";

    private File file1;
    private File file2;
    private File file3;
    private File file4;
    private File expectedResultsFile_small;
    private List<File> fileList;

    @Before
    public void setUp() throws Exception {
        this.fileList = new ArrayList<>(3);
        this.file1 = new File(this.getClass().getClassLoader()
                .getResource(FILE_1_TXT).toURI());
        this.file2 = new File(this.getClass().getClassLoader()
                .getResource(FILE_2_TXT).toURI());
        this.file3 = new File(this.getClass().getClassLoader()
                .getResource(FILE_3_TXT).toURI());
        this.file4 = new File(this.getClass().getClassLoader()
                .getResource(FILE_4_TXT).toURI());

        this.expectedResultsFile_small = new File(this.getClass().getClassLoader()
                .getResource(TEST_RESULT_FILE).toURI());

        File tmpFile1 = createTempFile(this.file1.getPath(), ".tmp");
        File tmpFile2 = createTempFile(this.file2.getPath(), ".tmp");

        copy(this.file1, tmpFile1);
        copy(this.file2, tmpFile2);

        this.fileList.add(tmpFile1);
        this.fileList.add(tmpFile2);
    }

    @After
    public void tearDown() {
        this.file1 = null;
        this.file2 = null;
        this.file3 = null;

        this.expectedResultsFile_small = null;
        this.fileList.clear();
        this.fileList = null;
    }

    @Test
    public void shouldRunSuccessfully() throws Exception {
        AppCustomSorting.main(new String[]{});
    }

    @Test
    public void shouldRunSuccessfullyWithParametersAndWithoutFilePaths() throws Exception {
        AppCustomSorting.main(new String[]{""});
        AppCustomSorting.main(new String[]{"-p"});
        AppCustomSorting.main(new String[]{"-h"});
        AppCustomSorting.main(new String[]{"-d"});
    }

    @Test
    public void shouldRunSuccessfullyWithAllParametersAndEmptyFiles() throws Exception {
        File inputFile = Files.createTempFile("input", ".tmp").toFile();
        File outputFile = Files.createTempFile("output", ".tmp").toFile();
        inputFile.deleteOnExit();
        outputFile.deleteOnExit();

        AppCustomSorting.main(new String[]{"-p", "-d", inputFile.getAbsolutePath(), outputFile.getAbsolutePath()});
    }

    @Test
    public void shouldRunTheParallelSorting() throws IOException {
        // given
        ExternalSort spyExternalSort = spy(externalSort);
        assertThat(spyExternalSort.isParallel()).isFalse();

        ParallelMergeSort spyParallelMergeSort = spy(spyExternalSort.getParallelMergeSort());
        spyExternalSort.setParallelMergeSort(spyParallelMergeSort);
        assertThat(spyExternalSort.isParallel()).isFalse();

        // when
        spyExternalSort.setParallel(true);
        verify(spyExternalSort).setParallel(true);
        assertThat(spyExternalSort.isParallel()).isTrue();

        spyExternalSort.sortAndWrite(EXPECTED_SORTED_LIST_BY_CHARS);

        //then
        verify(spyExternalSort.getParallelMergeSort(), atLeastOnce())
                .sort(EXPECTED_SORTED_LIST_BY_CHARS.toArray(String[]::new));
    }

    @Test
    public void shouldNotRunTheParallelSorting() throws IOException {
        // given
        ExternalSort spyExternalSort = spy(externalSort);
        assertThat(spyExternalSort.isParallel()).isFalse();

        ParallelMergeSort spyParallelMergeSort = spy(spyExternalSort.getParallelMergeSort());
        spyExternalSort.setParallelMergeSort(spyParallelMergeSort);
        assertThat(spyExternalSort.isParallel()).isFalse();

        // when
        spyExternalSort.setParallel(false);
        verify(spyExternalSort).setParallel(false);
        assertThat(spyExternalSort.isParallel()).isFalse();

        List<String> stub = new ArrayList<>(SAMPLE_LIST);
        spyExternalSort.sortAndWrite(stub);

        // then
        verify(spyExternalSort.getParallelMergeSort(), never())
                .sort(stub.toArray(String[]::new));
    }

    @Test
    public void shouldSortAndSaveDataIsNotParallel() throws IOException {
        externalSort.setParallel(false);
        List<String> data = new ArrayList<>(SAMPLE_LIST);
        File testFile = externalSort.sortAndWrite(data);
        testFile.deleteOnExit();

        assertThat(testFile.exists())
                .as("The file: " + testFile.toString() + "should be existed")
                .isTrue();
        assertThat(testFile).as("The file: " + testFile.toString() + "shouldn't be null")
                .isNotNull();
        assertThat(size(testFile.toPath()))
                .as("The file: " + testFile.toString() + "shouldn't be empty")
                .isGreaterThan(0L);
    }

    @Test
    public void shouldSortAndSaveDataIsParallel() throws IOException {
        externalSort.setParallel(true);
        File testFile = externalSort.sortAndWrite(SAMPLE_LIST);
        testFile.deleteOnExit();

        assertThat(testFile.exists())
                .as("The file: " + testFile.toString() + " should be existed")
                .isTrue();
        assertThat(testFile).as("The file: " + testFile.toString() + " shouldn't be null")
                .isNotNull();
        assertThat(size(testFile.toPath()))
                .as("The file: " + testFile.toString() + " shouldn't be empty")
                .isGreaterThan(0L);
    }

    @Test
    public void shouldSortDataCorrectlyIsNotParallel() throws IOException {
        externalSort.setParallel(false);
        List<String> data = new ArrayList<>(SAMPLE_LIST);
        File testFile = externalSort.sortAndWrite(data);
        testFile.deleteOnExit();

        Iterator<String> sortedData = lines(testFile.toPath()).iterator();
        List<String> actualResult = new ArrayList<>();
        while (sortedData.hasNext()) {
            actualResult.add(sortedData.next());
        }
        assertThat(actualResult)
                .as("The array: " + actualResult.toString()
                        + " should be the same as " + EXPECTED_SORTED_LIST_BY_CHARS.toString())
                .isEqualTo(EXPECTED_SORTED_LIST_BY_CHARS);
    }

    @Test
    public void shouldSortDataCorrectlyIsParallel() throws IOException {
        externalSort.setParallel(true);
        List<String> data = new ArrayList<>(SAMPLE_LIST);
        File testFile = externalSort.sortAndWrite(data);
        testFile.deleteOnExit();

        Iterator<String> sortedData = lines(testFile.toPath()).iterator();
        List<String> actualResult = new ArrayList<>();
        while (sortedData.hasNext()) {
            actualResult.add(sortedData.next());
        }
        assertThat(actualResult)
                .as("The array: " + actualResult.toString()
                        + " should be the same as " + EXPECTED_SORTED_LIST_BY_CHARS.toString())
                .isEqualTo(EXPECTED_SORTED_LIST_BY_CHARS);
    }

    @Test
    public void shouldHasCorrectResultAfterMerging() throws IOException {
        File mergedFile = Files.createTempFile("TempResult", ".tmp").toFile();
        mergedFile.deleteOnExit();

        externalSort.mergeSortedFiles(fileList, mergedFile);

        List<String> actualResults = readAllLines(mergedFile.toPath());
        List<String> expectedResults = readAllLines(expectedResultsFile_small.toPath());

        assertThat(actualResults)
                .as("The merged data: " + actualResults + " should be the same as " + expectedResults)
                .isEqualTo(expectedResults);
    }

    @Test
    public void shouldHasCorrectResultAfterSoringAndDividing() throws IOException {
        List<File> sortedFiles = externalSort.sortAndDivide(file3);
        List<String> actualResults = readAllLines(sortedFiles.get(0).toPath());
        List<String> expectedResults = readAllLines(expectedResultsFile_small.toPath());

        assertThat(actualResults)
                .as("The divided data: " + actualResults + " should be the same as " + expectedResults)
                .isEqualTo(expectedResults);
    }

    @Test
    public void shouldReturnAnEmptyFileAfterSortingAndSavingIsNotParallel() throws IOException {
        externalSort.setParallel(false);
        File inputFile = Files.createTempFile("emptyInput", ".tmp").toFile();
        File outputFile = Files.createTempFile("outputEmpty", ".tmp").toFile();
        inputFile.deleteOnExit();
        outputFile.deleteOnExit();

        List<File> dividedLists = externalSort.sortAndDivide(inputFile);
        externalSort.mergeSortedFiles(dividedLists, outputFile);

        assertThat(size(outputFile.toPath()))
                .as("The file: " + outputFile.getAbsolutePath() + " should be empty")
                .isEqualTo(0);
    }

    @Test
    public void shouldReturnAnEmptyFileAfterSortingAndSavingIsParallel() throws IOException {
        externalSort.setParallel(true);
        File inputFile = Files.createTempFile("emptyInput", ".tmp").toFile();
        File outputFile = Files.createTempFile("outputEmpty", ".tmp").toFile();
        inputFile.deleteOnExit();
        outputFile.deleteOnExit();

        List<File> dividedLists = externalSort.sortAndDivide(inputFile);
        externalSort.mergeSortedFiles(dividedLists, outputFile);

        assertThat(size(outputFile.toPath()))
                .as("The file: " + outputFile.getAbsolutePath() + " should be empty")
                .isEqualTo(0);
    }

    @Test
    public void shouldNotLoseDataIsParallelSorting() throws IOException {
        externalSort.setParallel(true);
        File tempResultsFile = Files.createTempFile("tempResult", ".tmp").toFile();
        out.println(tempResultsFile.getAbsolutePath());

        List<File> dividedLists = externalSort.sortAndDivide(file4);
        out.println("List size is: " + dividedLists.size());
        externalSort.mergeSortedFiles(dividedLists, tempResultsFile);

        List<String> expectedResult = readAllLines(file4.toPath());
        List<String> actualResult = readAllLines(tempResultsFile.toPath());

        assertThat(actualResult.size())
                .as("Sorted data should has the same size than original data")
                .isEqualTo(expectedResult.size());
    }

    @Test
    public void shouldNotLoseDataIsNotParallelSorting() throws IOException {
        externalSort.setParallel(false);
        File tempResultsFile = Files.createTempFile("tempResult", ".tmp").toFile();
        tempResultsFile.deleteOnExit();

        List<File> dividedLists = externalSort.sortAndDivide(file4);
        externalSort.mergeSortedFiles(dividedLists, tempResultsFile);

        List<String> expectedResult = readAllLines(file4.toPath());
        List<String> actualResult = readAllLines(tempResultsFile.toPath());

        assertThat(actualResult.size())
                .as("Sorted data should has the same size than original data")
                .isEqualTo(expectedResult.size());
    }
}
