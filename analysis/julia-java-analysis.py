import pandas as pd
import Util as u
import re

import pandas as pd
import Util as u
import re
import os

# =========================
# 1. Basisordner bestimmen
# =========================
BASE_DIR = os.path.dirname(os.path.abspath(__file__))

java_path = os.path.join(BASE_DIR, "..", "results", "java_results_ejml.csv")
julia_path = os.path.join(BASE_DIR, "..", "results", "julia_results_MM.csv")
output_dir = os.path.join(BASE_DIR, "runtime_comparison_julia")

# =========================
# 2. Ergebnisse laden
# =========================
java_df = pd.read_csv(java_path)
julia_df = pd.read_csv(julia_path)

# =========================
# 2. Dateinamen vereinheitlichen
# =========================
def clean_filename(x):
    return x.split("/")[-1]

java_df["file"] = java_df["file"].apply(clean_filename)
julia_df["file"] = julia_df["file"].apply(clean_filename)

# =========================
# 3. Zusammenführen
# =========================
df = pd.merge(java_df, julia_df, on="file")

# =========================
# 4. Spalten umbenennen
# =========================
df = df.rename(columns={
    "time_x": "time_java",
    "time_y": "time_julia"
})

# =========================
# 5. Inputgröße aus Dateiname extrahieren
# =========================
def extract_n(filename):
    match = re.search(r"n(\d+)_", filename)
    return int(match.group(1))

df["n"] = df["file"].apply(extract_n)

# =========================
# 6. Ausreißer entfernen
# =========================
df_clean = u.clean_dataframe(df, "time_java", "time_julia")

# =========================
# 7. Median-Plot
# =========================
u.plot_median(
    df_clean,
    "time_java",
    "time_julia",
    "Java",
    "Julia",
    output_dir
)