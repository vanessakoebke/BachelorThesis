# C++ Runtime Analysis

This folder contains C++ ports of the Julia benchmark scripts for `StrongDis`.

Build and run with CMake:

```sh
cmake -S cpp -B cpp/build -DCMAKE_BUILD_TYPE=Release
cmake --build cpp/build
./cpp/build/benchmark_strong_dis_mm
./cpp/build/benchmark_strong_dis_mv
./cpp/build/result_validation
```

Or from this folder with Make:

```sh
make
./build/benchmark_strong_dis_mm
./build/benchmark_strong_dis_mv
./build/result_validation
```

On macOS, the Makefile and CMake setup try to pick up the Command Line Tools SDK
automatically via `xcrun --show-sdk-path`.

The benchmark outputs are written to:

- `../results/cpp_results_MM.csv`
- `../results/cpp_results_MV.csv`

`result_validation` compares `../results/java_results_ejml_nc.csv` with
`../results/cpp_results_MV.csv` by default. You can pass two CSV paths to compare
different result files:

```sh
./cpp/build/result_validation results/java_results_ejml.csv results/cpp_results_MM.csv
```
