package test.accelerate.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import accelerate.batch.AccelerateBatch;
import accelerate.batch.AccelerateTask;
import accelerate.exception.AccelerateException;
import test.accelerate.AccelerateTestConfig;

/**
 * Junit test for {@link AccelerateBatch}
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 25-May-2015
 */
public class AccelerateBatchTest extends AccelerateTestConfig {
	/**
	 * 
	 */
	private static int taskCounter = 0;

	/**
	 *
	 */
	@Autowired
	private AccelerateBatch<AccelerateTask> tempMultiThreadedBatch = null;

	/**
	 *
	 */
	@Autowired
	private AccelerateBatch<AccelerateTask> tempSingleThreadedBatch = null;

	/**
	 * @return
	 */
	@Bean
	public static AccelerateBatch<AccelerateTask> tempMultiThreadedBatch() {
		AccelerateBatch<AccelerateTask> batch = new AccelerateBatch<>("tempMultiThreadedBatch", 10);
		return batch;
	}

	/**
	 * @return
	 */
	@Bean
	public static AccelerateBatch<AccelerateTask> tempSingleThreadedBatch() {
		AccelerateBatch<AccelerateTask> batch = new AccelerateBatch<>("tempSingleThreadedBatch", 10);
		return batch;
	}

	/**
	 * Test method for
	 * {@link accelerate.batch.AccelerateBatch#getAdvancedStatus()}
	 */
	@Test
	public void test01GetAdvancedStatus() {
		Assert.assertEquals(this.tempMultiThreadedBatch.getAdvancedStatus(),
				"{\"1.name\":\"tempMultiThreadedBatch\",\"2.multithreaded\":true,\"3.poolSize\":10,\"4.active\":true,\"5.completedTasks\":0,\"6.activeTasks\":0,\"7.waitingTasks\":0}",
				this.tempMultiThreadedBatch.getAdvancedStatus());
	}

	/**
	 * Test method for
	 * {@link accelerate.batch.AccelerateBatch#getSpringExecutor()}
	 */
	@Test
	public void test02GetSpringExecutor() {
		Assert.assertEquals(10, this.tempMultiThreadedBatch.getSpringExecutor().getMaxPoolSize());
	}

	/**
	 * Test method for
	 * {@link accelerate.batch.AccelerateBatch#getJavaExecutor()}
	 */
	@Test
	public void test03GetJavaExecutor() {
		Assert.assertFalse(this.tempMultiThreadedBatch.getJavaExecutor().isShutdown());
	}

	/**
	 * Test method for
	 * {@link accelerate.batch.AccelerateBatch#submitTasks(AccelerateTask[])}
	 *
	 * @throws AccelerateException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	public void test04SubmitTasksArray() throws AccelerateException, InterruptedException, ExecutionException {
		TestTask task = new TestTask("testSubmitTasksArray", 2000);
		this.tempMultiThreadedBatch.submitTasks(task);
		taskCounter++;
		task.getFuture().get();

		Assert.assertEquals(taskCounter, this.tempMultiThreadedBatch.getCompletedTaskCount());
	}

	/**
	 * Test method for
	 * {@link accelerate.batch.AccelerateBatch#submitTasks(java.util.List)}
	 *
	 * @throws AccelerateException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	public void test05SubmitTasksList() throws AccelerateException, InterruptedException, ExecutionException {
		TestTask task = new TestTask("testSubmitTasksList", 2000);

		List<AccelerateTask> taskList = new ArrayList<>();
		taskList.add(task);
		this.tempMultiThreadedBatch.submitTasks(taskList);
		taskCounter++;

		task.getFuture().get();

		Assert.assertEquals(taskCounter, this.tempMultiThreadedBatch.getCompletedTaskCount());
	}

	/**
	 * Test method for {@link accelerate.batch.AccelerateBatch#pause()} and
	 * {@link accelerate.batch.AccelerateBatch#resume()}
	 *
	 * @throws AccelerateException
	 * @throws InterruptedException
	 */
	@Test
	public void test06PauseAndResume() throws AccelerateException, InterruptedException {
		// wait for any pending tasks to complete
		while (this.tempMultiThreadedBatch.getActiveTaskCount() > 0) {
			TimeUnit.SECONDS.sleep(1);
			continue;
		}

		this.tempMultiThreadedBatch.pause();
		Assert.assertTrue(this.tempMultiThreadedBatch.isPaused());

		final TestTask task = new TestTask("testPauseAndResume", 2000);
		this.tempMultiThreadedBatch.submitTasks(task);
		taskCounter++;

		Assert.assertEquals(1, this.tempMultiThreadedBatch.getActiveTaskCount());

		this.tempMultiThreadedBatch.resume();

		try {
			task.getFuture().get();
		} catch (ExecutionException error) {
			error.printStackTrace();
		}

		Assert.assertEquals(0, this.tempMultiThreadedBatch.getActiveTaskCount());
	}

