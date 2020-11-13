const OLD_ASSETS = {
    "XETHZ": "ETH",
    "XMLNZ": "MLN",
    "XXBTZ": "XBT",
    "XXLMZ": "XLM"
}

function normalizePair(obj) {

    let pairNoEur = _.replace(_.get(obj, 'pair'), 'EUR', '');
    let normalizedPair = _.get(OLD_ASSETS, pairNoEur, pairNoEur);
    _.set(obj, 'pair', normalizedPair)

    return obj;
}

export default class Massager {

    tradeBalance({data: {result: balance}}) {

        return balance
    }

    trades(trades) {

        return _
            .chain(trades)
            .reduce(_.merge, {})
            .reduce((acc, value, key) => acc.concat(_.assign({'txid': key}, value)), [])
            .map(normalizePair)
            .groupBy('pair')
            .value();
    }

    getSummary() {

        let a = {}
        a.totalProfit = 0
        a.totalFees = 0
        a.totalInvested = 0
        a.actualValue = 0
        a.actualGainLoss = 0
        a.actualGainLossPercentage = 0

        return a
    }

    tradesSummary(trades, tickers) {

        return _.reduce(trades, (acc, asset, key) => {

            acc[key] = this.tradeSummary(asset, tickers[key])

            return acc
        }, {})
    }

    tradeSummary(asset, currentValue) {

        let orderedTrades = _.sortBy(asset, 'time', 'asc')

        return {
            trades: orderedTrades,
            sumUp: this.sumUp(orderedTrades, currentValue)
        }
    }

    sumUp(orderedTrades, currentValue) {

        let buys = []
        let sells = []
        let totalFees = 0

        _.each(orderedTrades, trade => {

            totalFees += +trade.fee

            if (trade.type === 'buy') {

                buys = buys.concat({
                    amountBuy: +trade.vol,
                    priceBuy: +trade.price
                })
            } else {

                let totalSell = +trade.vol

                _.each(buys, buy => {

                    if (totalSell > 0 && buy.amountBuy > 0) {

                        let amountSold

                        if (totalSell > buy.amountBuy) {

                            totalSell -= buy.amountBuy
                            amountSold = buy.amountBuy
                            buy.amountBuy = 0

                        } else {

                            buy.amountBuy -= totalSell
                            amountSold = totalSell
                            totalSell = 0
                        }

                        sells = sells.concat({
                            amountSold: amountSold,
                            priceBuy: buy.priceBuy,
                            priceSold: +trade.price,
                            gainLoss: amountSold * (+trade.price - buy.priceBuy)
                        })
                    }
                });
            }
        });

        let gainsLosses = _.reduce(sells, (acc, sell) => acc + sell.gainLoss, 0)

        let actualBuys = _.filter(buys, buy => buy.amountBuy > 0)

        let sumupBuys = _.reduce(actualBuys, (acc, buy) => {

            return {
                currentAmount: acc.currentAmount + buy.amountBuy,
                pricesAdded: acc.pricesAdded + buy.priceBuy,
                moneySpent: acc.moneySpent + (buy.amountBuy * buy.priceBuy)
            }
        }, {currentAmount: 0, pricesAdded: 0, moneySpent: 0})

        return {
            gainsLosses: gainsLosses,
            currentAmount: sumupBuys.currentAmount,
            moneySpent: sumupBuys.moneySpent,
            averagePrice: sumupBuys.pricesAdded / _.size(actualBuys),
            actualPrice: currentValue,
            currentValue: sumupBuys.currentAmount * currentValue,
            gainLoss: (sumupBuys.currentAmount * currentValue) - sumupBuys.moneySpent,
            gainLossPercentage: (((sumupBuys.currentAmount * currentValue) - sumupBuys.moneySpent) / sumupBuys.moneySpent) * 100,
            totalFees: totalFees
        }
    }

    ledger(ledgers) {

        return {
            total: _.sum(_.map(ledgers, ledger => _.sum(_.map(_.map(ledger, 'amount'), _.toNumber))))
        }
    }

    tickers({data: {result: tickers}}) {

        return _.reduce(tickers, (acc, value, key) => {

            let pairNoEur = _.replace(key, 'EUR', '');
            let normalizedPair = _.get(OLD_ASSETS, pairNoEur, pairNoEur);
            acc[normalizedPair] = +value['c'][0]
            return acc
        }, {})
    }
}

