import Massager from "./Massager";
import Retriever from "./Retriever";

export default class Composer {

    constructor() {

        this.massager = new Massager()
        this.retriever = new Retriever()
    }

    compose() {

        return this.retriever.tradeBalance()
            .then(this.massager.tradeBalance)
            .then(balance => this.balance = balance)
            .then(l => this.retriever.deposits())
            .then(this.massager.ledger)
            .then(deposits => this.deposits = deposits)
            .then(l => this.retriever.withdraws())
            .then(this.massager.ledger)
            .then(withdraws => this.withdraws = withdraws)
            .then(tradesHistory => this.tradesHistory = tradesHistory)
            .then(l => this.retriever.tickers(Object.keys(this.tradesHistory)))
            .then(this.massager.tickers)
            .then(tickers => this.tickers = tickers)
            .then(l => this.retriever.tradesHistory())
            .then(trades => this.massager.trades(trades, this.tickers))
            .then(l => ({
                balance: this.balance,
                deposits: this.deposits,
                withdraws: this.withdraws,
                tradesHistory: this.tradesHistory,
                tickers: this.tickers
            }))
    }
}
