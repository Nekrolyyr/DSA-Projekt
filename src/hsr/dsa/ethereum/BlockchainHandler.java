package hsr.dsa.ethereum;

import SmartContractDSAProject.SmartContractDSAProject;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class BlockchainHandler {

    ContractGasProvider gasProvider;
    SmartContractDSAProject smartContract;
    TransactionReceipt receipt;

    private String localEtherAccount;
    private String remoteEtherAccount;
    private String testNetAddress = "rinkeby.infura.io/v3/2418791e3c9c486a87f1acd07b6ee5d5";//"https://rinkeby.infura.io/"; // Token generieren!! nach einloggen!!!
    private String contractAddress;

    private Web3j localWeb3;
    private Web3j smartContractWeb3;

    BigInteger gambleAmount;

    private String davidsEtherAccount = "0x1cE0089b18c8135B6fff8b10fC43F596A7289D83";

    public BlockchainHandler(String localEtherAccount, String remoteEtherAccount) {
        this.localEtherAccount = localEtherAccount;
        this.remoteEtherAccount = remoteEtherAccount;

        localWeb3 = Web3j.build(new HttpService("https://rinkeby.infura.io/" + localEtherAccount));
        smartContractWeb3 = Web3j.build(new HttpService("https://rinkeby.infura.io/"  + "0x8D18D6144d338079cD4b4f7D0981f4eE7477D49f")); // Smart contract address

        printClientVersions();
        printGasPrice();

        System.out.println("Successfull connected!");

        System.out.println("Your Balance: " + getBalanceFromAccount(localEtherAccount) + " WEI");
        System.out.println("Enemys Balance: " + getBalanceFromAccount(remoteEtherAccount) + " WEI");

        initializeGasProvider();

        //deploySmartContract();
        getAlreadyDeployedSmartContract();
        startTransaction();

    }

    private BigInteger getBalanceFromAccount(String account) {
        EthGetBalance balance = null;
        try {
            balance = localWeb3.ethGetBalance(account, DefaultBlockParameterName.LATEST).send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BigDecimal balanceInEther = Convert.fromWei(balance.getBalance().toString(), Convert.Unit.WEI);
        return balanceInEther.toBigInteger();
        //BigInteger balanceInEther = Convert.fromWei(balance.getBalance().toString(), Convert.Unit.ETHER);
        //return balance;
    }

    public void storeAmountInBlockchain(BigInteger gambleAmount) {
        if (gambleAmount.compareTo(getBalanceFromAccount(localEtherAccount)) == -1) {
            System.out.println("You have not enough ether!");
        }
        if (gambleAmount.compareTo(getBalanceFromAccount(remoteEtherAccount)) == -1) {
            System.out.println("Your enemy has not enough ether!");
        }

        this.gambleAmount = gambleAmount;

    }

    // Can only be called from the looser, because he has to pay the winner.
    public void startTransaction() {
        try {
            TransactionReceipt tx = smartContract.start(new BigInteger(String.valueOf(100000000))).send();
            if (tx.isStatusOK()) {
                System.out.println("Start successfull!");
            } else {
                System.out.println("Start error!");
            }
            TransactionReceipt tx2 = smartContract.payAmountToEnemty(davidsEtherAccount).send();
            if (tx2.isStatusOK()) {
                System.out.println("Pay successfull!");
            } else {
                System.out.println("Pay error!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deploySmartContract() {
        // TODO: Remove hardcoded private Key
        String myPrivateKey = "d458a482cb2d7532aab8f76994a32351d5190bc08d661636690fae7272efeaac";
        Credentials creds = Credentials.create(myPrivateKey);

        try {
            // TODO: Contract address: 0x8D18D6144d338079cD4b4f7D0981f4eE7477D49f

            DefaultGasProvider defaultGasProvider = new DefaultGasProvider();
            smartContract = SmartContractDSAProject.deploy(localWeb3, creds, new DefaultGasProvider()).send();


            if (smartContract.isValid()) {
                contractAddress = smartContract.getContractAddress();
                System.out.println("Smart contract address: " + contractAddress);
            } else {
                System.out.println("Smart contract nod valid!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAlreadyDeployedSmartContract() {
        // TODO: Hardcoded contract address
        contractAddress = "0x8D18D6144d338079cD4b4f7D0981f4eE7477D49f";
        // TODO: Remove hardcoded private Key
        String myPrivateKey = "d458a482cb2d7532aab8f76994a32351d5190bc08d661636690fae7272efeaac";
        Credentials creds = Credentials.create(myPrivateKey);//localEtherAccount);
        smartContract = SmartContractDSAProject.load(contractAddress, smartContractWeb3, creds, new DefaultGasProvider());

        try {
            if (!smartContract.isValid()) {
                System.out.println("Contract not valid!");
            } else {
                System.out.println("Successfull");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeGasProvider() {
        gasProvider = new ContractGasProvider() {
            @Override
            public BigInteger getGasPrice(String s) {
                return new BigInteger(String.valueOf(94600000));//DefaultGasProvider.GAS_PRICE;
            }

            @Override
            public BigInteger getGasPrice() {
                return new BigInteger(String.valueOf(94600000));//DefaultGasProvider.GAS_PRICE;
            }

            @Override
            public BigInteger getGasLimit(String s) {
                return new BigInteger(String.valueOf(100000000));//DefaultGasProvider.GAS_LIMIT;
            }

            @Override
            public BigInteger getGasLimit() {
                return new BigInteger(String.valueOf(100000000));// DefaultGasProvider.GAS_LIMIT;
            }
        };
    }


    public void printClientVersions() {
        try {
            Web3ClientVersion localClientVersion = localWeb3.web3ClientVersion().send();
            //Web3ClientVersion remoteClientVersion = remoteWeb3.web3ClientVersion().send();
            System.out.println("Local client version: " + localClientVersion.getWeb3ClientVersion());
            //System.out.println("Remote client version: " + remoteClientVersion.getWeb3ClientVersion());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BigInteger getGasPrice() {
        try {
            EthGasPrice price = localWeb3.ethGasPrice().send();
            return price.getGasPrice();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new BigInteger(String.valueOf(0));
    }

    public void printGasPrice() {
        try {
            EthGasPrice price = localWeb3.ethGasPrice().send();
            System.out.println("Gas price: " + DefaultGasProvider.GAS_PRICE);
            System.out.println("Gas limit: " + DefaultGasProvider.GAS_LIMIT);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
