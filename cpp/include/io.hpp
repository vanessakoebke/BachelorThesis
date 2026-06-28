#pragma once

#include <cstddef>
#include <filesystem>
#include <string>
#include <vector>

struct Instance {
    std::vector<double> matrix;
    std::size_t n{};
    std::size_t a{};
    std::size_t b{};
};

Instance load_instance(const std::filesystem::path& path);
std::vector<std::filesystem::path> csv_instance_files(const std::filesystem::path& directory);
std::string clean_filename(const std::string& path);
std::filesystem::path repo_root();

