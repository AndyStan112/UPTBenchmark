import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

df = pd.read_csv("../static/pi_benchmark_results.csv")

gauss = df[df['Algorithm'] == 'GaussLegendre']
chud = df[df['Algorithm'] == 'Chudnovsky']

plt.figure(figsize=(10, 6))

plt.scatter(gauss['Digits'], gauss['AverageTimeMillis'], label='Gauss-Legendre', marker='o')
plt.scatter(chud['Digits'], chud['AverageTimeMillis'], label='Chudnovsky', marker='x')

deg = 2
gauss_fit = np.poly1d(np.polyfit(gauss['Digits'], gauss['AverageTimeMillis'], deg))
chud_fit = np.poly1d(np.polyfit(chud['Digits'], chud['AverageTimeMillis'], deg))

x_vals = np.linspace(df['Digits'].min(), df['Digits'].max(), 300)
plt.plot(x_vals, gauss_fit(x_vals), '--', label='Gauss-Legendre Fit')
plt.plot(x_vals, chud_fit(x_vals), '--', label='Chudnovsky Fit')

plt.title("Benchmark: Pi Digits vs Runtime")
plt.xlabel("Digits of Pi")
plt.ylabel("Average Time (ms)")
plt.legend()
plt.grid(True)
plt.tight_layout()
plt.savefig("../static/PIplot.png")
plt.show()
