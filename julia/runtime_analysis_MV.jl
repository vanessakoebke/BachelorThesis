using CSV
using DataFrames
using Statistics
using Plots
include("Io.jl")

using Printf

function format_time(x)
    if x >= 1e6
        return @sprintf("%.1f ms", x/1e6)
    elseif x >= 1e3
        return @sprintf("%.1f µs", x/1e3)
    else
        return @sprintf("%.0f ns", x)
    end
end

function plot_runtime_series!(x, values, label, color, marker, linestyle)
    plot!(x, values,
        label=label,
        marker=marker,
        markersize=7,
        markerstrokewidth=1.2,
        lw=2.6,
        linestyle=linestyle,
        seriescolor=color,
        linealpha=0.9)
end

# =========================
# 0. stabile Pfade (WICHTIG)
# =========================
base = @__DIR__

java_path  = joinpath(base, "..", "results", "java_results_ejml_nc.csv")
julia_path = joinpath(base, "..", "results", "julia_results_MV_withoutTypAnnotation.csv")
julia_ann_path = joinpath(base, "..", "results", "julia_results_MV_withTypAnnotation.csv")
rust_path = joinpath(base, "..", "results", "rust_results_MV.csv")
cpp_path = joinpath(base, "..", "results", "cpp_results_MV.csv")

# =========================
# 1. Daten laden
# =========================
function load_runtime_results(path, time_col)
    df = CSV.read(path, DataFrame)
    df.file = clean_filename.(df.file)
    rename!(df, :time => time_col)
    return df[:, [:file, time_col]]
end

java_mm = load_runtime_results(java_path, :time_java)
julia_mm = load_runtime_results(julia_path, :time_julia)
julia_ann_mm = load_runtime_results(julia_ann_path, :time_julia_ann)
rust_mm = load_runtime_results(rust_path, :time_rust)
cpp_mm = load_runtime_results(cpp_path, :time_cpp)

# Join
df = innerjoin(java_mm, julia_mm, on=:file, makeunique=true)
df = innerjoin(df, julia_ann_mm, on=:file, makeunique=true)
df = innerjoin(df, rust_mm, on=:file, makeunique=true)
df = innerjoin(df, cpp_mm, on=:file, makeunique=true)

# =========================
# 2. n aus Dateiname extrahieren
# =========================
extract_n(x) =
    parse(Int, match(r"n(\d+)_", String(x)).captures[1])

df.n = extract_n.(df.file)

# =========================
# 3. Median pro n
# =========================
med = combine(groupby(df, :n),
    :time_java  => median => :java_med,
    :time_julia => median => :julia_med,
    :time_julia_ann => median => :julia_ann_med,
    :time_rust => median => :rust_med,
    :time_cpp => median => :cpp_med
)

sort!(med, :n)

# =========================
# 4. Plot (log-scale korrekt)
# =========================
n_vals = [10, 50, 100, 500, 1000, 5000]
x = 1:length(n_vals)

# Daten plotten
plot(x, med.java_med,
    label="Java",
    marker=:circle,
    markersize=7,
    markerstrokewidth=1.2,
    lw=2.8,
    linestyle=:solid,
    seriescolor=:black,
    linealpha=0.9,
    yscale=:log10,
    grid=true,
    minorgrid=true,
    legend=:outertopright,
    size=(1100, 650),
    dpi=300,
    framestyle=:box,
    foreground_color_legend=nothing,
    background_color_legend=nothing)

plot_runtime_series!(x, med.julia_med, "Julia ohne Typannotation", :dodgerblue3, :square, :dash)
plot_runtime_series!(x, med.julia_ann_med, "Julia mit Typannotation", :deepskyblue4, :hexagon, :dot)
plot_runtime_series!(x, med.rust_med, "Rust", :darkorange2, :diamond, :dashdot)
plot_runtime_series!(x, med.cpp_med, "C++", :forestgreen, :utriangle, :solid)    

# X-Achse als Kategorien
xticks!(x, string.(n_vals))

all_times = vcat(med.java_med, med.julia_med, med.julia_ann_med, med.rust_med, med.cpp_med)
yt = exp10.(floor(log10(minimum(all_times))) :
            ceil(log10(maximum(all_times))))

yticks!(yt, format_time.(yt))

xlabel!("Eingabegröße (n)")
ylabel!("Laufzeit (log-Skala)")

# =========================
# 5. Save + show
# =========================
savefig(joinpath(base, "..", "results", "runtime_comparison_mv.png"))
display(current())

relative_plot = plot(x, ones(length(x)),
    label="Java",
    marker=:circle,
    markersize=7,
    markerstrokewidth=1.2,
    lw=2.8,
    linestyle=:solid,
    seriescolor=:black,
    grid=true,
    minorgrid=true,
    legend=:outertopright,
    size=(1100, 650),
    dpi=300,
    framestyle=:box,
    foreground_color_legend=nothing,
    background_color_legend=nothing)

plot_runtime_series!(x, med.julia_med ./ med.java_med, "Julia ohne Typannotation", :dodgerblue3, :square, :dash)
plot_runtime_series!(x, med.julia_ann_med ./ med.java_med, "Julia mit Typannotation", :deepskyblue4, :hexagon, :dot)
plot_runtime_series!(x, med.rust_med ./ med.java_med, "Rust", :darkorange2, :diamond, :dashdot)
plot_runtime_series!(x, med.cpp_med ./ med.java_med, "C++", :forestgreen, :utriangle, :solid)

xticks!(x, string.(n_vals))
xlabel!("Eingabegröße (n)")
ylabel!("Relative Laufzeit zu Java")
hline!([1.0], label="", linestyle=:dash, seriescolor=:gray)
savefig(relative_plot, joinpath(base, "..", "results", "runtime_comparison_mv_relative.png"))

# =========================
# 6. Speedup berechnen
# =========================
med.speedup_java_vs_julia = med.java_med ./ med.julia_med
med.speedup_java_vs_julia_ann = med.java_med ./ med.julia_ann_med
med.speedup_java_vs_rust = med.java_med ./ med.rust_med
med.speedup_java_vs_cpp = med.java_med ./ med.cpp_med

println(med[:, [:n, :speedup_java_vs_julia, :speedup_java_vs_julia_ann, :speedup_java_vs_rust, :speedup_java_vs_cpp]])
