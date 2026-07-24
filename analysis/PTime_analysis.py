import pandas as pd
import Util as u

#EquivDis sequential vs. parallel implementation
df = pd.read_csv("Output/final_PTIME_EquivDis_sequential_parallel_2026-07-03_20:23:06.238471577.csv")
df_clean = u.clean_dataframe(df, "time_seq", "time_para")

u.plot_combined_boxplot(df, "time_seq", "time_para",
                      "Sequential", "Parallel",
                      "analysis/PTime/EquivDis",
                      "PTime EquivDis")

u.plot_median(df, "time_seq", "time_para",
            "Sequential", "Parallel",
            "analysis/PTime/EquivDis",
            "PTime EquivDis - median")

u.plot_single_boxplot(df, "time_seq", "Sequential", "analysis/PTime/EquivDis", "PTime EquivDis sequential")

u.plot_single_boxplot(df, "time_para", "Parallel", "analysis/PTime/EquivDis", "PTime EquivDis parallelized")

#StrongerDis sequential vs. parallel implementation
df = pd.read_csv("Output/PTIME_StrongerDis_sequential_parallel_2026-07-13_12:33:59.594673.csv")
df_clean = u.clean_dataframe(df, "time_seq", "time_para")

u.plot_combined_boxplot(df, "time_seq", "time_para",
                      "Sequential", "Parallel",
                      "analysis/PTime/StrongerDis",
                      "PTime StrongerDis")

u.plot_median(df, "time_seq", "time_para",
            "Sequential", "Parallel",
            "analysis/PTime/StrongerDis",
            "PTime StrongerDis - median")

u.plot_single_boxplot(df, "time_seq", "Sequential", "analysis/PTime/StrongerDis", "PTime StrongerDis sequential")

u.plot_single_boxplot(df, "time_para", "Parallel", "analysis/PTime/StrongerDis", "PTime StrongerDis parallelized")