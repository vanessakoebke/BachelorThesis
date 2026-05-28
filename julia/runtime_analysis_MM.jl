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

# =========================
# 0. stabile Pfade (WICHTIG)
# =========================
base = @__DIR__

java_path  = joinpath(base, "..", "results", "java_results_ejml.csv")
julia_path = joinpath(base, "..", "results", "julia_results_MM_withoutTypeAnnotation.csv")
julia_ann_path = joinpath(base, "..", "results", "julia_results_MM_withTypeAnnotation.csv")

# =========================
# 1. Daten laden
# =========================
java_mm  = CSV.read(java_path, DataFrame)
julia_mm = CSV.read(julia_path, DataFrame)
julia_ann_mm = CSV.read(julia_ann_path, DataFrame)

# Dateinamen vereinheitlichen
java_mm.file = clean_filename.(java_mm.file)
julia_mm.file = clean_filename.(julia_mm.file)
julia_ann_mm.file = clean_filename.(julia_ann_mm.file)

# Spalten vorher umbenennen
rename!(java_mm, :time => :time_java)
rename!(julia_mm, :time => :time_julia)
rename!(julia_ann_mm, :time => :time_julia_ann)

# Join
df = innerjoin(java_mm, julia_mm, on=:file, makeunique=true)
df = innerjoin(df, julia_ann_mm, on=:file, makeunique=true)

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
    :time_julia_ann => median => :julia_ann_med
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
    lw=2,
    yscale=:log10,
    grid=true,
    legend=:topleft)

plot!(x, med.julia_med,
    label="Julia ohne Typannotation",
    marker=:square,
    lw=2)

plot!(x, med.julia_ann_med,
    label="Julia mit Typannotation",
    marker=:square,
    lw=2)    

# X-Achse als Kategorien
xticks!(x, string.(n_vals))

yt = exp10.(floor(log10(minimum(vcat(med.java_med, med.julia_med)))) :
            ceil(log10(maximum(vcat(med.java_med, med.julia_med)))))

yticks!(yt, format_time.(yt))

xlabel!("Eingabegröße (n)")
ylabel!("Laufzeit (log-Skala)")

# =========================
# 5. Save + show
# =========================
savefig(joinpath(base, "..", "results", "runtime_comparison_mm.png"))
display(current())

# =========================
# 6. Speedup berechnen
# =========================
med.speedup_java_vs_julia = med.java_med ./ med.julia_med
med.speedup_java_vs_julia_ann = med.java_med ./ med.julia_ann_med

println(med[:, [:n, :speedup_java_vs_julia, :speedup_java_vs_julia_ann]])