import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import os

csv_path = "../static/fixed_vs_float.csv"
plot_path = "../static/fixed_vs_float_plot.png"

df = pd.read_csv(csv_path)
df["Instructions"] = pd.to_numeric(df["Instructions"])
df["TimeMS"] = pd.to_numeric(df["TimeMS"])

plt.figure(figsize=(10, 6))
sns.lineplot(
    data=df,
    x="Instructions",
    y="TimeMS",
    hue="Mode",
    style="Device",
    markers=True,
    dashes=True
)

plt.title("Fixed vs Floating Point Performance")
plt.xlabel("Number of Instructions")
plt.ylabel("Time (ms)")
plt.grid(True)
plt.tight_layout()

os.makedirs(os.path.dirname(plot_path), exist_ok=True)
plt.savefig(plot_path)
plt.show()
