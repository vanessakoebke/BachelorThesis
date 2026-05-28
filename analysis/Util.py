import numpy as np
import matplotlib.pyplot as plt
from matplotlib.patches import Patch
import os
import matplotlib.ticker as ticker
from matplotlib.ticker import FuncFormatter

#Preparing data
def format_time(x, pos):
    if x >= 1e6:
        return f"{x/1e6:.1f} ms"
    elif x >= 1e3:
        return f"{x/1e3:.1f} µs"
    else:
        return f"{x:.0f} ns"

def remove_outliers(group):
    Q1 = group.quantile(0.25)
    Q3 = group.quantile(0.75)
    IQR = Q3 - Q1
    return group[(group >= Q1 - 1.5 * IQR) & (group <= Q3 + 1.5 * IQR)]

def clean_dataframe(df, col1, col2):
    df_clean = df.copy()
    df_clean[col1] = df.groupby("n")[col1].transform(remove_outliers)
    df_clean[col2] = df.groupby("n")[col2].transform(remove_outliers)
    return df_clean.dropna()

#Plots
def plot_combined_boxplot(df, col1, col2, label1, label2, output_path):
    n_values = sorted(df["n"].unique())

    data1 = [df[df["n"] == n][col1] for n in n_values]
    data2 = [df[df["n"] == n][col2] for n in n_values]

    x = np.arange(len(n_values))
    offset = 0.2

    plt.figure(figsize=(10, 5))

    bp1 = plt.boxplot(data1, positions=x - offset, widths=0.35, patch_artist=True)
    bp2 = plt.boxplot(data2, positions=x + offset, widths=0.35, patch_artist=True)

    # Farben
    for box in bp1["boxes"]:
        box.set_facecolor("#1f77b4")
    for box in bp2["boxes"]:
        box.set_facecolor("#ff7f0e")

    # 👉 Median hervorheben
    for median in bp1["medians"]:
        median.set_color("black")
        
    for median in bp2["medians"]:
        median.set_color("black")
       

    plt.xticks(x, n_values)
    plt.yscale("log")
    plt.gca().yaxis.set_major_formatter(ticker.FuncFormatter(format_time))

    plt.xlabel("Input size (n)")
    plt.ylabel("Runtime (log scale)")

    legend_elements = [
        Patch(facecolor="#1f77b4", label=label1),
        Patch(facecolor="#ff7f0e", label=label2)
    ]
    plt.legend(handles=legend_elements)

    plt.grid(True, which="both", linestyle="--", alpha=0.4)

    os.makedirs(output_path, exist_ok=True)

    plt.tight_layout()
    plt.savefig(f"{output_path}/boxplot.png", dpi=300)
    plt.close()

def plot_single_boxplot(df, column, label, output_path):

    # n-Werte sortieren
    n_values = sorted(df["n"].unique())
    data = [df[df["n"] == n][column] for n in n_values]

    x = np.arange(len(n_values))

    plt.figure(figsize=(8, 5))

    bp = plt.boxplot(data, patch_artist=True)


    # Achsen
    plt.xticks(x + 1, n_values)
    plt.yscale("log")

    plt.gca().yaxis.set_major_formatter(FuncFormatter(format_time))

    plt.xlabel("Input size (n)")
    plt.ylabel("Runtime (log scale)")
    

    plt.grid(True, which="both", linestyle="--", alpha=0.4)

    # Ordner sicherstellen
    os.makedirs(output_path, exist_ok=True)

    plt.tight_layout()
    plt.savefig(f"{output_path}/boxplot_{column}.png", dpi=300)
    plt.close()

def plot_median(df, col1, col2, label1, label2, output_path):

    n_values = sorted(df["n"].unique())
    medians = df.groupby("n")[[col1, col2]].median().reindex(n_values)

    x = np.arange(len(n_values))

    plt.figure()

    plt.plot(x, medians[col1], marker="o", label=label1)
    plt.plot(x, medians[col2], marker="o", label=label2)

    plt.xticks(x, n_values)
    plt.yscale("log")
    plt.gca().yaxis.set_major_formatter(ticker.FuncFormatter(format_time))

    plt.xlabel("Input size (n)")
    plt.ylabel("Median runtime (log scale)")
    plt.legend()

    plt.grid(True, linestyle="--", alpha=0.4)

    os.makedirs(output_path, exist_ok=True)

    plt.savefig(f"{output_path}/median.png")
    plt.close()