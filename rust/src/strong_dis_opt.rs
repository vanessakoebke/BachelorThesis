use std::time::{SystemTime, UNIX_EPOCH};

use ndarray::prelude::*;
use ndarray_linalg::blas::c::*;

/// =========================
/// strong_dis_mm (BLAS GEMM)
/// =========================
pub fn strong_dis_mm_opt(f: &[f64], n: usize, a: usize, b: usize) -> bool {
    let mut current = f.to_vec();
    let mut next = vec![0.0; n * n];

    for iter in 1..=(2 * n - 1) {
        let (ma, mb) = column_sums(&current, n, a, b);

        if (iter & 1 == 1 && ma < mb) || (iter & 1 == 0 && mb < ma) {
            return true;
        }

        if ma != mb {
            return false;
        }

        matmul_blas(&current, f, &mut next, n);

        std::mem::swap(&mut current, &mut next);
    }

    false
}

/// =========================
/// strong_dis_mv (BLAS GEMV)
/// =========================
pub fn strong_dis_mv_opt(f: &[f64], n: usize, a: usize, b: usize) -> bool {
    let mut rng = SimpleRng::from_time();

    let mut v1 = vec![0.0; n];
    let mut v2 = vec![0.0; n];

    let mut tmp1 = vec![0.0; n];
    let mut tmp2 = vec![0.0; n];

    v1[a] = 1.0;
    v2[b] = 1.0;

    for iter in 1..=(2 * n) {
        let r = rng.next_inclusive(1, 2 * n * 1000) as f64;

        matvec_blas(f, &v1, &mut tmp1, n, r);
        matvec_blas(f, &v2, &mut tmp2, n, r);

        std::mem::swap(&mut v1, &mut tmp1);
        std::mem::swap(&mut v2, &mut tmp2);

        let sum_v1: f64 = v1.iter().sum();
        let sum_v2: f64 = v2.iter().sum();

        if (sum_v1 - sum_v2).abs() > 1e-9 {
            return if iter & 1 == 1 {
                sum_v1 <= sum_v2
            } else {
                sum_v1 > sum_v2
            };
        }
    }

    false
}

/// =========================
/// BLAS: Matrix × Matrix
/// =========================
fn matmul_blas(a: &[f64], b: &[f64], c: &mut [f64], n: usize) {
    unsafe {
        cblas_dgemm(
            CblasRowMajor,
            CblasNoTrans,
            CblasNoTrans,
            n as i32,
            n as i32,
            n as i32,
            1.0,
            a.as_ptr(),
            n as i32,
            b.as_ptr(),
            n as i32,
            0.0,
            c.as_mut_ptr(),
            n as i32,
        );
    }
}

/// =========================
/// BLAS: Matrix × Vector
/// =========================
fn matvec_blas(matrix: &[f64], x: &[f64], y: &mut [f64], n: usize, scale: f64) {
    unsafe {
        cblas_dgemv(
            CblasRowMajor,
            CblasNoTrans,
            n as i32,
            n as i32,
            scale,
            matrix.as_ptr(),
            n as i32,
            x.as_ptr(),
            1,
            0.0,
            y.as_mut_ptr(),
            1,
        );
    }
}

/// =========================
/// column sums (unchanged, cheap)
/// =========================
fn column_sums(matrix: &[f64], n: usize, a: usize, b: usize) -> (f64, f64) {
    let mut sa = 0.0;
    let mut sb = 0.0;

    for row in matrix.chunks_exact(n) {
        sa += row[a];
        sb += row[b];
    }

    (sa, sb)
}

/// =========================
/// RNG (unchanged)
/// =========================
struct SimpleRng {
    state: u64,
}

impl SimpleRng {
    fn from_time() -> Self {
        let nanos = SystemTime::now()
            .duration_since(UNIX_EPOCH)
            .map(|d| d.as_nanos() as u64)
            .unwrap_or(0x9e37_79b9_7f4a_7c15);

        Self {
            state: nanos ^ 0x9e37_79b9_7f4a_7c15,
        }
    }

    fn next_u64(&mut self) -> u64 {
        let mut x = self.state;
        x ^= x << 13;
        x ^= x >> 7;
        x ^= x << 17;
        self.state = x;
        x
    }

    fn next_inclusive(&mut self, min: usize, max: usize) -> usize {
        min + (self.next_u64() as usize % (max - min + 1))
    }
}

/// =========================
/// tests
/// =========================
#[cfg(test)]
mod tests {
    use super::{strong_dis_mm_opt, strong_dis_mv_opt};

    #[test]
    fn mm_and_mv_return_true_for_simple_asymmetric_case() {
        let f = vec![
            0.0, 1.0,
            0.0, 0.0,
        ];

        assert!(strong_dis_mm_opt(&f, 2, 0, 1));
        assert!(strong_dis_mv_opt(&f, 2, 0, 1));
    }

    #[test]
    fn mm_and_mv_return_false_when_arguments_are_equal() {
        let f = vec![
            1.0, 0.0,
            0.0, 1.0,
        ];

        assert!(!strong_dis_mm_opt(&f, 2, 0, 0));
        assert!(!strong_dis_mv_opt(&f, 2, 0, 0));
    }
}