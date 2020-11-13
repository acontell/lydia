import Massager from "./Massager";
import Retriever from "./Retriever";

export default class Composer {

    constructor() {

        this.massager = new Massager()
        this.retriever = new Retriever()
    }

    compose() {

        let tradeBalance
        let tradeDeposits
        let tradeWithdraws
        let trades
        let tradeTickers

        return this.retriever.tradeBalance()
            .then(this.massager.tradeBalance)
            .then(balance => tradeBalance = balance)
            .then(l => this.retriever.deposits())
            .then(this.massager.ledger)
            .then(deposits => tradeDeposits = deposits)
            .then(l => this.retriever.withdraws())
            .then(this.massager.ledger)
            .then(withdraws => tradeWithdraws = withdraws)
            .then(l => this.retriever.tradesHistory())
            .then(this.massager.trades)
            .then(tradesHistory => trades = tradesHistory)
            .then(l => this.retriever.tickers(Object.keys(trades)))
            .then(this.massager.tickers)
            .then(tickers => tradeTickers = tickers)
            .then(l => ({
                balance: tradeBalance,
                deposits: tradeDeposits,
                withdraws: tradeWithdraws,
                trades: this.massager.updateTrades(trades, tradeTickers),
                gainsLosses: tradeBalance.eb - (tradeDeposits.total + tradeWithdraws.total)
            }))
    }
}
