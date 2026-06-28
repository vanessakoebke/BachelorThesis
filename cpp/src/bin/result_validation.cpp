#include "io.hpp"

#include <fstream>
#include <iostream>
#include <sstream>
#include <string>
#include <unordered_map>
#include <vector>

namespace {

std::vector<std::string> split_csv_row(const std::string& row) {
    std::vector<std::string> parts;
    std::stringstream stream(row);
    std::string part;
    while (std::getline(stream, part, ',')) {
        parts.push_back(part);
    }
    return parts;
}

std::unordered_map<std::string, std::string> read_results(const std::filesystem::path& path) {
    std::ifstream input(path);
    if (!input) {
        throw std::runtime_error("could not open result file: " + path.string());
    }

    std::unordered_map<std::string, std::string> results;
    std::string line;
    bool header = true;
    while (std::getline(input, line)) {
        if (header) {
            header = false;
            continue;
        }
        if (line.empty()) {
            continue;
        }

        const auto parts = split_csv_row(line);
        if (parts.size() < 3) {
            throw std::runtime_error("invalid result row in " + path.string() + ": " + line);
        }
        results[clean_filename(parts[0])] = parts[2];
    }

    return results;
}

} // namespace

int main(int argc, char** argv) {
    try {
        const auto root = repo_root();
        const auto left_path = argc > 1
            ? std::filesystem::path(argv[1])
            : root / "results" / "java_results_ejml_nc.csv";
        const auto right_path = argc > 2
            ? std::filesystem::path(argv[2])
            : root / "results" / "cpp_results_MV.csv";

        const auto left = read_results(left_path);
        const auto right = read_results(right_path);

        int errors = 0;
        for (const auto& [file, left_result] : left) {
            const auto right_result = right.find(file);
            if (right_result == right.end()) {
                std::cout << "Fehlt in Vergleichsdatei: " << file << '\n';
                ++errors;
                continue;
            }
            if (right_result->second != left_result) {
                std::cout << "Fehler in Datei: " << file
                          << " (" << left_result << " != " << right_result->second << ")\n";
                ++errors;
            }
        }

        if (errors == 0) {
            std::cout << "Vergleich abgeschlossen\n";
        } else {
            std::cout << "Vergleich abgeschlossen: " << errors << " Abweichung(en)\n";
        }
    } catch (const std::exception& error) {
        std::cerr << error.what() << '\n';
        return 1;
    }

    return 0;
}

