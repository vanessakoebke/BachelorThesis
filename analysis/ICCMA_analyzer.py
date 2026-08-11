from pathlib import Path

import matplotlib.pyplot as plt
from matplotlib.lines import Line2D
import pandas as pd
import matplotlib.ticker as ticker


BASE_DIR = Path(__file__).resolve().parent
OUTPUT_DIR = BASE_DIR.parent / "Output"
PLOT_DIR = BASE_DIR / "ICCMA"

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

import pandas as pd


def format_time_columns(input_file, output_file):
    df = pd.read_csv(input_file)

    # Alle Zeitspalten finden
    time_columns = [col for col in df.columns if "time" in col.lower()]

    # Zeitspalten zunächst numerisch machen
    for column in time_columns:
        df[column] = pd.to_numeric(df[column], errors="coerce")

    # Median pro File berechnen
    median_rows = []

    for file, group in df.groupby("file"):
        median_row = group.iloc[0].copy()

        # Kennzeichnen, dass es sich um eine Median-Zeile handelt
        median_row["file"] = file + "_median"

        for column in time_columns:
            median_row[column] = group[column].median()

        median_rows.append(median_row)

    median_df = pd.DataFrame(median_rows)

    # Originaldaten + Medianzeilen
    result = pd.concat([df, median_df], ignore_index=True)

    # Zeitspalten formatieren
    for column in time_columns:
        result[column] = result[column].apply(format_time)

    result.to_csv(output_file, index=False)

    print(f"Gespeichert: {output_file}")
    print(f"Formatierte Zeitspalten: {time_columns}")



    

def format_number(x, pos):
    """Show logarithmic tick values as ordinary decimal numbers, not 10^x."""
    if float(x).is_integer():
        return f"{x:.0f}"
    return f"{x:g}"


def apply_plain_log_labels(axis):
    """Use ordinary numbers rather than 10^x on a logarithmic axis."""
    # Additional ticks at 2 and 5 in every decade keep short ranges readable,
    # e.g. 100, 200, 500, 1000 instead of only 100 and 1000.
    axis.set_major_locator(ticker.LogLocator(base=10, subs=(1, 2, 5), numticks=100))
    axis.set_major_formatter(ticker.FuncFormatter(format_number))
    axis.set_minor_formatter(ticker.NullFormatter())


def load_problem_data(results_file, categories_file, dimensions_file, problem):
    """Load per-instance medians for one problem from the combined results CSV."""
    runtimes = pd.read_csv(results_file, skipinitialspace=True)
    categories = pd.read_csv(categories_file, skipinitialspace=True)
    dimensions = pd.read_csv(dimensions_file, skipinitialspace=True)

    for dataframe in (runtimes, categories, dimensions):
        dataframe.columns = dataframe.columns.str.strip()

    columns_by_problem = {
        "equivdis": ("time_NC_equivDis", "time_PTime_equivDis"),
        "strongerdis": ("time_NC", "time_PTime_strongerDis"),
    }
    try:
        nc_column, ptime_column = columns_by_problem[problem.lower()]
    except KeyError as error:
        raise ValueError("problem must be 'equivdis' or 'strongerdis'.") from error

    # Prüfen, ob NC-Spalte existiert (Pflicht)
    if nc_column not in runtimes.columns:
        raise ValueError(f"Missing column '{nc_column}' in {results_file}")

    # Aggregationen vorbereiten
    agg_dict = {"median_NC": (nc_column, "median")}
    
    # PTime nur berechnen, wenn die Spalte in der CSV existiert
    has_ptime = ptime_column in runtimes.columns
    if has_ptime:
        agg_dict["median_PTime"] = (ptime_column, "median")

    medians = runtimes.groupby("file").agg(**agg_dict).reset_index()

    # Falls kein PTime existierte, Spalte als None anlegen (damit nachfolgender Code nicht abstürzt)
    if not has_ptime:
        medians["median_PTime"] = None

    return medians.merge(categories, on="file", how="left").merge(
        dimensions, on="file", how="left"
    )

