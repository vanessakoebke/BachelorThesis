import pandas as pd
import Util as u

# EquivDis sequential vs. parallel
df = pd.read_csv("Output/final_NC_EquivDis_sequential_parallel_2026-07-02_13:37:34.597040493.csv")
df_clean = u.clean_dataframe(df, "time_seq", "time_para")

u.plot_combined_boxplot(df, "time_seq", "time_para",
                      "Sequential", "Parallel",
                      "analysis/NC/EquivDis",
                      "NC EquivDis")

u.plot_median(df, "time_seq", "time_para",
            "Sequential", "Parallel",
            "analysis/NC/EquivDis",
            "NC EquivDis - median")

u.plot_single_boxplot(df, "time_seq", "Sequential", "analysis/NC/EquivDis", "NC EquivDis sequential")

u.plot_single_boxplot(df, "time_para", "Parallel", "analysis/NC/EquivDis", "NC EquivDis parallelized")

#EquivDis parallel vs. space optimized implementation
df = pd.read_csv("Output/NC_EquivDis_parallel_spaceOptimized_2026-05-05_17:12:58.950087.csv")
df_clean = u.clean_dataframe(df, "time_para", "time_spaceOpti")

u.plot_combined_boxplot(df, "time_para", "time_spaceOpti",
                      "Parallel", "Space optimized",
                      "analysis/NC/EquivDis_SpaceOpti",
                      "NC EquivDis")

u.plot_median(df, "time_para", "time_spaceOpti",
            "Parallel", "Space optimized",
            "analysis/NC/EquivDis_SpaceOpti",
            "NC EquivDis")

u.plot_single_boxplot(df, "time_para", "Parallel", "analysis/NC/EquivDis_SpaceOpti", "NC EquivDis parallelized")

u.plot_single_boxplot(df, "time_spaceOpti", "Space optimized", "analysis/NC/EquivDis_SpaceOpti", "NC EquivDis space-optimized")

# StrongerDis sequential vs. parallel
df = pd.read_csv("Output/final_NC_StrongerDis_sequential_parallel_2026-07-03_20:51:49.952894808.csv")
df_clean = u.clean_dataframe(df, "time_seq", "time_para")

u.plot_combined_boxplot(df, "time_seq", "time_para",
                      "Sequential", "Parallel",
                      "analysis/NC/StrongerDis", "NC StrongerDis")

u.plot_median(df, "time_seq", "time_para",
            "Sequential", "Parallel",
            "analysis/NC/StrongerDis", "NC StrongerDis - median")

u.plot_single_boxplot(df, "time_seq", "Sequential", "analysis/NC/StrongerDis", "NC StrongerDis sequential")

u.plot_single_boxplot(df, "time_para", "Parallel", "analysis/NC/StrongerDis", "NC StrongerDis parallelized")