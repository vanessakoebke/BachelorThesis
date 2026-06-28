#include "io.hpp"
#include "strong_dis.hpp"

#include <chrono>
#include <fstream>
#include <iostream>

int main() {
    try {
        const auto root = repo_root();
        const auto files = csv_instance_files(root / "instances");
        std::filesystem::create_directories(root / "results");

        std::ofstream output(root / "results" / "cpp_results_MV.csv");
        output << "file,time,result\n";
        output << std::boolalpha;

        for (const auto& file : files) {
            std::cout << "Benchmarking " << file << '\n';
            const auto instance = load_instance(file);

            strong_dis_mv(instance.matrix, instance.n, instance.a, instance.b);

            const auto start = std::chrono::steady_clock::now();
            const bool result = strong_dis_mv(instance.matrix, instance.n, instance.a, instance.b);
            const auto end = std::chrono::steady_clock::now();
            const auto elapsed = std::chrono::duration_cast<std::chrono::nanoseconds>(end - start).count();

            output << file.string() << ',' << elapsed << ',' << result << '\n';
        }
    } catch (const std::exception& error) {
        std::cerr << error.what() << '\n';
        return 1;
    }

    return 0;
}

