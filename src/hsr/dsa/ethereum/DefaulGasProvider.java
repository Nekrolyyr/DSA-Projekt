package hsr.dsa.ethereum;

import java.math.BigInteger;

public class DefaulGasProvider {
    BigInteger price = new BigInteger(String.valueOf(1000));
    BigInteger gasLimit = new BigInteger(String.valueOf(100000));

    BigInteger getGasPrice(String contractFunc) {
        return price;
    }

    @Deprecated
    BigInteger getGasPrice() {
        return price;
    }

    BigInteger getGasLimit(String contractFunc) {
        return gasLimit;
    }

    @Deprecated
    BigInteger getGasLimit() {
        return gasLimit;
    }
}
