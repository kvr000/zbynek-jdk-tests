package com.github.kvr000.jdktest.paralleljarbenchmark;


import lombok.SneakyThrows;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;


@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS, batchSize = 1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Fork(value = 1)
public class ParallelJarReadBenchmark
{
	public static final int PARALLEL = Math.max(Runtime.getRuntime().availableProcessors() / 2, 1);

	@State(Scope.Benchmark)
	public static class Shared
	{
		ExecutorService executor;

		@Setup(Level.Trial)
		public void setup()
		{
			executor = Executors.newFixedThreadPool(PARALLEL);
		}

		@TearDown
		public void teardown()
		{
			executor.close();
		}
	}

	@Benchmark
	public void bigCompressed_single(Blackhole blackhole, Shared state)
	{
		runGeneral(blackhole, state, 1, "InputCompressed8M.txt");
	}

	@Benchmark
	public void bigCompressed_parallel(Blackhole blackhole, Shared state)
	{
		runGeneral(blackhole, state, PARALLEL, "InputCompressed8M.txt");
	}

	@Benchmark
	public void bigStored_single(Blackhole blackhole, Shared state)
	{
		runGeneral(blackhole, state, 1, "InputStored8M.txt");
	}

	@Benchmark
	public void bigStored_parallel(Blackhole blackhole, Shared state)
	{
		runGeneral(blackhole, state, PARALLEL, "InputStored8M.txt");
	}

	@Benchmark
	public void smallCompressed_single(Blackhole blackhole, Shared state)
	{
		runGeneral(blackhole, state, 1, "InputCompressed4K.txt");
	}

	@Benchmark
	public void smallCompressed_parallel(Blackhole blackhole, Shared state)
	{
		runGeneral(blackhole, state, PARALLEL, "InputCompressed4K.txt");
	}

	@Benchmark
	public void smallStored_single(Blackhole blackhole, Shared state)
	{
		runGeneral(blackhole, state, 1, "InputStored4K.txt");
	}

	@Benchmark
	public void smallStored_parallel(Blackhole blackhole, Shared state)
	{
		runGeneral(blackhole, state, PARALLEL, "InputStored4K.txt");
	}

	private void runGeneral(Blackhole blackhole, Shared state, int parallel, String filename)
	{
		IntStream.range(0, parallel)
			.mapToObj(i -> CompletableFuture.supplyAsync(() -> readResource(filename), state.executor))
			.toList()
			.forEach(CompletableFuture::join);
	}

	@SneakyThrows
	private int readResource(String filename)
	{
		int size = 0;
		byte[] buf = new byte[16384];
		try (InputStream is = Objects.requireNonNull(ParallelJarReadBenchmark.class.getResourceAsStream(filename))) {
			int read;
			while ((read = is.read(buf)) > 0) {
				size += read;
			}
		}
		return size;
	}
}
