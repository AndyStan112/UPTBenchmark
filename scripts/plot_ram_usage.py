import pandas as pd
import matplotlib.pyplot as plt
import glob
import os

csv_pattern = "../static/ram/csv/virtual_memory_usage_*.csv"

plot_dir = "../static/ram/plots/usage"
os.makedirs(plot_dir, exist_ok=True)

if __name__ == "__main__":
    csv_paths = glob.glob(csv_pattern)
    if not csv_paths:
        raise RuntimeError(f"No CSVs found under pattern: {csv_pattern}")

    for path in csv_paths:
        df = pd.read_csv(path, parse_dates=["Timestamp"])
        t0 = df["Timestamp"].iloc[0]
        df["ElapsedSec"] = (df["Timestamp"] - t0).dt.total_seconds()

        base = os.path.splitext(os.path.basename(path))[0]

        plt.figure(figsize=(10, 6))
        if "Device" in df.columns:
            for device in df["Device"].unique():
                subset = df[df["Device"] == device]
                plt.plot(subset["ElapsedSec"], subset["CPU_Percent"], label=device)
            plt.legend()
        else:
            plt.plot(df["ElapsedSec"], df["CPU_Percent"])
        plt.title(f"CPU Usage (%) over time ({base})")
        plt.xlabel("Elapsed time (s)")
        plt.ylabel("CPU Usage (%)")
        plt.grid(True)
        cpu_out = f"{plot_dir}/cpu/{base}_cpu.png"
        plt.tight_layout()
        plt.savefig(cpu_out)
        plt.close()
        print(f"Saved {cpu_out}")

        plt.figure(figsize=(10, 6))
        if "Device" in df.columns:
            for device in df["Device"].unique():
                subset = df[df["Device"] == device]
                plt.plot(subset["ElapsedSec"], subset["RAM_Used_Percent"], label=device)
            plt.legend()
        else:
            plt.plot(df["ElapsedSec"], df["RAM_Used_Percent"])
        plt.title(f"RAM Usage (%) over time ({base})")
        plt.xlabel("Elapsed time (s)")
        plt.ylabel("RAM Usage (%)")
        plt.grid(True)
        ram_out = f"{plot_dir}/ram/{base}_ram.png"
        plt.tight_layout()
        plt.savefig(ram_out)
        plt.close()
        print(f"Saved {ram_out}")

        plt.figure(figsize=(10, 6))
        if "Device" in df.columns:
            for device in df["Device"].unique():
                subset = df[df["Device"] == device]
                plt.plot(subset["ElapsedSec"], subset["Disk_Util_Percent"], label=device)
            plt.legend()
        else:
            plt.plot(df["ElapsedSec"], df["Disk_Util_Percent"])
        plt.title(f"Disk Utilization (%) over time ({base})")
        plt.xlabel("Elapsed time (s)")
        plt.ylabel("Disk Utilization (%)")
        plt.grid(True)
        disk_out = f"{plot_dir}/disk/{base}_disk.png"
        plt.tight_layout()
        plt.savefig(disk_out)
        plt.close()
        print(f"Saved {disk_out}")