def plot_runtime_scatter_only_NC(data, title, output_file):
    """Plot per-instance median runtimes, coloured by ICCMA category."""
    plot_data = data.dropna(subset=["dimension", "median_NC", "category"])
    plot_data = plot_data[(plot_data["dimension"] > 0) &
                          (plot_data["median_NC"] > 0) ]

    if plot_data.empty:
        raise ValueError(f"No plottable data for {title} available.")

    categories = sorted(plot_data["category"].unique())
    color_map = {
        category: plt.get_cmap("tab20")(index % 20)
        for index, category in enumerate(categories)
    }
    

    fig, ax = plt.subplots(figsize=(10, 6))
    for category in categories:
        subset = plot_data[plot_data["category"] == category]
        color = color_map[category]
        ax.scatter(subset["dimension"], subset["median_NC"],
                   color=color, marker="o", s=36, alpha=0.8)
        
    ax.set_title("MV StrongerDis - median")
    ax.set_xlabel("Input size n (log scale)")
    ax.set_ylabel("Run time (log scale)")
    ax.set_xscale("log")
    ax.set_yscale("log")
    apply_plain_log_labels(ax.xaxis)
    ax.yaxis.set_major_formatter(ticker.FuncFormatter(format_time))
    #ax.set_title(title)
    ax.grid(True, which="both", linestyle="--", linewidth=0.5, alpha=0.45)

    
    # Algorithm legend inside the plot
    # Category legend below the plot
    category_legend = [
            Line2D([0], [0], marker="o", color=color_map[category],
                linestyle="None", label=category)
            for category in categories
        ]

    legend1 = ax.legend(
        handles=category_legend,
        loc="upper left",
        title="Category"
    )

    

   

    # Keep the first legend when adding the second one
    ax.add_artist(legend1)

    fig.tight_layout()
    fig.savefig(output_file, dpi=300, bbox_inches="tight")
    plt.close(fig)

def plot_runtime_scatter(data, title, output_file):
    """Plot per-instance median runtimes, coloured by ICCMA category."""
    plot_data = data.dropna(subset=["dimension", "median_NC", "median_PTime", "category"])
    plot_data = plot_data[(plot_data["dimension"] > 0) &
                          (plot_data["median_NC"] > 0) &
                          (plot_data["median_PTime"] > 0)]

    if plot_data.empty:
        raise ValueError(f"No plottable data for {title} available.")

    categories = sorted(plot_data["category"].unique())
    color_map = {
        category: plt.get_cmap("tab20")(index % 20)
        for index, category in enumerate(categories)
    }
    print("Category → Color:")
    for category, color in color_map.items():
        print(category, color)

    fig, ax = plt.subplots(figsize=(10, 6))
    for category in categories:
        subset = plot_data[plot_data["category"] == category]
        color = color_map[category]
        ax.scatter(subset["dimension"], subset["median_NC"],
                   color=color, marker="o", s=36, alpha=0.8)
        ax.scatter(subset["dimension"], subset["median_PTime"],
                   color=color, marker="x", s=42, linewidths=1.3, alpha=0.8)

    ax.set_xlabel("Input size n (log scale)")
    ax.set_ylabel("Run time (log scale)")
    ax.set_xscale("log")
    ax.set_yscale("log")
    apply_plain_log_labels(ax.xaxis)
    ax.yaxis.set_major_formatter(ticker.FuncFormatter(format_time))
    ax.set_title(title)
    ax.grid(True, which="both", linestyle="--", linewidth=0.5, alpha=0.45)

    
    # Algorithm legend inside the plot
    algorithm_legend = [
        Line2D([0], [0], marker="x", color="black",
            linestyle="None", label="MM"),
        Line2D([0], [0], marker="o", color="black",
            linestyle="None", label="MV"),
    ]

    legend1 = ax.legend(
        handles=algorithm_legend,
        loc="upper left",
        title="Algorithm"
    )

    # Category legend below the plot
    category_legend = [
        Line2D([0], [0], marker="o", color=color_map[category],
            linestyle="None", label=category)
        for category in categories
    ]

    legend2 = ax.legend(
        handles=category_legend,
        loc="upper center",
        bbox_to_anchor=(0.5, -0.12),
        ncol=5,
        title="Category"
    )

    # Keep the first legend when adding the second one
    ax.add_artist(legend1)

    fig.tight_layout()
    fig.savefig(output_file, dpi=300, bbox_inches="tight")
    plt.close(fig)


def plot_speedup_scatter(data, title, output_file):
    """Plot the PTIME-to-NC speedup for every instance, coloured by category.

    A value above one means NC is faster; a value below one means PTIME is faster.
    """
    plot_data = data.dropna(subset=["dimension", "median_NC", "median_PTime", "category"])
    plot_data = plot_data[(plot_data["dimension"] > 0) &
                          (plot_data["median_NC"] > 0) &
                          (plot_data["median_PTime"] > 0)].copy()

    if plot_data.empty:
        raise ValueError(f"No plottable data for {title} available.")

    plot_data["speedup"] = plot_data["median_PTime"] / plot_data["median_NC"]
    categories = sorted(plot_data["category"].unique())
    color_map = {
        category: plt.get_cmap("tab20")(index % 20)
        for index, category in enumerate(categories)
    }

    fig, ax = plt.subplots(figsize=(10, 6))
    for category in categories:
        subset = plot_data[plot_data["category"] == category]
        ax.scatter(subset["dimension"], subset["speedup"], color=color_map[category],
                   marker="o", s=36, alpha=0.8, label=category)

    ax.axhline(1, color="black", linestyle="--", linewidth=1, label="Same run time")
    ax.set_xlabel("Input size n (log scale)")
    ax.set_ylabel("Speedup: MM / MV (log scale)")
    ax.set_xscale("log")
    ax.set_yscale("log")
    apply_plain_log_labels(ax.xaxis)
    apply_plain_log_labels(ax.yaxis)
    ax.set_title(title)
    ax.grid(True, which="both", linestyle="--", linewidth=0.5, alpha=0.45)
    ax.legend(title="Category", ncol=2, loc="best")

    fig.tight_layout()
    fig.savefig(output_file, dpi=300, bbox_inches="tight")
    plt.close(fig)

