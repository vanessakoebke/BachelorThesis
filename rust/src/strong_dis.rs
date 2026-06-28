use std::time::{SystemTime, UNIX_EPOCH};

pub fn strong_dis_mm(f: &[f64], n: usize, a: usize, b: usize) -> bool {
    let mut m = f.to_vec();

    for iter in 1..=(2 * n - 1) {
        let ma = column_sum(&m, n, a);
        let mb = column_sum(&m, n, b);

        if (iter % 2 != 0 && ma < mb) || (iter % 2 == 0 && mb < ma) {
            return true;
        }
        if ma != mb {
            return false;
        }

        m = multiply_square(&m, f, n);
    }

    false
}

pub fn strong_dis_mv(f: &[f64], n: usize, a: usize, b: usize) -> bool {
    let mut rng = SimpleRng::from_time();
    let mut v1 = vec![0.0; n];
    let mut v2 = vec![0.0; n];
    v1[a] = 1.0;
    v2[b] = 1.0;

    for iter in 1..=(2 * n) {
        let r = rng.next_inclusive(1, 2 * n * 1000) as f64;
        v1 = multiply_matrix_vector_scaled(f, &v1, n, r);
        v2 = multiply_matrix_vector_scaled(f, &v2, n, r);

        let sum_v1: f64 = v1.iter().sum();
        let sum_v2: f64 = v2.iter().sum();

        if (sum_v1 - sum_v2).abs() > 1e-9 {
            if iter % 2 != 0 {
                return sum_v1 <= sum_v2;
            }
            return sum_v1 > sum_v2;
        }
    }

    false
}

fn column_sum(matrix: &[f64], n: usize, column: usize) -> f64 {
    (0..n).map(|row| matrix[row * n + column]).sum()
}

fn multiply_square(left: &[f64], right: &[f64], n: usize) -> Vec<f64> {
    let mut product = vec![0.0; n * n];

    for i in 0..n {
        for k in 0..n {
            let left_value = left[i * n + k];
            if left_value == 0.0 {
                continue;
            }
            for j in 0..n {
                product[i * n + j] += left_value * right[k * n + j];
            }
        }
    }

    product
}

fn multiply_matrix_vector_scaled(matrix: &[f64], vector: &[f64], n: usize, scale: f64) -> Vec<f64> {
    let mut result = vec![0.0; n];

    for i in 0..n {
        let mut sum = 0.0;
        for j in 0..n {
            sum += matrix[i * n + j] * vector[j];
        }
        result[i] = sum * scale;
    }

    result
}

struct SimpleRng {
    state: u64,
}

impl SimpleRng {
    fn from_time() -> Self {
        let nanos = SystemTime::now()
            .duration_since(UNIX_EPOCH)
            .map(|duration| duration.as_nanos() as u64)
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

#[cfg(test)]
mod tests {
    use super::{strong_dis_mm, strong_dis_mv};

    #[test]
    fn mm_and_mv_return_true_for_simple_asymmetric_case() {
        let f = vec![0.0, 1.0, 0.0, 0.0];

        assert!(strong_dis_mm(&f, 2, 0, 1));
        assert!(strong_dis_mv(&f, 2, 0, 1));
    }

    #[test]
    fn mm_and_mv_return_false_when_arguments_are_equal() {
        let f = vec![1.0, 0.0, 0.0, 1.0];

        assert!(!strong_dis_mm(&f, 2, 0, 0));
        assert!(!strong_dis_mv(&f, 2, 0, 0));
    }
}
