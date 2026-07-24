#include "strong_dis_opt.hpp"

#include <Accelerate/Accelerate.h>

#include <chrono>
#include <cmath>
#include <numeric>
#include <random>

namespace {

std::pair<double, double> column_sums(
    const std::vector<double>& matrix,
    std::size_t n,
    std::size_t a,
    std::size_t b
) {
    double sum_a = 0.0;
    double sum_b = 0.0;

    for (std::size_t row = 0; row < n; ++row) {
        sum_a += matrix[row * n + a];
        sum_b += matrix[row * n + b];
    }

    return {sum_a, sum_b};
}

void matmul_blas(
    const std::vector<double>& left,
    const std::vector<double>& right,
    std::vector<double>& result,
    std::size_t n
) {
    cblas_dgemm(
        CblasRowMajor,
        CblasNoTrans,
        CblasNoTrans,
        static_cast<int>(n),
        static_cast<int>(n),
        static_cast<int>(n),
        1.0,
        left.data(),
        static_cast<int>(n),
        right.data(),
        static_cast<int>(n),
        0.0,
        result.data(),
        static_cast<int>(n)
    );
}

void matvec_blas(
    const std::vector<double>& matrix,
    const std::vector<double>& vector,
    std::vector<double>& result,
    std::size_t n,
    double scale
) {
    cblas_dgemv(
        CblasRowMajor,
        CblasNoTrans,
        static_cast<int>(n),
        static_cast<int>(n),
        scale,
        matrix.data(),
        static_cast<int>(n),
        vector.data(),
        1,
        0.0,
        result.data(),
        1
    );
}

std::mt19937_64 make_rng() {
    const auto seed = static_cast<std::uint64_t>(
        std::chrono::high_resolution_clock::now().time_since_epoch().count()
    );
    return std::mt19937_64(seed);
}

} // namespace

bool strong_dis_mm_opt(const std::vector<double>& f, std::size_t n, std::size_t a, std::size_t b) {
    auto current = f;
    std::vector<double> next(n * n, 0.0);

    for (std::size_t iter = 1; iter <= 2 * n - 1; ++iter) {
        const auto [ma, mb] = column_sums(current, n, a, b);

        if ((iter % 2 != 0 && ma < mb) || (iter % 2 == 0 && mb < ma)) {
            return true;
        }
        if (ma != mb) {
            return false;
        }

        matmul_blas(current, f, next, n);
        current.swap(next);
    }

    return false;
}

bool strong_dis_mv_opt(const std::vector<double>& f, std::size_t n, std::size_t a, std::size_t b) {
    auto rng = make_rng();
    std::uniform_int_distribution<std::size_t> distribution(1, 2 * n * 1000);

    std::vector<double> v1(n, 0.0);
    std::vector<double> v2(n, 0.0);
    std::vector<double> tmp1(n, 0.0);
    std::vector<double> tmp2(n, 0.0);
    v1[a] = 1.0;
    v2[b] = 1.0;

    for (std::size_t iter = 1; iter <= 2 * n; ++iter) {
        const double r = static_cast<double>(distribution(rng));
        matvec_blas(f, v1, tmp1, n, r);
        matvec_blas(f, v2, tmp2, n, r);

        v1.swap(tmp1);
        v2.swap(tmp2);

        const double sum_v1 = std::accumulate(v1.begin(), v1.end(), 0.0);
        const double sum_v2 = std::accumulate(v2.begin(), v2.end(), 0.0);

        if (std::abs(sum_v1 - sum_v2) > 1e-9) {
            if (iter % 2 != 0) {
                return sum_v1 <= sum_v2;
            }
            return sum_v1 > sum_v2;
        }
    }

    return false;
}

