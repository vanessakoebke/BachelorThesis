import pandas as pd


class GraphStructureAnalyzer:

    def __init__(self, file_path):
        self.file_path = file_path
        self.df = pd.read_csv(file_path, dtype=str)

    def remove_duplicates(self):
        """Remove duplicate rows."""

        before = len(self.df)

        self.df = self.df.drop_duplicates().reset_index(drop=True)

        after = len(self.df)

        print(f"Removed {before - after} duplicate rows.")
        print(f"Remaining rows: {after}")

    def get_numeric_ranges(self):
        """Print min and max values for numeric attributes per category."""

        numeric_columns = [
            "nodes",
            "edges",
            "density",
            "sccs",
            "largest_scc",
            "max_width",
            "max_depth"
        ]

        print("\nNumeric attributes:")

        for category, group in self.df.groupby("category"):

            print(f"\nCategory: {category}")

            for column in numeric_columns:
                values = pd.to_numeric(
                    group[column],
                    errors="coerce"
                )

                print(
                    f"{column}: "
                    f"min={values.min()}, "
                    f"max={values.max()}"
                )

    def get_boolean_values(self):
        """Print boolean values per category."""

        boolean_columns = [
            "is_cyclic"
        ]

        print("\nBoolean attributes:")

        for category, group in self.df.groupby("category"):

            print(f"\nCategory: {category}")

            for column in boolean_columns:

                values = (
                    group[column]
                    .dropna()
                    .str.lower()
                    .unique()
                )

                print(
                    f"{column}: {', '.join(sorted(values))}"
                )

    def analyze(self):
        """Run the complete analysis."""

        print(f"Initial number of rows: {len(self.df)}")

        self.remove_duplicates()

        self.get_numeric_ranges()

        self.get_boolean_values()

    def generate_latex_table(self, output_file=None):
        numeric_attributes = [
            "nodes",
            "edges",
            "density",
            "sccs",
            "largest_scc",
            "max_width",
            "max_depth"
        ]

        # Numerische Spalten in Zahlen umwandeln
        for attribute in numeric_attributes:
            self.df[attribute] = pd.to_numeric(
                self.df[attribute],
                errors="coerce"
            )

        rows = []

        for category, group in self.df.groupby("category"):
            values = [category]

            for attribute in numeric_attributes:
                min_value = group[attribute].min()
                max_value = group[attribute].max()

                if attribute == "density":
                    values.append(f"{min_value:.3f} -- {max_value:.3f}")
                else:
                    values.append(f"{min_value:g} -- {max_value:g}")

            cyclic_values = sorted(group["is_cyclic"].unique())
            values.append(", ".join(cyclic_values))

            values.append(" \\\\")

            rows.append(" & ".join(map(str, values)))

        latex = "\n".join(rows)

        if output_file:
            with open(output_file, "w", encoding="utf-8") as file:
                file.write(latex)
            print(f"LaTeX table written to: {output_file}")
        else:
            print("\nLaTeX table:")
            print(latex)

        return latex

    def print_averages(self):
        attributes = [
            "nodes",
            "edges",
            "density",
            "sccs",
            "largest_scc",
            "max_width",
            "max_depth"
        ]

        # Numerische Spalten sicher in Zahlen umwandeln
        for attribute in attributes:
            self.df[attribute] = pd.to_numeric(
                self.df[attribute],
                errors="coerce"
            )

        # Durchschnitt pro Kategorie
        averages_by_category = self.df.groupby("category")[attributes].mean()

        print("\nAverage values per category:")
        print(averages_by_category.to_string())

        # Durchschnitt über alle Graphen
        overall_averages = self.df[attributes].mean()

        print("\nAverage values overall:")
        print(overall_averages.to_string())


analyzer = GraphStructureAnalyzer("../Output/GraphStructure Kopie.csv")
analyzer.analyze()
analyzer.generate_latex_table("../Output/Analyzed_Graph_Structure Kopie.txt")
analyzer.print_averages()