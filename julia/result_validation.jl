using CSV
using DataFrames
include("Io.jl")

java_mm = CSV.read("results/java_results_ejml_nc.csv", DataFrame)
julia_mm = CSV.read("results/julia_results_MV_withoutTypAnnotation.csv", DataFrame)

julia_mm.file = clean_filename.(julia_mm.file)
java_mm.file  = clean_filename.(java_mm.file)

df = innerjoin(java_mm, julia_mm, on="file", makeunique=true)

# Ergebnisvergleich
for row in eachrow(df)

    if row.result != row.result_1   # je nach Spaltenname ggf. anpassen
        println("Fehler in Datei: ", row.file)
    end
end

println("Vergleich abgeschlossen")