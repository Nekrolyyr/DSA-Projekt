package hsr.dsa.ethereum;


import java.io.IOException;
import java.math.BigDecimal;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;


public class Ethereum {

    // Start Client:
    // geth --rpcapi personal,db,eth,net,web3 --rpc --rinkeby
    public Ethereum() {
        Web3j web3 = Web3j.build(new HttpService("https://rinkeby.infura.io/0x036FBAE35b84e03926Cf466C2Ef19165C66829b2"));
        System.out.println("Conencted");

        try {
            Web3ClientVersion clientVersion = web3.web3ClientVersion().send();

            EthBlockNumber blockNumber = web3.ethBlockNumber().send();

            EthGasPrice gasPrice = web3.ethGasPrice().send();

            EthGetBalance balance = web3.ethGetBalance("0x036FBAE35b84e03926Cf466C2Ef19165C66829b2", DefaultBlockParameterName.LATEST).send();
            BigDecimal balanceInEther = Convert.fromWei(balance.getBalance().toString(), Convert.Unit.ETHER);


            System.out.println("Client version: " + clientVersion.getWeb3ClientVersion());
            System.out.println("Block knumber: " + blockNumber.getBlockNumber());
            System.out.println("Gas price: " + gasPrice.getGasPrice());
            System.out.println("Balance: " + balance.getBalance());
            System.out.println("balance in ether: " + balanceInEther);

        } catch (IOException e) {
            throw new RuntimeException("Error: " + e);
        }
    }

}