def count_max():
    df = pd.read_csv("../Output/results.csv")

    # Anzahl verschiedener Elemente in Spalte x
    num_unique = df["file"].nunique()
    print(f"Anzahl verschiedener Elemente in x: {num_unique}")

    # 10 größte Werte aus Spalte y
    print_top_10(df, "time_PTime_equivDis", "PTime EquivDis")
    print_top_10(df, "time_PTime_strongerDis", "PTime StrongerDis")
    print_top_10(df, "time_NC_equivDis", "NC EquivDis")
    print_top_10(df, "time_NC_strongerDis", "NC StrongerDis")

def format_time(x, pos=None):
    if x >= 3.6e12:
        return f"{x / 3.6e12:.1f} h"
    elif x >= 6e10:
        return f"{x / 6e10:.1f} min"
    elif x >= 1e9:
        return f"{x / 1e9:.1f} s"
    elif x >= 1e6:
        return f"{x / 1e6:.1f} ms"
    elif x >= 1e3:
        return f"{x / 1e3:.1f} µs"
    else:
        return f"{x:.0f} ns"

def print_top_10(df, column, name):
    top_10 = (
        df.groupby("file")[column]
        .max()
        .nlargest(10)
        .reset_index()
    )

    top_10[column] = top_10[column].apply(format_time)

    # Dateinamen für LaTeX vorbereiten
    top_10["file"] = top_10["file"].apply(
        lambda x: f"\\detokenize{{{x}}} &"
    )

    print(f"\n10 langsamste Files in {name}:")
    print(top_10.to_string(index=False))

def get_input_files():
    results = pd.read_csv("../Output/results.csv")
    categories = pd.read_csv("../Output/ICCMA_categories.csv")

    # Doppelte Dateien entfernen
    distinct_files = results["file"].drop_duplicates()

    # Kategorie anhand des Dateinamens zuordnen
    result = pd.DataFrame({"file": distinct_files}).merge(
        categories[["file", "category"]],
        on="file",
        how="left"
    )
    result = result.sort_values(["category", "file"])

    # LaTeX-Tabelle ausgeben
    print("\\begin{tabular}{ll}")
    print("\\hline")
    print("\\textbf{File} & \\textbf{Category} \\\\")
    print("\\hline")

    for _, row in result.iterrows():
        file = row["file"].replace("_", "\\_")
        category = row["category"].replace("_", "\\_")

        print(f"{file} & {category} \\\\")

    print("\\hline")
    print("\\end{tabular}")

def main():
    results_file = OUTPUT_DIR / "results.csv"
    categories_file = OUTPUT_DIR / "ICCMA_categories.csv"
    dimensions_file = OUTPUT_DIR / "ICCMA_dimensions.csv"
    #strongerdis = load_problem_data(
     #       results_file, categories_file, dimensions_file, "strongerdis"
      #  )
    #equivdis = load_problem_data(
     #       results_file, categories_file, dimensions_file, "equivdis"
      #  )
    onlync_strongerdis_file = OUTPUT_DIR/"preliminary.csv"
    onlync =load_problem_data(
        onlync_strongerdis_file, categories_file, dimensions_file, "strongerdis"
    )

    PLOT_DIR.mkdir(parents=True, exist_ok=True)
    #plot_runtime_scatter(strongerdis, "StrongerDis MM vs. MV", PLOT_DIR / "strongerdis_scatter.png")
    #plot_runtime_scatter(equivdis, "EquivDis MM vs. MV", PLOT_DIR / "equivdis_scatter.png")
    #plot_speedup_scatter(strongerdis, "StrongerDis Speedup",
        #                 PLOT_DIR / "strongerdis_speedup.png")
    #plot_speedup_scatter(equivdis, "EquivDis Speedup",
        #                 PLOT_DIR / "equivdis_speedup.png")
    plot_runtime_scatter_only_NC(onlync, "MV",
                             PLOT_DIR / "onlyNC_StrongerDis.png")


if __name__ == "__main__":
    main()
    count_max()
    format_time_columns(
            "../Output/results_normalized.csv",
            "../Output/results_normalized.csv"
        )
    #get_input_files()


   

