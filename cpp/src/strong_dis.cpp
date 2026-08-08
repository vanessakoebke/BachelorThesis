#include "strong_dis.hpp"

#include <cmath>
#include <numeric>

namespace {

double column_sum(const std::vector<double>& matrix, std::size_t n, std::size_t column) {
    double sum = 0.0;
    for (std::size_t row = 0; row < n; ++row) {
        sum += matrix[row * n + column];
    }
    return sum;
}

std::vector<double> multiply_square(
    const std::vector<double>& left,
    const std::vector<double>& right,
    std::size_t n
) {
    std::vector<double> product(n * n, 0.0);

    for (std::size_t i = 0; i < n; ++i) {
        for (std::size_t k = 0; k < n; ++k) {
            const double left_value = left[i * n + k];
            if (left_value == 0.0) {
                continue;
            }
            for (std::size_t j = 0; j < n; ++j) {
                product[i * n + j] += left_value * right[k * n + j];
            }
        }
    }

    return product;
}

std::vector<double> multiply_matrix_vector(
    const std::vector<double>& matrix,
    const std::vector<double>& vector,
    std::size_t n
) {
    std::vector<double> result(n, 0.0);

    for (std::size_t i = 0; i < n; ++i) {
        double sum = 0.0;
        for (std::size_t j = 0; j < n; ++j) {
            sum += matrix[i * n + j] * vector[j];
        }
        result[i] = sum;
    }

    return result;
}

} // namespace

bool strong_dis_mm(const std::vector<double>& f, std::size_t n, std::size_t a, std::size_t b) {
    auto m = f;

    for (std::size_t iter = 1; iter <= 2 * n - 1; ++iter) {
        const double ma = column_sum(m, n, a);
        const double mb = column_sum(m, n, b);

        if ((iter % 2 != 0 && ma < mb) || (iter % 2 == 0 && mb < ma)) {
            return true;
        }
        if (ma != mb) {
            return false;
        }

        m = multiply_square(m, f, n);
    }

    return false;
}

bool strong_dis_mv(const std::vector<double>& f, std::size_t n, std::size_t a, std::size_t b) {
    std::vector<double> v1(n, 0.0);
    std::vector<double> v2(n, 0.0);
    v1[a] = 1.0;
    v2[b] = 1.0;

    for (std::size_t iter = 1; iter <= 2 * n; ++iter) {
        v1 = multiply_matrix_vector(f, v1, n);
        v2 = multiply_matrix_vector(f, v2, n);

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
