cmake_minimum_required(VERSION 3.16)

project(strongdis_runtime_cpp LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

add_library(strongdis_runtime
    src/io.cpp
    src/strong_dis.cpp
)

target_include_directories(strongdis_runtime PUBLIC include)
target_compile_definitions(strongdis_runtime PUBLIC PROJECT_ROOT="${CMAKE_CURRENT_LIST_DIR}/..")

if(APPLE)
    execute_process(
        COMMAND xcrun --show-sdk-path
        OUTPUT_VARIABLE MACOS_SDK_PATH
        OUTPUT_STRIP_TRAILING_WHITESPACE
        ERROR_QUIET
    )
    if(MACOS_SDK_PATH)
        target_compile_options(strongdis_runtime PUBLIC
            -isysroot ${MACOS_SDK_PATH}
            -I${MACOS_SDK_PATH}/usr/include/c++/v1
        )
    endif()
endif()

if(MSVC)
    target_compile_options(strongdis_runtime PRIVATE /W4)
else()
    target_compile_options(strongdis_runtime PRIVATE -Wall -Wextra -pedantic)
endif()

add_executable(benchmark_strong_dis_mm src/bin/benchmark_strong_dis_mm.cpp)
target_link_libraries(benchmark_strong_dis_mm PRIVATE strongdis_runtime)

add_executable(benchmark_strong_dis_mv src/bin/benchmark_strong_dis_mv.cpp)
target_link_libraries(benchmark_strong_dis_mv PRIVATE strongdis_runtime)

add_executable(result_validation src/bin/result_validation.cpp)
target_link_libraries(result_validation PRIVATE strongdis_runtime)

add_executable(strong_dis_tests tests/strong_dis_tests.cpp)
target_link_libraries(strong_dis_tests PRIVATE strongdis_runtime)

enable_testing()
add_test(NAME strong_dis_tests COMMAND strong_dis_tests)
