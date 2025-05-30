import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

df = pd.read_csv("../static/loop_unrolling_scores.csv")

plt.figure(figsize=(10, 6))
sns.lineplot(data=df, x='UnrollLevel', y='AverageScore', hue='Device', marker='o')
plt.title("Composite Benchmark Score vs Unroll Level (per Device)")
plt.xlabel("Unroll Level (0 = no unrolling)")
plt.ylabel("Average Composite Score")
plt.grid(True)
plt.tight_layout()
plt.savefig("loop_unrolling_scores_plot.png")
plt.show()
