# Market Madness  
*An educational market-making & trading simulation game in Java*

---

## 1 Quick start

```bash
mvn clean package                    # builds a self-contained fat-jar
java -jar target/market-madness.jar  # launches the GUI
```

*Requires JDK 17+ and Maven 3.9+.*  
The **first** instance starts an embedded WebSocket server on **localhost : 8025**.  
Open the jar again in another terminal (or on a friend’s machine on the same LAN) and both players’ P/L will show up on the live leaderboard.

---

## 2 Game flow

| Role | What you do every 15-second round |
|------|----------------------------------|
| **Market Maker** | • Enter **mid-price** and **spread**.<br>• Collect half-spread on each matched buy/sell.<br>• Carry any **inventory imbalance** into the dice realisation. |
| **Participant** | • See current **Bid / Offer**.<br>• **Buy**, **Sell** or purchase a **Call / Put** option.<br>• Position realises when the three dice are fully revealed. |

Choose a Role. A progress bar counts down. At *t = 0* the hidden dice flip, inventory is settled, and a fresh quote appears.
> **Limits:** All Bid/Offer quotes are clamped to the range **[3, 18]** (the min/max possible dice sums), and each round we simulate **100** traders when computing Maker P/L.

---

## 3 Dice maths

Three fair six-sided dice ⇒ sample space = 216.

* **E[Σ] = 3 × 3.5 = 10.5**  
* Symmetric distribution:

| Σ | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 | 11 | 12 | 13 | 14 | 15 | 16 | 17 | 18 |
|---|---|---|---|---|---|---|---|----|----|----|----|----|----|----|----|----|
| # outcomes | 1 | 3 | 6 | 10 | 15 | 21 | 25 | 27 | 25 | 21 | 15 | 10 | 6 | 3 | 1 | 1 |

Hidden dice (shown as **H**) are valued **3.5** in expectation (each die has mean 3.5).

**Example (Participant)**

Dice display: [8, H, H] 
Visible = 8, Hidden dice count = 2 → EV = 8 + 2×3.5 = 15.0

- If Offer ≤ 15.0, buying at the Offer has positive expectation.

- If Bid ≥ 15.0, selling at the Bid is profitable.

**Example (Maker)**

- You set **mid** = 10 and **spread** = 4, so  
  - **Bid** = 10 − 4/2 = 8  
  - **Offer** = 10 + 4/2 = 12

- The expected dice total is  
  \[
    E[\Sigma] = 3 \times 3.5 = 10.5.
  \]

- Compute your “edges”:  
  \[
    \text{edgeBuy}  = \frac{\text{Offer} - E[\Sigma]}{\text{spread}}
                   = \frac{12 - 10.5}{4}
                   = 0.375,
  \]
  \[
    \text{edgeSell} = \frac{E[\Sigma] - \text{Bid}}{\text{spread}}
                   = \frac{10.5 - 8}{4}
                   = 0.625.
  \]

- Simulate 100 traders:  
  - **buyers**  = round(0.375 × 100) = 38  
  - **sellers** = round(0.625 × 100) = 63  
  - **matched** = min(38, 63) = 38  
  - **imbalance** = 38 − 63 = –25  (you’re net short)

- **Spread revenue** = matched × spread = 38 × 4 = 152

- **Inventory P/L** = imbalance × (realizedPrice − midpoint)  
  If the dice sum realizes to 15, then  
  \[
    \text{inventoryPL} = -25 \times (15 - 10) = -125.
  \]

- **Total maker P/L this round**  
  = spreadRevenue + inventoryPL  
  = 152 + (–125)  
  = **27**

- That **+27** is added to your cumulative P/L each time you submit a market.

- **NOTE:** Maker P/L is calculated by simulating 100 traders and constraining prices to the dice-sum range [3, 18].
---

## 4 Option pricing

Option premium is set dynamically every round:

```bash
premium = ½ × (Offer − Bid) # half the current spread strike = Offer (Call) = Bid (Put)
```

Pay-off at realisation:
```bash
Call P/L = max(0, DiceSum − strike) − premium Put P/L = max(0, strike − DiceSum) − premium
```
*Rationale*
1. Wider spread ⇒ less liquidity ⇒ higher implied volatility ⇒ dearer optionality.

2. Half-spread is easy to compute mentally and yields realistic “theta/vega” intuition without Black–Scholes.

## 5 Database

Every executed **Trade** (side, price, qty, timestamp) is persisted to `marketmadness.db` (SQLite).  
Choose **View Trades** from the main menu to browse the latest 200 rows (sortable).

---

## 6 Build options

| Command | Result |
|---------|--------|
| `mvn clean package` | Creates fat-jar `target/market-madness.jar`. |
| `mvn exec:java -Dexec.mainClass=com.marketmadness.App` | Runs directly from `target/classes` during development. |
| `mvn dependency:copy-dependencies -DincludeScope=runtime` | Copies runtime jars to `target/dependency/` for class-path launch if fat-jar is disallowed. |





