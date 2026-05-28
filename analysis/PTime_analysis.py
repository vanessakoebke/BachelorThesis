import pandas as pd
import Util as u

#EquivDis sequential vs. parallel implementation
df = pd.read_csv("Output/NC_EquivDis_sequential_parallel_2026-05-04_16:29:19.342124.csv")
df_clean = u.clean_dataframe(df, "time_seq", "time_para")

u.plot_combined_boxplot(df_clean, "time_seq", "time_para",
                      "Sequential", "Parallel",
                      "analysis/PTime/EquivDis")

u.plot_median(df_clean, "time_seq", "time_para",
            "Sequential", "Parallel",
            "analysis/PTime")

u.plot_single_boxplot(df_clean, "time_seq", "Sequential", "analysis/PTime/EquivDis")

u.plot_single_boxplot(df_clean, "time_para", "Parallel", "analysis/PTime/EquivDis")

