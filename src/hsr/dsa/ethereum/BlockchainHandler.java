package hsr.dsa.ethereum;

import SmartContractDSAProject.SmartContractDSAProject;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class BlockchainHandler {

    private String localEtherAccount;
    private String remoteEtherAccount;
    private String testNetAddress = "https://rinkeby.infura.io/";
    private String contractAddress;

    private Web3j localWeb3;
    private Web3j remoteWeb3;

    DefaultGasProvider gasProvider = new DefaultGasProvider();

    SmartContractDSAProject smartContract;

    TransactionReceipt receipt;

    public BlockchainHandler(String localEtherAccount, String remoteEtherAccount) {
        this.localEtherAccount = localEtherAccount;
        this.remoteEtherAccount = remoteEtherAccount;
        localWeb3 = Web3j.build(new HttpService(testNetAddress + localEtherAccount));
        remoteWeb3 = Web3j.build(new HttpService(testNetAddress + remoteEtherAccount));
        System.out.println("Successfull connected!");

        System.out.println("Your Balance: " + getBalanceFromAccount(localEtherAccount) + " ETH");
        System.out.println("Enemys Balance: " + getBalanceFromAccount(remoteEtherAccount) + " ETH");

        deploySmartContract();
        System.out.println("Smart contract deployed");
    }

    private BigDecimal getBalanceFromAccount(String account) {
        EthGetBalance balance = null;
        try {
            balance = localWeb3.ethGetBalance(account, DefaultBlockParameterName.LATEST).send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BigDecimal balanceInEther = Convert.fromWei(balance.getBalance().toString(), Convert.Unit.ETHER);
        return balanceInEther;
    }

    public void storeAmountInBlockchain(double amount) {
        BigDecimal gambleAmount = BigDecimal.valueOf(amount);
        if (gambleAmount.compareTo(getBalanceFromAccount(localEtherAccount)) == -1) {
            System.out.println("You have not enough ether!");
        }
        if (gambleAmount.compareTo(getBalanceFromAccount(remoteEtherAccount)) == -1) {
            System.out.println("Your enemy has not enough ether!");
        }

        try {
            //receipt = smartContract.storeAmountInContract(gambleAmount).send();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Can only be called from the looser, because he has to pay the winner.
    public void startTransaction() {

        //smartContract.startTransaction().send();
    }


    private void invokeContractFunction() {
        TransactionReceipt receipt = null;
        try {
            receipt = smartContract.testFunction(new BigInteger(String.valueOf(2))).send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String txHash = receipt.getTransactionHash();
    }

    private void deploySmartContract() {
        Credentials creds = Credentials.create(localEtherAccount);

        try {
            smartContract = SmartContractDSAProject.deploy(localWeb3, creds, gasProvider).send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        contractAddress = smartContract.getContractAddress();
    }

    private void getAlreadyDeployedSmartContract() {
        Credentials creds = Credentials.create(localEtherAccount);
        SmartContractDSAProject registryContract = SmartContractDSAProject.load(contractAddress, localWeb3, creds, gasProvider);
    }

}
