import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

# =========================
# 1. CSV einlesen
# =========================
df = pd.read_csv("Output/NC_EquivDis_sequential_parallel_2026-05-04_16:29:19.342124.csv")

df["n"] = df["n"].astype(int)
df["time_seq"] = df["time_seq"].astype(float)
df["time_para"] = df["time_para"].astype(float)

# =========================
# 2. Ausreißer entfernen (IQR-Methode)
# =========================
def remove_outliers(group):
    Q1 = group.quantile(0.25)
    Q3 = group.quantile(0.75)
    IQR = Q3 - Q1
    return group[(group >= Q1 - 1.5 * IQR) & (group <= Q3 + 1.5 * IQR)]

df_clean = df.copy()

df_clean["time_seq"] = df.groupby("n")["time_seq"].transform(remove_outliers)
df_clean["time_para"] = df.groupby("n")["time_para"].transform(remove_outliers)

df_clean = df_clean.dropna()

# sortierte n-Werte
n_values = sorted(df_clean["n"].unique())

# sortierte n-Werte
n_values = sorted(df_clean["n"].unique())
x = np.arange(len(n_values))  # 👈 künstliche Kategorienachse

# =========================
# Boxplot Sequential
# =========================
plt.figure()

data_seq = [df_clean[df_clean["n"] == n]["time_seq"] for n in n_values]

plt.boxplot(data_seq)
plt.xticks(x + 1, n_values)  # boxplot startet bei 1
plt.title("Sequential Runtime by Input Size")
plt.xlabel("Input size (n)")
plt.ylabel("Time")
plt.yscale("log")
plt.savefig("analysis/NC/boxplot_seq.png")
plt.close()

# =========================
# Boxplot Parallel
# =========================
plt.figure()

data_para = [df_clean[df_clean["n"] == n]["time_para"] for n in n_values]

plt.boxplot(data_para)
plt.xticks(x + 1, n_values)

plt.title("Parallel Runtime by Input Size")
plt.xlabel("Input size (n)")
plt.ylabel("Time")
plt.yscale("log")
plt.savefig("analysis/NC/boxplot_para.png")
plt.close()

plt.figure(figsize=(10, 5))

data_seq = []
data_para = []

for n in n_values:
    seq = df_clean[df_clean["n"] == n]["time_seq"].dropna()
    para = df_clean[df_clean["n"] == n]["time_para"].dropna()

    if len(seq) > 0 and len(para) > 0:
        data_seq.append(seq)
        data_para.append(para)

x = np.arange(len(data_seq))
offset = 0.2

# Sequential Boxplot
bp1 = plt.boxplot(
    data_seq,
    positions=x - offset,
    widths=0.35,
    patch_artist=True
)

# Parallel Boxplot
bp2 = plt.boxplot(
    data_para,
    positions=x + offset,
    widths=0.35,
    patch_artist=True
)

# 🎨 Farben setzen
for box in bp1["boxes"]:
    box.set_facecolor("#1f77b4")  # blau (Sequential)

for box in bp2["boxes"]:
    box.set_facecolor("#ff7f0e")  # orange (Parallel)

# x-Achse
plt.xticks(x, n_values)
plt.yscale("log")

plt.xlabel("Input size (n)")
plt.ylabel("Runtime (log scale)")
plt.title("Sequential vs Parallel Runtime Comparison")

# Legend manuell sauber bauen
from matplotlib.patches import Patch
legend_elements = [
    Patch(facecolor="#1f77b4", label="Sequential"),
    Patch(facecolor="#ff7f0e", label="Parallel")
]
plt.legend(handles=legend_elements)

plt.grid(True, which="both", linestyle="--", alpha=0.4)

import os
os.makedirs("analysis/NC", exist_ok=True)

plt.tight_layout()
plt.savefig("analysis/NC/boxplot_combined.png", dpi=300)
plt.close()

# =========================
# 5. Break-even Punkt finden
# =========================

medians["diff"] = medians["time_para"] - medians["time_seq"]

print("\nBreak-even Analyse:")
for i in range(1, len(medians)):
    prev = medians.iloc[i-1]
    curr = medians.iloc[i]

    if prev["diff"] > 0 and curr["diff"] < 0:
        print(f"Parallel wird schneller zwischen n={medians.index[i-1]} und n={medians.index[i]}")