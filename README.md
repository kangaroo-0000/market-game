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

A progress bar counts down. At *t = 0* the hidden dice flip, inventory is settled, and a fresh quote appears.

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

- You set mid = 10, spread = 4 → Bid = 8, Offer = 12.

- EV often clusters near 10.5; a symmetric book yields spread income with neutral expectation.

- If hidden dice reveal sum = 15, sellers paid 15 – bid/book risk.

- Imbalances (unequal buys vs sells) create directional P/L beyond spread.


---

## 4 Option (“Power Card”) pricing

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





