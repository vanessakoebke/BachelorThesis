#include "strong_dis.hpp"
#include "strong_dis_opt.hpp"

#include <cassert>
#include <vector>

int main() {
    const std::vector<double> asymmetric = {
        0.0, 1.0,
        0.0, 0.0,
    };
    assert(strong_dis_mm(asymmetric, 2, 0, 1));
    assert(strong_dis_mv(asymmetric, 2, 0, 1));
    assert(strong_dis_mm_opt(asymmetric, 2, 0, 1));
    assert(strong_dis_mv_opt(asymmetric, 2, 0, 1));

    const std::vector<double> identity = {
        1.0, 0.0,
        0.0, 1.0,
    };
    assert(!strong_dis_mm(identity, 2, 0, 0));
    assert(!strong_dis_mv(identity, 2, 0, 0));
    assert(!strong_dis_mm_opt(identity, 2, 0, 0));
    assert(!strong_dis_mv_opt(identity, 2, 0, 0));

    return 0;
}
