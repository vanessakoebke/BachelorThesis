import pandas as pd
import Util as u

#EquivDis sequential vs. parallel implementation
df = pd.read_csv("Output/neu_PTIME_EquivDis_sequential_parallel_2026-07-28_12:21:55.183102361.csv")
#df_clean = u.clean_dataframe(df, "time_seq", "time_para")

u.plot_combined_boxplot(df, "time_seq", "time_para",
                      "Sequential", "Parallel",
                      "analysis/PTime/EquivDis",
                      "MM EquivDis")

u.plot_median(df, "time_seq", "time_para",
            "Sequential", "Parallel",
            "analysis/PTime/EquivDis",
            "MM EquivDis - median")

u.plot_single_boxplot(df, "time_seq", "Sequential", "analysis/PTime/EquivDis", "PTime EquivDis sequential")

u.plot_single_boxplot(df, "time_para", "Parallel", "analysis/PTime/EquivDis", "PTime EquivDis parallelized")

#StrongerDis sequential vs. parallel implementation
df = pd.read_csv("Output/neu_PTIME_StrongerDis_sequential_parallel_2026-07-31_01:18:48.088632513.csv")
#df_clean = u.clean_dataframe(df, "time_seq", "time_para")

u.plot_combined_boxplot(df, "time_seq", "time_para",
                      "Sequential", "Parallel",
                      "analysis/PTime/StrongerDis",
                      "MM StrongerDis")

u.plot_median(df, "time_seq", "time_para",
            "Sequential", "Parallel",
            "analysis/PTime/StrongerDis",
            "MM StrongerDis - median")

u.plot_single_boxplot(df, "time_seq", "Sequential", "analysis/PTime/StrongerDis", "PTime StrongerDis sequential")

u.plot_single_boxplot(df, "time_para", "Parallel", "analysis/PTime/StrongerDis", "PTime StrongerDis parallelized")