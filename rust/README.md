# Rust Runtime Analysis

This folder contains Rust ports of the Julia benchmark scripts for `StrongDis`.

Run from this directory:

```sh
cargo run --release --bin benchmark_strong_dis_mm
cargo run --release --bin benchmark_strong_dis_mv
cargo run --release --bin result_validation
```

Or from the repository root:

```sh
cargo run --release --manifest-path rust/Cargo.toml --bin benchmark_strong_dis_mm
cargo run --release --manifest-path rust/Cargo.toml --bin benchmark_strong_dis_mv
cargo run --release --manifest-path rust/Cargo.toml --bin result_validation
```

The benchmark outputs are written to:

- `../results/rust_results_MM.csv`
- `../results/rust_results_MV.csv`

`result_validation` compares `../results/java_results_ejml_nc.csv` with
`../results/rust_results_MV.csv` by default. You can pass two CSV paths to compare
different result files:

```sh
cargo run --release --bin result_validation -- ../results/java_results_ejml.csv ../results/rust_results_MM.csv
```
