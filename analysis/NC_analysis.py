import pandas as pd
import Util as u

# EquivDis sequential vs. parallel
df = pd.read_csv("Output/neu_NC_EquivDis_sequential_parallel_2026-07-28_07:12:50.481730190.csv")
#df_clean = u.clean_dataframe(df, "time_seq", "time_para")

u.plot_combined_boxplot(df, "time_seq", "time_para",
                      "Sequential", "Parallel",
                      "analysis/NC/EquivDis",
                      "MV EquivDis")

u.plot_median(df, "time_seq", "time_para",
            "Sequential", "Parallel",
            "analysis/NC/EquivDis",
            "MV EquivDis - median")

u.plot_single_boxplot(df, "time_seq", "Sequential", "analysis/NC/EquivDis", "MV EquivDis sequential")

u.plot_single_boxplot(df, "time_para", "Parallel", "analysis/NC/EquivDis", "MV EquivDis parallelized")

#EquivDis parallel vs. space optimized implementation
df = pd.read_csv("Output/NC_EquivDis_opt1_spaceOptimized_2026-08-01_16:03:45.275924531.csv")
#df_clean = u.clean_dataframe(df, "time_para", "time_spaceOpti")

u.plot_combined_boxplot(df, "time_opt1", "time_spaceOpti",
                      "Optimal RQ1.1b", "Space optimized",
                      "analysis/NC/EquivDis_SpaceOpti",
                      "MV EquivDis")

u.plot_median(df, "time_opt1", "time_spaceOpti",
            "Optimal RQ1.1b", "Space optimized",
            "analysis/NC/EquivDis_SpaceOpti",
            "MV EquivDis")

u.plot_single_boxplot(df, "time_opt1", "Parallel", "analysis/NC/EquivDis_SpaceOpti", "MV EquivDis parallelized")

u.plot_single_boxplot(df, "time_spaceOpti", "Space optimized", "analysis/NC/EquivDis_SpaceOpti", "MV EquivDis space-optimized")

# StrongerDis sequential vs. parallel
df = pd.read_csv("Output/neu_NC_StrongerDis_sequential_parallel_2026-07-28_12:39:14.654226031.csv")
#df_clean = u.clean_dataframe(df, "time_seq", "time_para")

u.plot_combined_boxplot(df, "time_seq", "time_para",
                      "Sequential", "Parallel",
                      "analysis/NC/StrongerDis", "MV StrongerDis")

u.plot_median(df, "time_seq", "time_para",
            "Sequential", "Parallel",
            "analysis/NC/StrongerDis", "MV StrongerDis - median")

u.plot_single_boxplot(df, "time_seq", "Sequential", "analysis/NC/StrongerDis", "MV StrongerDis sequential")

u.plot_single_boxplot(df, "time_para", "Parallel", "analysis/NC/StrongerDis", "MV StrongerDis parallelized")



#StrongerDis parallel vs. space optimized implementation
df = pd.read_csv("Output/NC_StrongerDis_opt1_spaceOptimized_2026-08-01_18:13:04.430649840.csv")
#df_clean = u.clean_dataframe(df, "time_para", "time_spaceOpti")

u.plot_combined_boxplot(df, "time_opt1", "time_spaceOpti",
                      "Optimal RQ2.1b", "Space optimized",
                      "analysis/NC/StrongerDis_SpaceOpti",
                      "MV StrongerDis")

u.plot_median(df, "time_opt1", "time_spaceOpti",
            "Optimal RQ2.1b", "Space optimized",
            "analysis/NC/StrongerDis_SpaceOpti",
            "MV StrongerDis")

u.plot_single_boxplot(df, "time_opt1", "Parallel", "analysis/NC/StrongerDis_SpaceOpti", "MV StrongerDis parallelized")

u.plot_single_boxplot(df, "time_spaceOpti", "Space optimized", "analysis/NC/StrongerDis_SpaceOpti", "MV StrongerDis space-optimized")
