<!--- ( vim: set tw=120: ) --->

# Parallel Jar file read access benchmark

Benchmarks jar file read in JDK 22 with RandomAccessFile based original code and lock-free FileChannel based enhanced code.

## Comparison

### x86_64 - 4 core (8 hyperthreads) Intel x86_64 1185G7

<!--- benchmark:table:jarfile-x86_64:filter=^ParallelJarReadBenchmark\..*&compare=java-23-original-jar:basic test: --->

|Benchmark                                        |Mode|Units|java-23-original-jar|java-23-concurrent-jar|java-23-original-jar%|java-23-concurrent-jar%|
|:------------------------------------------------|:---|:----|-------------------:|---------------------:|--------------------:|----------------------:|
|ParallelJarReadBenchmark.bigCompressed_parallel  |avgt|ns/op|        63351938.756|          62644087.254|                   +0|                     -1|
|ParallelJarReadBenchmark.bigCompressed_single    |avgt|ns/op|        60384800.627|          60212176.588|                   +0|                     +0|
|ParallelJarReadBenchmark.bigStored_parallel      |avgt|ns/op|         9873453.966|           3092875.785|                   +0|                    -68|
|ParallelJarReadBenchmark.bigStored_single        |avgt|ns/op|         2057215.326|           1890039.132|                   +0|                     -8|
|ParallelJarReadBenchmark.smallCompressed_parallel|avgt|ns/op|           84874.515|             81285.933|                   +0|                     -4|
|ParallelJarReadBenchmark.smallCompressed_single  |avgt|ns/op|           55071.652|             55070.724|                   +0|                     +0|
|ParallelJarReadBenchmark.smallStored_parallel    |avgt|ns/op|           47339.434|             41219.983|                   +0|                    -12|
|ParallelJarReadBenchmark.smallStored_single      |avgt|ns/op|           23081.398|             22142.783|                   +0|                     -4|

Measured on 4 threads, lock-free FileChannel based enhanced code is 68% faster for stored big files, 12% for stored
small files and 1-3% faster for compressed files.  It should get likely better for more CPUs.

Measurement CPU: 4 core (8 hyperthreads) Intel x86_64 1185G7, run in 4 threads only at 1800MHz. 


### aarch64 - 32 core Graviton-3

<!--- benchmark:table:jarfile-aarch64:filter=^ParallelJarReadBenchmark\..*&compare=java-23-original-jar:basic test: --->

|Benchmark                                        |Mode|Units|java-23-original-jar|java-23-concurrent-jar|java-23-original-jar%|java-23-concurrent-jar%|
|:------------------------------------------------|:---|:----|-------------------:|---------------------:|--------------------:|----------------------:|
|ParallelJarReadBenchmark.bigCompressed_parallel  |avgt|ns/op|        36834776.405|          36083884.429|                   +0|                     -2|
|ParallelJarReadBenchmark.bigCompressed_single    |avgt|ns/op|        35898860.357|          35679958.897|                   +0|                     +0|
|ParallelJarReadBenchmark.bigStored_parallel      |avgt|ns/op|        54481754.667|           4897821.555|                   +0|                    -91|
|ParallelJarReadBenchmark.bigStored_single        |avgt|ns/op|         1317134.183|           1126491.515|                   +0|                    -14|
|ParallelJarReadBenchmark.smallCompressed_parallel|avgt|ns/op|          148053.938|            103852.740|                   +0|                    -29|
|ParallelJarReadBenchmark.smallCompressed_single  |avgt|ns/op|           39559.095|             39635.063|                   +0|                     +0|
|ParallelJarReadBenchmark.smallStored_parallel    |avgt|ns/op|          131200.164|             56400.555|                   +0|                    -57|
|ParallelJarReadBenchmark.smallStored_single      |avgt|ns/op|           19270.832|             18022.741|                   +0|                     -6|

Measured on 16 threads, lock-free FileChannel based enhanced code is 91% faster for stored big files, 57% faster for
stored small files and 2-29% for compressed files.

Measurement CPU: 32 core (32 threads) Graviton-3, run in 16 threads only at 2600MHz.


## Build & Run

Build:
```
mvn package
```

Run:
```
...path-to.../java -jar target/ -jar target/parallel-jar-benchmark
```
Note that there is no `.jar` extension at the end - the file is re-compressed into `target/parallel-jar-benchmark` without extension.

Recalculate benchmark table:
```
mvn dryuf-jmh-review:update-benchmarks
```


## Machine setup

```
sudo apt update && sudo apt install -y git && sudo apt install -y openjdk-21-jdk maven autoconf unzip zip build-essential libasound2-dev libcups2-dev libfontconfig1-dev libx11-dev libxext-dev libxrender-dev libxrandr-dev libxtst-dev libxt-dev
git clone https://github.com/kvr000/zbynek-jdk-tests.git
git clone -b parallel-zipfile https://github.com/kvr000/jdk.git
```

