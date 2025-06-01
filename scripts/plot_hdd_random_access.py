import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import os

csv_path = "../static/hdd_random_access.csv"
plot_dir = "../static/hdd_plots"
os.makedirs(plot_dir, exist_ok=True)

df = pd.read_csv(csv_path)
df["BufferSizeKB"] = pd.to_numeric(df["BufferSizeKB"])
df["WriteSpeedMBps"] = pd.to_numeric(df["WriteSpeedMBps"])

modes = df["TestMode"].unique()
for mode in modes:
    plt.figure(figsize=(10, 6))
    subset = df[df["TestMode"] == mode]
    sns.lineplot(
        data=subset,
        x="BufferSizeKB",
        y="WriteSpeedMBps",
        hue="Device",
        marker='o'
    )
    plt.title(f"HDD Access Speed ({mode})")
    plt.xlabel("Buffer Size (KB)")
    plt.ylabel("Speed (MB/s)")
    plt.grid(True)
    plt.tight_layout()

    filename = f"{plot_dir}/hdd_access_{mode}.png"
    plt.savefig(filename)
    plt.show()
