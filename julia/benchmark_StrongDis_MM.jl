using BenchmarkTools

include("io.jl")
include("StrongDis.jl")

files = readdir("instances", join=true)

times = NamedTuple[]

mkpath("results")

for file in files

    if !endswith(file, ".csv")
        continue
    end

    println("Benchmarking $file")

    A, a, b = load_instance(file)

    # Warmup (JIT compilation)
    StrongDis_MM(A, a, b)

    # Eigentliche Messung
    start = time_ns()

    r = StrongDis_MM(A, a, b)

    elapsed = time_ns() - start

    push!(times, (
        file=file,
        time=elapsed,
        result=r
    ))
end

open("results/julia_results_MM.csv", "w") do io

    println(io, "file,time,result")

    for t in times
        println(io, "$(t.file),$(t.time),$(t.result)")
    end
end