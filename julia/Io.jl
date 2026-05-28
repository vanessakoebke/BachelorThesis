function load_instance(path)

    lines = readlines(path)

    # Header
    header = split(lines[1], ",")

    n = parse(Int, header[1])

    # +1 wegen Julia indexing
    a::Int = parse(Int, header[2]) + 1
    b::Int = parse(Int, header[3]) + 1

    # Matrix
    A::Matrix{Int} = zeros(Int, n, n)

    for i in 1:n

        row = split(lines[i+1])

        for j in 1:n
            A[i, j] = parse(Int, row[j])
        end
    end

    return A, a, b
end

function clean_filename(path::AbstractString)
    return split(path, "/")[end]
end
