package com.griddynamics.custom_threadpool;

import com.griddynamics.custom_threadpool.exception.CustomException;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class CustomThreadPoolTest {

    private static int DOUBLE_THREADS = 2;
    private static String CORRECT_ARGUMENT = "1000";
    private static String INCORRECT_ARGUMENT = "not_a_digit";

    @Test
    public void shouldRunUsingCorrectArgument() {
        AppCustomThreadPool.main(new String[]{CORRECT_ARGUMENT});
    }

    @Test(expected = CustomException.class)
    public void shouldThrowAnExceptionUsingIncorrectArgument() {
        AppCustomThreadPool.main(new String[]{INCORRECT_ARGUMENT});
    }

    @Test(expected = CustomException.class)
    public void shouldThrowAnExceptionUsingEmptyArgument() {
        AppCustomThreadPool.main(new String[]{" "});
    }

    @Test(expected = CustomException.class)
    public void shouldThrowAnExceptionUsingNullArgument() {
        AppCustomThreadPool.main(null);
    }

    @Test
    public void shouldRunUsingNegativeArgument() {
        AppCustomThreadPool.main(new String[]{"-1"});
    }

    @Test
    public void shouldRunUsingZeroArgument() {
        AppCustomThreadPool.main(new String[]{"0"});
    }

    @Test
    public void shouldRunUsingZeroThreadsNumber() {
        CustomThreadPool threadPool = new CustomThreadPool(0);
        for (int i = 0; i < DOUBLE_THREADS + 2; i++) {
            threadPool.submit(new MyTask(i), Integer.valueOf(CORRECT_ARGUMENT));
        }
        threadPool.stop();
    }

    @Test
    public void shouldRunInOneThread() {
        CustomThreadPool threadPool = new CustomThreadPool(1);
        for (int i = 0; i < DOUBLE_THREADS + 2; i++) {
            threadPool.submit(new MyTask(i), Integer.valueOf(CORRECT_ARGUMENT));
        }
        threadPool.stop();
    }

    @Test
    public void shouldReturnCorrectNumberOfThreads() {
        CustomThreadPool threadPool = new CustomThreadPool(10);
        assertThat(threadPool.getThreads().size()).isEqualTo(10);
    }

    @Test
    public void shouldReturnAnEmptyTasksList() {
        CustomThreadPool threadPool = new CustomThreadPool(2);
        for (int i = 0; i < DOUBLE_THREADS + 20; i++) {
            threadPool.submit(new MyTask(i), Integer.valueOf(CORRECT_ARGUMENT));
        }
        threadPool.stop();

        assertThat(threadPool.getTasks()).isEmpty();
    }

    @Test
    public void shouldReturnCorrectDelayTime() {
        CustomThreadPool threadPool = new CustomThreadPool(2);
        for (int i = 0; i < DOUBLE_THREADS + 2; i++) {
            threadPool.submit(new MyTask(i), Integer.valueOf(CORRECT_ARGUMENT));
        }
        threadPool.stop();
        assertThat(threadPool.getDelayMills()).isEqualTo(1000);
    }
}
