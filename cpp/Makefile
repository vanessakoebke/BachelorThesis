CXX ?= c++
CXXFLAGS ?= -std=c++17 -O3 -Wall -Wextra -pedantic
CPPFLAGS ?= -Iinclude -DPROJECT_ROOT=\"$(abspath ..)\"
SDKROOT ?= $(shell xcrun --show-sdk-path 2>/dev/null)
ifneq ($(wildcard $(SDKROOT)/usr/include/c++/v1/cstddef),)
CPPFLAGS += -isysroot $(SDKROOT) -I$(SDKROOT)/usr/include/c++/v1
endif
LDFLAGS ?=
LDLIBS ?=
ifneq ($(wildcard $(SDKROOT)/System/Library/Frameworks/Accelerate.framework),)
CPPFLAGS += -F$(SDKROOT)/System/Library/Frameworks -DACCELERATE_NEW_LAPACK
LDLIBS += -framework Accelerate
endif
BUILD_DIR := build
LIB_SOURCES := src/io.cpp src/strong_dis.cpp
OPT_SOURCES := src/io.cpp src/strong_dis_opt.cpp

.PHONY: all clean test

all: $(BUILD_DIR)/benchmark_strong_dis_mm $(BUILD_DIR)/benchmark_strong_dis_mv $(BUILD_DIR)/benchmark_strong_dis_mm_opt $(BUILD_DIR)/benchmark_strong_dis_mv_opt $(BUILD_DIR)/result_validation

$(BUILD_DIR):
	mkdir -p $(BUILD_DIR)

$(BUILD_DIR)/benchmark_strong_dis_mm: $(LIB_SOURCES) src/bin/benchmark_strong_dis_mm.cpp | $(BUILD_DIR)
	$(CXX) $(CPPFLAGS) $(CXXFLAGS) $^ $(LDFLAGS) $(LDLIBS) -o $@

$(BUILD_DIR)/benchmark_strong_dis_mv: $(LIB_SOURCES) src/bin/benchmark_strong_dis_mv.cpp | $(BUILD_DIR)
	$(CXX) $(CPPFLAGS) $(CXXFLAGS) $^ $(LDFLAGS) $(LDLIBS) -o $@

$(BUILD_DIR)/benchmark_strong_dis_mm_opt: $(OPT_SOURCES) src/bin/benchmark_strong_dis_mm_opt.cpp | $(BUILD_DIR)
	$(CXX) $(CPPFLAGS) $(CXXFLAGS) $^ $(LDFLAGS) $(LDLIBS) -o $@

$(BUILD_DIR)/benchmark_strong_dis_mv_opt: $(OPT_SOURCES) src/bin/benchmark_strong_dis_mv_opt.cpp | $(BUILD_DIR)
	$(CXX) $(CPPFLAGS) $(CXXFLAGS) $^ $(LDFLAGS) $(LDLIBS) -o $@

$(BUILD_DIR)/result_validation: $(LIB_SOURCES) src/bin/result_validation.cpp | $(BUILD_DIR)
	$(CXX) $(CPPFLAGS) $(CXXFLAGS) $^ $(LDFLAGS) $(LDLIBS) -o $@

$(BUILD_DIR)/strong_dis_tests: $(LIB_SOURCES) src/strong_dis_opt.cpp tests/strong_dis_tests.cpp | $(BUILD_DIR)
	$(CXX) $(CPPFLAGS) $(CXXFLAGS) $^ $(LDFLAGS) $(LDLIBS) -o $@

test: $(BUILD_DIR)/strong_dis_tests
	$(BUILD_DIR)/strong_dis_tests

clean:
	rm -rf $(BUILD_DIR)
