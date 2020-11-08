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

    trades(trades) {

        return _
            .chain(trades)
            .reduce(_.merge, {})
            .reduce((acc, value, key) => acc.concat(_.assign({'txid': key}, value)), [])
            .map(normalizePair)
            .groupBy('pair')
            .value();
    }

    ledger(ledgers) {

        return {
            total: _.sum(_.map(ledgers, ledger => _.sum(_.map(_.map(ledger, 'amount'), _.toNumber))))
        }
    }
}
