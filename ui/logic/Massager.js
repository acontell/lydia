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

    updateTrades(trades, tickers) {

        return _.map(trades, (asset, key) => {
            console.log(tickers)
            console.log(key)
            this.trade(asset, tickers[key])
        })
    }

    trade(asset, currentValue) {

        console.log(asset, currentValue)
        return asset
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
