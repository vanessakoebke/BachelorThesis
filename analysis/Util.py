import numpy as np
import matplotlib.pyplot as plt
from matplotlib.patches import Patch
import os
import matplotlib.ticker as ticker
from matplotlib.ticker import FuncFormatter

blue ="#1f77b4" 
purple = "#c5b0d5"

#Preparing data
def format_time(x, pos):
    if x >= 3.6e12:          # 1 Stunde
        return f"{x/3.6e12:.1f} h"
    elif x >= 6e10:          # 1 Minute
        return f"{x/6e10:.1f} min"
    elif x >= 1e9:           # 1 Sekunde
        return f"{x/1e9:.1f} s"
    elif x >= 1e6:           # 1 Millisekunde
        return f"{x/1e6:.1f} ms"
    elif x >= 1e3:           # 1 Mikrosekunde
        return f"{x/1e3:.1f} µs"
    else:
        return f"{x:.0f} ns"

def remove_outliers(group):
    Q1 = group.quantile(0.25)
    Q3 = group.quantile(0.75)
    IQR = Q3 - Q1
    return group[(group >= Q1 - 1.5 * IQR) & (group <= Q3 + 1.5 * IQR)]


#Plots
def plot_combined_boxplot(df, col1, col2, label1, label2, output_path, title):
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
        box.set_facecolor(blue)
    for box in bp2["boxes"]:
        box.set_facecolor(purple)

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
        Patch(facecolor=blue, label=label1),
        Patch(facecolor=purple, label=label2)
    ]
    plt.legend(handles=legend_elements, loc="upper left")
    plt.title(title)
    plt.grid(True, which="both", linestyle="--", alpha=0.4)

    os.makedirs(output_path, exist_ok=True)

    plt.tight_layout()
    plt.savefig(f"{output_path}/{title}_boxplot.png", dpi=300)
    plt.close()

def plot_single_boxplot(df, column, label, output_path, title):

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
    plt.title(title)
    # Ordner sicherstellen
    os.makedirs(output_path, exist_ok=True)

    plt.tight_layout()
    plt.savefig(f"{output_path}/{title}_boxplot_{column}.png", dpi=300)
    plt.close()

def plot_median(df, col1, col2, label1, label2, output_path, title):

    n_values = sorted(df["n"].unique())
    medians = df.groupby("n")[[col1, col2]].median().reindex(n_values)

    x = np.arange(len(n_values))

    plt.figure()

    plt.plot(x, medians[col1], marker="o", label=label1, color=blue)
    plt.plot(x, medians[col2], marker="o", label=label2, color=purple)

    plt.xticks(x, n_values)
    plt.yscale("log")
    plt.gca().yaxis.set_major_formatter(ticker.FuncFormatter(format_time))

    plt.xlabel("Input size (n)")
    plt.ylabel("Median runtime (log scale)")
    plt.legend()
    plt.title(title)

    plt.grid(True, linestyle="--", alpha=0.4)

    os.makedirs(output_path, exist_ok=True)

    plt.savefig(f"{output_path}/{title}_median.png")
    plt.close()

