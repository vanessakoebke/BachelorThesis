import pandas as pd
import Util as u

df = pd.read_csv("Output/NC_EquivDis_sequential_parallel_2026-06-30_18:44:52.003087582.csv")
df_clean = u.clean_dataframe(df, "time_seq", "time_para")

u.plot_combined_boxplot(df_clean, "time_seq", "time_para",
                      "Sequential", "Parallel",
                      "analysis/NC")

u.plot_median(df_clean, "time_seq", "time_para",
            "Sequential", "Parallel",
            "analysis/NC/EquivDis")

u.plot_single_boxplot(df_clean, "time_seq", "Sequential", "analysis/NC/EquivDis")

u.plot_single_boxplot(df_clean, "time_para", "Parallel", "analysis/NC/EquivDis")

#EquivDis parallel vs. space optimized implementation
df = pd.read_csv("Output/NC_EquivDis_parallel_spaceOptimized_2026-05-05_17:12:58.950087.csv")
df_clean = u.clean_dataframe(df, "time_para", "time_spaceOpti")

u.plot_combined_boxplot(df_clean, "time_para", "time_spaceOpti",
                      "Parallel", "Space optimized",
                      "analysis/NC/EquivDis_SpaceOpti")

u.plot_median(df_clean, "time_para", "time_spaceOpti",
            "Parallel", "Space optimized",
            "analysis/NC/EquivDis_SpaceOpti")

u.plot_single_boxplot(df_clean, "time_para", "Parallel", "analysis/NC/EquivDis_SpaceOpti")

u.plot_single_boxplot(df_clean, "time_spaceOpti", "Space optimized", "analysis/NC/EquivDis_SpaceOpti")