import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import os

csv_path = "../static/hdd_write_speed.csv"
plot_dir = "../static/hdd_plots"
os.makedirs(plot_dir, exist_ok=True)

df = pd.read_csv(csv_path)
df["FileSizeMB"] = pd.to_numeric(df["FileSizeMB"])
df["BufferSizeKB"] = pd.to_numeric(df["BufferSizeKB"])
df["WriteSpeedMBps"] = pd.to_numeric(df["WriteSpeedMBps"])

for mode in df["TestMode"].unique():
    subset = df[df["TestMode"] == mode]

    plt.figure(figsize=(10, 6))

    x_col = "BufferSizeKB" if mode == "fs" else "FileSizeMB"
    x_label = "Buffer Size (KB)" if mode == "fs" else "File Size (MB)"

    sns.lineplot(
        data=subset,
        x=x_col,
        y="WriteSpeedMBps",
        hue="Device",
        marker="o",
        dashes=False
    )

    plt.title(f"Write Speed vs {'Buffer' if mode == 'fs' else 'File'} Size ({mode.upper()} mode)")
    plt.xlabel(x_label)
    plt.ylabel("Write Speed (MB/s)")
    plt.grid(True)
    plt.tight_layout()

    plot_filename = os.path.join(plot_dir, f"hdd_write_speed_{mode}.png")
    plt.savefig(plot_filename)
    plt.show()