	/**
	 * Test method for {@link accelerate.batch.AccelerateBatch#shutdownNow()}
	 * 
	 * @throws AccelerateException
	 */
	@Test
	public void test07ShutdownNow() throws AccelerateException {
		this.tempMultiThreadedBatch.shutdownNow();
		Assert.assertFalse(this.tempMultiThreadedBatch.isActive());

		try {
			this.tempMultiThreadedBatch.submitTasks(new TestTask("testShutdown", 2));
			Assert.fail("submitTasks should fail when batch has been shutdown");
		} catch (AccelerateException error) {
			Assert.assertEquals(
					"Batch has been shutdown! Invoke activate() reinitialize the batch before submitting tasks",
					error.getMessage());
		}

		this.tempMultiThreadedBatch.activate();
		Assert.assertTrue(this.tempMultiThreadedBatch.isActive());

		try {
			this.tempMultiThreadedBatch.activate();
			Assert.fail("activate should fail when batch is already active");
		} catch (AccelerateException error) {
			Assert.assertEquals("Batch is already active! Invoke shutdown() to close the batch before reinitializing",
					error.getMessage());
		}

		try {
			this.tempMultiThreadedBatch.submitTasks(new ArrayList<>());
			Assert.fail("submitTasks should fail when empty list is provided");
		} catch (AccelerateException error) {
			Assert.assertEquals("No tasks provided!", error.getMessage());
		}

	}

	/**
	 * Test method for execution of
	 * {@link accelerate.batch.AccelerateBatch#shutdown(java.lang.String, long)}
	 *
	 * @throws AccelerateException
	 * @throws InterruptedException
	 */
	@Test
	public void test09Shutdown() throws AccelerateException, InterruptedException {
		this.tempMultiThreadedBatch.pause();
		this.tempMultiThreadedBatch.submitTasks(new TestTask("testShutdown", 2));

		this.tempMultiThreadedBatch.shutdown("SECONDS", 2);

		Assert.assertFalse(this.tempMultiThreadedBatch.getJavaExecutor().isTerminated());

		this.tempMultiThreadedBatch.resume();
		this.tempMultiThreadedBatch.getJavaExecutor().awaitTermination(5, TimeUnit.SECONDS);
		Assert.assertTrue(this.tempMultiThreadedBatch.getJavaExecutor().isTerminated());
	}

	/**
	 * Test method for different getters of task lists in
	 * {@link accelerate.batch.AccelerateBatch}
	 *
	 * @throws AccelerateException
	 * @throws InterruptedException
	 */
	@Test
	public void test10TaskLists() throws AccelerateException, InterruptedException {
		this.tempMultiThreadedBatch.activate();
		this.tempMultiThreadedBatch.pause();

		List<AccelerateTask> taskList = new ArrayList<>();
		IntStream.range(0, 12).forEach(idx -> taskList.add(new TestTask("test10WaitingTasks" + idx, 2)));
		this.tempMultiThreadedBatch.submitTasks(taskList);
		this.tempMultiThreadedBatch.shutdown("SECONDS", 1);

		while (this.tempMultiThreadedBatch.getActiveTasks().size() < 10) {
			TimeUnit.SECONDS.sleep(1);
			continue;
		}

		Assert.assertEquals(2, this.tempMultiThreadedBatch.getWaitingTaskCount());
		Assert.assertEquals(10, this.tempMultiThreadedBatch.getActiveTasks().size());
		Assert.assertEquals(12, this.tempMultiThreadedBatch.getTasks().size());

		this.tempMultiThreadedBatch.resume();

		try {
			taskList.get(11).getFuture().get();
		} catch (InterruptedException | ExecutionException error) {
			error.printStackTrace();
		}
	}

	/**
	 * Test method for execution of remaining methods in
	 * {@link accelerate.batch.AccelerateBatch}
	 *
	 * @throws AccelerateException
	 */
	@Test
	public void test11Misc() throws AccelerateException {
		this.tempSingleThreadedBatch.activate();
		Assert.assertFalse(this.tempSingleThreadedBatch.isMultiThreadingEnabled());
		Assert.assertEquals(10, this.tempMultiThreadedBatch.getThreadPoolSize());
	}

	/**
	 * Test implementation {@link AccelerateTask} for
	 * {@link AccelerateBatchTest}
	 *
	 * @author Rohit Narayanan
	 * @version 1.0 Initial Version
	 * @since 26-May-2015
	 */
	@SuppressWarnings("javadoc")
	private static class TestTask extends AccelerateTask {
		private int sleepTime = 0;

		public TestTask(String aTaskKey, int aSleepTime) {
			super(aTaskKey);
			this.sleepTime = aSleepTime;
		}

		@Override
		public void execute() throws AccelerateException {
			checkPause();

			try {
				Thread.sleep(this.sleepTime);
			} catch (InterruptedException error) {
				error.printStackTrace();
			}

			System.out.println(getTaskKey() + " - Exiting !!");
		}
	}
}
