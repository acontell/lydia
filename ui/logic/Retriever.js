import axios from 'axios';
import _ from 'lodash';

const MAX_PAGE_SIZE = 50

export default class Retriever {

    tradeBalance() {

        return axios.get('/api/trade-balance')
    }

    tickers(tickers) {

        return axios.get('/api/tickers', {params: {tickers: tickers.join(',')}})
    }

    tradesHistory(offset = 0) {

        return getData('/api/trades-history', offset, ({data: {result: {trades, count}}}) => ({
            result: trades,
            count
        }))
    }

    ledger(endpoint, offset) {

        return getData(endpoint, offset, ({data: {result: {ledger, count}}}) => ({
            result: ledger,
            count
        }))
    }

    deposits(offset = 0) {

        return this.ledger('/api/get-deposits', offset)
    }

    withdraws(offset = 0) {

        return this.ledger('/api/get-withdraws', offset)
    }
}

function getData(endpoint, offset, mappingFnc, resultRef = []) {

    return axios.get(endpoint, {params: {offset: offset}})
        .then(mappingFnc)
        .then(({result, count}) => {

            resultRef.push(result)
            let size = _.reduce(resultRef, (acc, value) => acc + _.size(value), 0)

            return isAllLoaded(offset, size, +count)
                ? resultRef
                : getData(endpoint, size, mappingFnc, resultRef)
        })
}

function isAllLoaded(offset, size, count) {

    return isFirstPageEnough(offset, size, count)
        || size >= count
}

function isFirstPageEnough(offset, size) {

    return offset === 0 && size < MAX_PAGE_SIZE
}

