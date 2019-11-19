package hsr.dsa.ethereum;

import SmartContractDSAProject.SmartContractDSAProject;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
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
    private String testNetAddress = "https://rinkeby.infura.io/";
    private String contractAddress;

    private Web3j localWeb3;
    private Web3j remoteWeb3;

    BigInteger gambleAmount;

    public BlockchainHandler(String localEtherAccount, String remoteEtherAccount) {
        this.localEtherAccount = localEtherAccount;
        this.remoteEtherAccount = remoteEtherAccount;

        localWeb3 = Web3j.build(new HttpService(testNetAddress + localEtherAccount));
        remoteWeb3 = Web3j.build(new HttpService(testNetAddress + remoteEtherAccount));

        printClientVersions();
        printGasPrice();

        System.out.println("Successfull connected!");

        System.out.println("Your Balance: " + getBalanceFromAccount(localEtherAccount) + " WEI");
        System.out.println("Enemys Balance: " + getBalanceFromAccount(remoteEtherAccount) + " WEI");

        initializeGasProvider();

        //deploySmartContract();
        getAlreadyDeployedSmartContract();
        System.out.println("Smart contract deployed");

        // TODO: Remove this
        testTransaction();
    }

    private void testTransaction() {

        Admin web3j = Admin.build(new HttpService());
        //PersonalUnlockAccount myAccountUnlocked = web3j.personalUnlockAccount(localEtherAccount, "Passwort").send();

       /* if (myAccountUnlocked.accountUnlocked()) {
            Transaction transaction = Transaction.createEtherTransaction(localEtherAccount, new BigInteger(String.valueOf(0)), DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT, remoteEtherAccount, new BigInteger(String.valueOf(1000)));
            //EthSendTransaction transactionResponse = parity.ethSen
                    // https://web3j.readthedocs.io/en/latest/transactions.html
        }*/





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
            smartContract.startTransaction(remoteEtherAccount, gambleAmount).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*private void invokeContractFunction() {
        TransactionReceipt receipt = null;
        try {
            receipt = smartContract.testFunction(new BigInteger(String.valueOf(2))).send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String txHash = receipt.getTransactionHash();
    }*/

    private void deploySmartContract() {
        // TODO: private Key hardcoded
        String myPrivateKey = "d458a482cb2d7532aab8f76994a32351d5190bc08d661636690fae7272efeaac";
        Credentials creds = Credentials.create(myPrivateKey);

        try {
            // TODO: Contract address: 0xB3e261933b5AEAc3BF4b15E37e14811C4287431A
            smartContract = SmartContractDSAProject.deploy(localWeb3, creds, gasProvider).send();

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
        contractAddress = "0xB3e261933b5AEAc3BF4b15E37e14811C4287431A";
        Credentials creds = Credentials.create(localEtherAccount);
        smartContract = SmartContractDSAProject.load(contractAddress, localWeb3, creds, gasProvider);

        try {
            if (!smartContract.isValid()) {
                System.out.println("Contract not valid!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeGasProvider() {
        gasProvider = new ContractGasProvider() {
            @Override
            public BigInteger getGasPrice(String s) {
                return DefaultGasProvider.GAS_PRICE;
            }

            @Override
            public BigInteger getGasPrice() {
                return DefaultGasProvider.GAS_PRICE;
            }

            @Override
            public BigInteger getGasLimit(String s) {
                return DefaultGasProvider.GAS_LIMIT;
            }

            @Override
            public BigInteger getGasLimit() {
                return DefaultGasProvider.GAS_LIMIT;
            }
        };
    }


    public void printClientVersions() {
        try {
            Web3ClientVersion localClientVersion = localWeb3.web3ClientVersion().send();
            Web3ClientVersion remoteClientVersion = remoteWeb3.web3ClientVersion().send();
            System.out.println("Local client version: " + localClientVersion.getWeb3ClientVersion());
            System.out.println("Remote client version: " + remoteClientVersion.getWeb3ClientVersion());
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
            System.out.println("Gas price: " + price.getGasPrice());
            System.out.println("Gas limit: " + DefaultGasProvider.GAS_PRICE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
