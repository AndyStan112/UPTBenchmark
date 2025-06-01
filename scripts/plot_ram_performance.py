import pandas as pd
import matplotlib.pyplot as plt
import os

csv_path = "../static/ram/csv/virtual_memory_benchmark.csv"
plot_dir = "../static/ram/plots"
os.makedirs(plot_dir, exist_ok=True)

df = pd.read_csv(csv_path)

df["FileSizeMB"]     = pd.to_numeric(df["FileSizeMB"],     errors="coerce")
df["ReadSpeedMBps"]  = pd.to_numeric(df["ReadSpeedMBps"],  errors="coerce")
df["WriteSpeedMBps"] = pd.to_numeric(df["WriteSpeedMBps"], errors="coerce")

plt.figure(figsize=(10, 6))
for (device, mode), grp in df.groupby(["Device", "Mode"]):
    linestyle = "-" if mode == "ram" else "--"
    plt.plot(
        grp["FileSizeMB"],
        grp["WriteSpeedMBps"],
        label=f"{device} ({mode})",
        linestyle=linestyle
    )
plt.title("Write Speed (MB/s) vs File Size (MB)")
plt.xlabel("File Size (MB)")
plt.ylabel("Write Speed (MB/s)")
plt.grid(True)
plt.legend()
plt.tight_layout()
out_write = f"{plot_dir}/write_speed_vs_size.png"
plt.savefig(out_write)
plt.close()
print(f"Saved {out_write}")

plt.figure(figsize=(10, 6))
for (device, mode), grp in df.groupby(["Device", "Mode"]):
    linestyle = "-" if mode == "ram" else "--"
    plt.plot(
        grp["FileSizeMB"],
        grp["ReadSpeedMBps"],
        label=f"{device} ({mode})",
        linestyle=linestyle
    )
plt.title("Read Speed (MB/s) vs File Size (MB)")
plt.xlabel("File Size (MB)")
plt.ylabel("Read Speed (MB/s)")
plt.grid(True)
plt.legend()
plt.tight_layout()
out_read = f"{plot_dir}/read_speed_vs_size.png"
plt.savefig(out_read)
plt.close()
print(f"Saved {out_read}")
