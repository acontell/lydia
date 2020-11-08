import axios from 'axios';
import _ from 'lodash';
import Massager from "./Massager";

const MAX_PAGE_SIZE = 50

export default class Retriever {

    constructor() {

        this.massager = new Massager()
    }

    tradeBalance() {

        return axios.get('/api/trade-balance')
    }

    tradesHistory(offset = 0) {

        return getData('/api/trades-history', offset, ({data: {result: {trades, count}}}) => ({
            result: trades,
            count
        }), this.massager.trades)
    }

    ledger(endpoint, offset) {

        return getData(endpoint, offset, ({data: {result: {ledger, count}}}) => ({
            result: ledger,
            count
        }), this.massager.ledger)
    }

    deposits(offset = 0) {

        return this.ledger('/api/get-deposits', offset)
    }

    withdraws(offset = 0) {

        return this.ledger('/api/get-withdraws', offset)
    }
}

function getData(endpoint, offset, mappingFnc, massageFnc, resultRef = []) {

    return axios.get(endpoint, {params: {offset: offset}})
        .then(mappingFnc)
        .then(({result, count}) => {

            resultRef.push(result)
            let size = _.reduce(resultRef, (acc, value) => acc + _.size(value), 0)

            return isAllLoaded(offset, size, +count)
                ? massageFnc(resultRef)
                : getData(endpoint, size, mappingFnc, massageFnc, resultRef)
        })
}

function isAllLoaded(offset, size, count) {

    return isFirstPageEnough(offset, size, count)
        || size >= count
}

function isFirstPageEnough(offset, size) {

    return offset === 0 && size < MAX_PAGE_SIZE
}