Build & run:
```
( cd jdk && git checkout parallel-zipfile && bash configure && make images ) && ( cd zbynek-jdk-tests/parallel-jar-benchmarks/ && mvn package && $HOME/jdk/build/*-release/images/jdk/bin/java -jar target/parallel-jar-benchmark )
```

## Raw data

### java 23 original-jar

<!--- benchmark:data:jarfile-x86_64:java-23-original-jar: --->

```
Benchmark                                          Mode  Cnt         Score          Error  Units
ParallelJarReadBenchmark.bigCompressed_parallel    avgt    3  63351938.756 ± 17745571.192  ns/op
ParallelJarReadBenchmark.bigCompressed_single      avgt    3  60384800.627 ±   943293.505  ns/op
ParallelJarReadBenchmark.bigStored_parallel        avgt    3   9873453.966 ±  2532257.230  ns/op
ParallelJarReadBenchmark.bigStored_single          avgt    3   2057215.326 ±   610658.491  ns/op
ParallelJarReadBenchmark.smallCompressed_parallel  avgt    3     84874.515 ±    16916.623  ns/op
ParallelJarReadBenchmark.smallCompressed_single    avgt    3     55071.652 ±    13505.492  ns/op
ParallelJarReadBenchmark.smallStored_parallel      avgt    3     47339.434 ±     7253.294  ns/op
ParallelJarReadBenchmark.smallStored_single        avgt    3     23081.398 ±     5404.455  ns/op
```

### java 23 concurrent-jar

<!--- benchmark:data:jarfile-x86_64:java-23-concurrent-jar: --->

```
Benchmark                                          Mode  Cnt         Score          Error  Units
ParallelJarReadBenchmark.bigCompressed_parallel    avgt    3  62644087.254 ± 42202443.186  ns/op
ParallelJarReadBenchmark.bigCompressed_single      avgt    3  60212176.588 ±  1514655.377  ns/op
ParallelJarReadBenchmark.bigStored_parallel        avgt    3   3092875.785 ±  1861047.964  ns/op
ParallelJarReadBenchmark.bigStored_single          avgt    3   1890039.132 ±   874230.960  ns/op
ParallelJarReadBenchmark.smallCompressed_parallel  avgt    3     81285.933 ±     1739.298  ns/op
ParallelJarReadBenchmark.smallCompressed_single    avgt    3     55070.724 ±     8785.030  ns/op
ParallelJarReadBenchmark.smallStored_parallel      avgt    3     41219.983 ±     7709.404  ns/op
ParallelJarReadBenchmark.smallStored_single        avgt    3     22142.783 ±    14288.910  ns/op
```

### Graviton3 java 22 original-jar

<!--- benchmark:data:jarfile-aarch64:java-23-original-jar: --->

```
Benchmark                                          Mode  Cnt         Score         Error  Units
ParallelJarReadBenchmark.bigCompressed_parallel    avgt    3  36834776.405 ± 2975818.906  ns/op
ParallelJarReadBenchmark.bigCompressed_single      avgt    3  35898860.357 ±  210751.311  ns/op
ParallelJarReadBenchmark.bigStored_parallel        avgt    3  54481754.667 ± 6724704.772  ns/op
ParallelJarReadBenchmark.bigStored_single          avgt    3   1317134.183 ±  129198.082  ns/op
ParallelJarReadBenchmark.smallCompressed_parallel  avgt    3    148053.938 ±    8087.848  ns/op
ParallelJarReadBenchmark.smallCompressed_single    avgt    3     39559.095 ±    5524.191  ns/op
ParallelJarReadBenchmark.smallStored_parallel      avgt    3    131200.164 ±  211294.551  ns/op
ParallelJarReadBenchmark.smallStored_single        avgt    3     19270.832 ±   12378.181  ns/op
```

### Graviton3 java 23 concurrent-jar

<!--- benchmark:data:jarfile-aarch64:java-23-concurrent-jar: --->

```
Benchmark                                          Mode  Cnt         Score         Error  Units
ParallelJarReadBenchmark.bigCompressed_parallel    avgt    3  36083884.429 ± 1428163.218  ns/op
ParallelJarReadBenchmark.bigCompressed_single      avgt    3  35679958.897 ±  563825.044  ns/op
ParallelJarReadBenchmark.bigStored_parallel        avgt    3   4897821.555 ±  749918.306  ns/op
ParallelJarReadBenchmark.bigStored_single          avgt    3   1126491.515 ±  142806.616  ns/op
ParallelJarReadBenchmark.smallCompressed_parallel  avgt    3    103852.740 ±    3521.892  ns/op
ParallelJarReadBenchmark.smallCompressed_single    avgt    3     39635.063 ±   19075.852  ns/op
ParallelJarReadBenchmark.smallStored_parallel      avgt    3     56400.555 ±    1334.652  ns/op
ParallelJarReadBenchmark.smallStored_single        avgt    3     18022.741 ±    8216.155  ns/op
```
