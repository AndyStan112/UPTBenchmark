import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import os

csv_path = "../static/hdd_write_speed.csv"
plot_path = "../static/hdd_write_speed_plot.png"

df = pd.read_csv(csv_path)
df["FileSizeMB"] = pd.to_numeric(df["FileSizeMB"])
df["BufferSizeKB"] = pd.to_numeric(df["BufferSizeKB"])
df["WriteSpeedMBps"] = pd.to_numeric(df["WriteSpeedMBps"])

plt.figure(figsize=(12, 6))
sns.lineplot(
    data=df,
    x="BufferSizeKB",
    y="WriteSpeedMBps",
    hue="TestMode",
    style="Device",
    markers=True,
    dashes=False
)
plt.title("HDD Write Speed vs Buffer Size/File Size")
plt.xlabel("Buffer Size (KB)")
plt.ylabel("Write Speed (MB/s)")
plt.grid(True)
plt.tight_layout()

os.makedirs(os.path.dirname(plot_path), exist_ok=True)
plt.savefig(plot_path)
plt.show()
