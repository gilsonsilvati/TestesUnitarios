package br.com.teste.domain.runners;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;

public class ParallelRunner extends BlockJUnit4ClassRunner {

	public ParallelRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
		
		setScheduler(new ThreadPoll());
	}
	
	private static class ThreadPoll implements RunnerScheduler {
		
		private ExecutorService executor;
		
		public ThreadPoll() {
			executor = Executors.newFixedThreadPool(5);
		}

		@Override
		public void schedule(Runnable run) {
			executor.submit(run);
		}

		@Override
		public void finished() {
			executor.shutdown();
			
			try {
				executor.awaitTermination(10, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		
	}

}
