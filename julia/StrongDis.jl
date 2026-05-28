function StrongDis_MM(F, a, b)
    M = copy(F)
    for i in 1:(2*size(F,1) - 1)
        Ma = sum(@view M[:, a])
        Mb = sum(@view M[:, b])

        if (i % 2 != 0 && Ma < Mb) || (i % 2 == 0 && Mb < Ma) 
            return true
        end
        if Ma != Mb
            return false
        end
        M = M * F;
    end
    return false
end

function StrongDis_MM_ann(F::Matrix{Float64}, a::Int, b::Int)
    M = copy(F)
    for i in 1:(2*size(F,1) - 1)
        Ma = sum(@view M[:, a])
        Mb = sum(@view M[:, b])

        if (i % 2 != 0 && Ma < Mb) || (i % 2 == 0 && Mb < Ma) 
            return true
        end
        if Ma != Mb
            return false
        end
        M = M * F;
    end
    return false
end

function StrongDis_MV_ann(F::Matrix{Int}, a::Int, b::Int)
    n = size(F, 1)
    v1 = zeros(Float64, n)
    v2 = zeros(Float64, n)
    v1[a] = 1.0
    v2[b] = 1.0
    for iter in 1:(2n)
        r = rand(1:(2n*1000))   # Julia-Äquivalent zu ThreadLocalRandom
        v1 = F * v1 * r
        v2 = F * v2 * r
        sumV1 = sum(v1)
        sumV2 = sum(v2)
        if abs(sumV1 - sumV2) > 1e-9
            if isodd(iter)
                return sumV1 > sumV2 ? false : true
            else
                return sumV1 > sumV2 ? true : false
            end
        end
    end
    return false
end

function StrongDis_MV(F, a, b)
    n = size(F, 1)
    v1 = zeros(Float64, n)
    v2 = zeros(Float64, n)
    v1[a] = 1.0
    v2[b] = 1.0
    for iter in 1:(2n)
        r = rand(1:(2n*1000))   # Julia-Äquivalent zu ThreadLocalRandom
        v1 = F * v1 * r
        v2 = F * v2 * r
        sumV1 = sum(v1)
        sumV2 = sum(v2)
        if abs(sumV1 - sumV2) > 1e-9
            if isodd(iter)
                return sumV1 > sumV2 ? false : true
            else
                return sumV1 > sumV2 ? true : false
            end
        end
    end
    return false
end