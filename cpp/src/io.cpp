#include "io.hpp"

#include <algorithm>
#include <fstream>
#include <sstream>
#include <stdexcept>

Instance load_instance(const std::filesystem::path& path) {
    std::ifstream input(path);
    if (!input) {
        throw std::runtime_error("could not open instance file: " + path.string());
    }

    std::string header;
    if (!std::getline(input, header)) {
        throw std::runtime_error("missing instance header: " + path.string());
    }

    std::replace(header.begin(), header.end(), ',', ' ');
    std::istringstream header_stream(header);

    Instance instance;
    if (!(header_stream >> instance.n >> instance.a >> instance.b)) {
        throw std::runtime_error("invalid instance header: " + path.string());
    }

    instance.matrix.assign(instance.n * instance.n, 0.0);
    for (std::size_t i = 0; i < instance.n; ++i) {
        std::string line;
        if (!std::getline(input, line)) {
            throw std::runtime_error("missing matrix row in: " + path.string());
        }

        std::istringstream row_stream(line);
        for (std::size_t j = 0; j < instance.n; ++j) {
            if (!(row_stream >> instance.matrix[i * instance.n + j])) {
                throw std::runtime_error("invalid matrix row in: " + path.string());
            }
        }
    }

    return instance;
}

std::vector<std::filesystem::path> csv_instance_files(const std::filesystem::path& directory) {
    std::vector<std::filesystem::path> files;

    for (const auto& entry : std::filesystem::directory_iterator(directory)) {
        if (entry.is_regular_file() && entry.path().extension() == ".csv") {
            files.push_back(entry.path());
        }
    }

    std::sort(files.begin(), files.end());
    return files;
}

std::string clean_filename(const std::string& path) {
    auto name = std::filesystem::path(path).filename().string();
    return name.empty() ? path : name;
}

std::filesystem::path repo_root() {
#ifdef PROJECT_ROOT
    return std::filesystem::path(PROJECT_ROOT);
#else
    auto current = std::filesystem::current_path();
    while (!current.empty()) {
        if (std::filesystem::exists(current / "instances") &&
            std::filesystem::exists(current / "results")) {
            return current;
        }
        current = current.parent_path();
    }
    return std::filesystem::current_path();
#endif
}

